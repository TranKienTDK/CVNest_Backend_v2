package com.harryberlin.cvnest.util.listener;

import com.harryberlin.cvnest.domain.Company;
import com.harryberlin.cvnest.elasticsearch.document.CompanyDocument;
import com.harryberlin.cvnest.elasticsearch.repository.CompanyDocumentRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyEntityListener {

    @Autowired
    private CompanyDocumentRepository companyDocumentRepository;

    @PostPersist
    @PostUpdate
    public void afterSave(Company company) {
        CompanyDocument companyDocument = CompanyDocument.builder()
                .id(company.getId())
                .name(company.getName())
                .address(company.getAddress())
                .website(company.getWebsite())
                .avatar(company.getAvatar())
                .description(company.getDescription())
                .industry(company.getIndustry())
                .build();

        this.companyDocumentRepository.save(companyDocument);
    }

    @PostRemove
    public void afterDelete(Company company) {
        this.companyDocumentRepository.deleteById(company.getId());
    }
}
