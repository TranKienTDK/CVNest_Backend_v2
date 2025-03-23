package com.harryberlin.cvnest.elasticsearch.repository;

import com.harryberlin.cvnest.elasticsearch.document.JobDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDocumentRepository extends ElasticsearchRepository<JobDocument, String> {
}
