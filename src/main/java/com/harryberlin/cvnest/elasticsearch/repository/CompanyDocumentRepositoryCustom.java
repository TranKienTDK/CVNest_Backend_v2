package com.harryberlin.cvnest.elasticsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyDocumentRepositoryCustom {
    Page<String> findCompanyDocumentByQuery(String name, String address, String industry, Pageable pageable);
}
