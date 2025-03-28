package com.harryberlin.cvnest.consumer;

import com.harryberlin.cvnest.domain.Company;
import com.harryberlin.cvnest.domain.Job;
import com.harryberlin.cvnest.elasticsearch.document.CompanyDocument;
import com.harryberlin.cvnest.elasticsearch.repository.CompanyDocumentRepository;
import com.harryberlin.cvnest.event.company.CreateCompanyEvent;
import com.harryberlin.cvnest.event.company.DeleteCompanyEvent;
import com.harryberlin.cvnest.event.company.UpdateCompanyEvent;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.repository.CompanyRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyIndexES {
    private final CompanyRepository companyRepository;
    private final CompanyDocumentRepository companyDocumentRepository;

    @Async
    @EventListener(CreateCompanyEvent.class)
    public void index(CreateCompanyEvent event) {
        Company company = this.companyRepository.findById(event.getId())
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));

        CompanyDocument companyDocument = CompanyDocument.builder()
                .id(company.getId())
                .name(company.getName())
                .address(company.getAddress())
                .industry(company.getIndustry())
                .jobIds(company.getJobs().stream().map(Job::getId).collect(Collectors.toList()))
                .build();

        this.companyDocumentRepository.save(companyDocument);
    }

    @Async
    @EventListener(UpdateCompanyEvent.class)
    public void updateIndex(UpdateCompanyEvent event) {
        Company company = this.companyRepository.findById(event.getId())
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));

        CompanyDocument companyDocument = this.companyDocumentRepository.findById(event.getId())
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));

        companyDocument.setName(company.getName());
        companyDocument.setAddress(company.getAddress());
        companyDocument.setIndustry(company.getIndustry());
        companyDocument.setJobIds(company.getJobs().stream().map(Job::getId).collect(Collectors.toList()));

        this.companyDocumentRepository.save(companyDocument);
    }

    @Async
    @EventListener(DeleteCompanyEvent.class)
    public void deleteIndex(DeleteCompanyEvent event) {
        CompanyDocument companyDocument = this.companyDocumentRepository.findById(event.getId())
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));

        this.companyDocumentRepository.delete(companyDocument);
    }
}
