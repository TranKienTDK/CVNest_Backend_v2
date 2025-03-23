package com.harryberlin.cvnest.util.listener;

import com.harryberlin.cvnest.domain.Job;
import com.harryberlin.cvnest.elasticsearch.document.JobDocument;
import com.harryberlin.cvnest.elasticsearch.repository.JobDocumentRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobEntityListener {
    @Autowired
    private JobDocumentRepository jobDocumentRepository;

    @PostPersist
    @PostUpdate
    public void afterSave(Job job) {
        JobDocument jobDocument = JobDocument.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .type(job.getType())
                .startDate(job.getStartDate())
                .endDate(job.getEndDate())
                .isActive(job.isActive())
                .startSalary(job.getStartSalary())
                .endDate(job.getEndDate())
                .companyName(job.getCompany().getName())
                .build();

        this.jobDocumentRepository.save(jobDocument);
    }

    @PostRemove
    public void afterDelete(Job job) {
        this.jobDocumentRepository.deleteById(job.getId());
    }

}
