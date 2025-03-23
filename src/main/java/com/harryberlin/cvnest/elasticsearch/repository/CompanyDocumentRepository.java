package com.harryberlin.cvnest.elasticsearch.repository;

import com.harryberlin.cvnest.elasticsearch.document.CompanyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyDocumentRepository extends ElasticsearchRepository<CompanyDocument, String>, CompanyDocumentRepositoryCustom {

}
