package com.harryberlin.cvnest.elasticsearch.repository;

import com.harryberlin.cvnest.elasticsearch.document.CompanyDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyDocumentRepositoryCustom {
    Page<CompanyDocument> findCompanyDocumentByQuery(String name, String address, String industry, Pageable pageable);
}
