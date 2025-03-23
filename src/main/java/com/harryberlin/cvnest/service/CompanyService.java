package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.Company;
import com.harryberlin.cvnest.dto.request.CompanyCreateRequest;
import com.harryberlin.cvnest.dto.request.CompanyUpdateRequest;
import com.harryberlin.cvnest.dto.response.CompanyResponse;
import com.harryberlin.cvnest.elasticsearch.document.CompanyDocument;
import com.harryberlin.cvnest.elasticsearch.repository.CompanyDocumentRepository;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.mapper.company.CompanyDocumentMapper;
import com.harryberlin.cvnest.mapper.company.CompanyMapper;
import com.harryberlin.cvnest.repository.CompanyRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyMapper companyMapper;
    CompanyDocumentMapper companyDocumentMapper;
    CompanyRepository companyRepository;
    CompanyDocumentRepository companyDocumentRepository;

    public CompanyResponse createCompany(CompanyCreateRequest request) {
        Company company = this.companyRepository.save(this.companyMapper.toEntity(request));
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

        return this.companyMapper.toResponse(this.companyRepository.save(company));
    }

    public void deleteCompany(String id) {
        this.companyRepository.deleteById(id);
    }

    // ELASTIC SEARCH
    public Page<CompanyResponse> searchCompany(String name, String address, String industry, Pageable pageable) {
        Page<CompanyDocument> companyDocumentsPage =
                this.companyDocumentRepository.findCompanyDocumentByQuery(name, address, industry, pageable);

        List<CompanyResponse> companyResponses = companyDocumentsPage.getContent().stream()
                .map(this.companyDocumentMapper::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(companyResponses, pageable, companyDocumentsPage.getTotalElements());
    }


}
