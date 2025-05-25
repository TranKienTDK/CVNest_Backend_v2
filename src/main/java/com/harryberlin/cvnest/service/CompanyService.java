package com.harryberlin.cvnest.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harryberlin.cvnest.domain.Company;
import com.harryberlin.cvnest.dto.request.CompanyCreateRequest;
import com.harryberlin.cvnest.dto.request.CompanyUpdateRequest;
import com.harryberlin.cvnest.dto.response.CompanyResponse;
import com.harryberlin.cvnest.elasticsearch.repository.CompanyDocumentRepository;
import com.harryberlin.cvnest.event.company.CreateCompanyEvent;
import com.harryberlin.cvnest.event.company.DeleteCompanyEvent;
import com.harryberlin.cvnest.event.company.ImportCompanyListEvent;
import com.harryberlin.cvnest.event.company.UpdateCompanyEvent;
import com.harryberlin.cvnest.event.job.DeleteJobEvent;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.mapper.company.CompanyMapper;
import com.harryberlin.cvnest.repository.CompanyRepository;
import com.harryberlin.cvnest.util.constant.Error;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyMapper companyMapper;
    CompanyRepository companyRepository;
    CompanyDocumentRepository companyDocumentRepository;

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CompanyResponse createCompany(CompanyCreateRequest request) {
        Company company = this.companyRepository.save(this.companyMapper.toEntity(request));

        CreateCompanyEvent event = new CreateCompanyEvent(company.getId());
        this.applicationEventPublisher.publishEvent(event);

        return this.companyMapper.toResponse(company);
    }

    public Page<CompanyResponse> getAllCompanies(Pageable pageable) {
        Page<Company> companiesPage = this.companyRepository.findAll(pageable);
        return companiesPage.map(this.companyMapper::toResponse);
    }

    public CompanyResponse getCompanyById(String id) {
        return this.companyRepository.findById(id)
                .map(this.companyMapper::toResponse)
                .orElse(null);
    }

    public CompanyResponse updateCompany(CompanyUpdateRequest request) {
        Company company = this.companyRepository.findById(request.getId())
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));

        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setWebsite(request.getWebsite());
        company.setAvatar(request.getAvatar());
        company.setDescription(request.getDescription());

        CompanyResponse response = this.companyMapper.toResponse(this.companyRepository.save(company));

        UpdateCompanyEvent event = new UpdateCompanyEvent(company.getId());
        this.applicationEventPublisher.publishEvent(event);

        return response;
    }

    @Transactional
    public void deleteCompany(String id) {
        Company company = this.companyRepository.findById(id)
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));

        if (company.getJobs() != null && !company.getJobs().isEmpty()) {
            company.getJobs().forEach(job -> {
                DeleteJobEvent jobEvent = new DeleteJobEvent(job.getId());
                this.applicationEventPublisher.publishEvent(jobEvent);
            });
        }

        this.companyRepository.deleteById(id);

        DeleteCompanyEvent companyEvent = new DeleteCompanyEvent(id);
        this.applicationEventPublisher.publishEvent(companyEvent);
    }

    // ELASTIC SEARCH
    public Page<CompanyResponse> searchCompany(String name, String address, String industry, Pageable pageable) {
        Specification<Company> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (address != null && !address.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%"));
            }
            if (industry != null && !industry.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("industry")), industry.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Company> companyPage = companyRepository.findAll(spec, pageable);

        return companyPage.map(companyMapper::toResponse);
    }

    // IMPORT DATA FROM JSON
    @Transactional
    public void importCompaniesFromJson() throws IOException {
        Resource resource = new ClassPathResource("data/companies.json");

        List<CompanyCreateRequest> companyRequestList = this.objectMapper.readValue(resource.getFile(),
                new TypeReference<List<CompanyCreateRequest>>() {});

        Set<String> existingCompanyNames = new HashSet<>();
        List<CompanyCreateRequest> filteredRequestList = companyRequestList.stream()
                .filter(companyRequest -> existingCompanyNames.add(companyRequest.getName()))
                .toList();

        List<Company> companyList = filteredRequestList.stream()
                .map(this.companyMapper::toEntity)
                .toList();

        List<Company> existingCompanies = this.companyRepository.findAllByNameIn(
                companyList.stream().map(Company::getName).collect(Collectors.toList()));

        List<Company> newCompanies = companyList.stream()
                .filter(company -> existingCompanies.stream().noneMatch(existing -> existing.getName().equals(company.getName())))
                .collect(Collectors.toList());

        List<Company> savedCompanies = this.companyRepository.saveAll(newCompanies);

        List<CreateCompanyEvent> events = savedCompanies.stream()
                .map(company -> new CreateCompanyEvent(company.getId()))
                .toList();

        ImportCompanyListEvent event = new ImportCompanyListEvent(events);
        this.applicationEventPublisher.publishEvent(event);
    }
}
