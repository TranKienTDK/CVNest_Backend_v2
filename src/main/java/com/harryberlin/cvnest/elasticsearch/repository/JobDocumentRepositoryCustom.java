package com.harryberlin.cvnest.elasticsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobDocumentRepositoryCustom {
    Page<String> searchJobs(String title, String contract, String experienceYear,
                            String salaryRange, String jobType, String level,
                            List<String> skillIds, Pageable pageable);
}
