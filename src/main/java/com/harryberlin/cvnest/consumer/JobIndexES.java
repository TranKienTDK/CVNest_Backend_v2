package com.harryberlin.cvnest.consumer;

import com.harryberlin.cvnest.domain.Job;
import com.harryberlin.cvnest.domain.Skill;
import com.harryberlin.cvnest.elasticsearch.document.CompanyDocument;
import com.harryberlin.cvnest.elasticsearch.document.JobDocument;
import com.harryberlin.cvnest.elasticsearch.repository.CompanyDocumentRepository;
import com.harryberlin.cvnest.elasticsearch.repository.JobDocumentRepository;
import com.harryberlin.cvnest.event.job.CreateJobEvent;
import com.harryberlin.cvnest.event.job.DeleteJobEvent;
import com.harryberlin.cvnest.event.job.UpdateJobEvent;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.repository.JobRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobIndexES {

    private final JobRepository jobRepository;
    private final JobDocumentRepository jobDocumentRepository;
    private final CompanyDocumentRepository companyDocumentRepository;

    @Async
    @EventListener(classes = CreateJobEvent.class)
    public void index(CreateJobEvent event ) {
        Job job = this.jobRepository.findById(event.getId())
                .orElseThrow(() -> new BaseException(Error.JOB_NOT_FOUND));

        JobDocument jobDocument = JobDocument.builder()
                .id(event.getId())
                .title(job.getTitle())
                .contract(job.getContract())
                .jobType(job.getJobType())
                .salary(job.getSalary())
                .experienceYear(job.getExperienceYear())
                .level(job.getLevel())
                .skillIds(job.getSkills().stream().map(Skill::getId).toList())
                .companyId(job.getCompany().getId())
                .build();

        this.jobDocumentRepository.save(jobDocument);

        CompanyDocument companyDocument = this.companyDocumentRepository.findById(job.getCompany().getId())
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));
        List<String> currentJobIds = companyDocument.getJobIds();
        List<String> updatedJobIds = currentJobIds != null ? new ArrayList<>(currentJobIds) : new ArrayList<>();

        if (!updatedJobIds.contains(job.getId())) {
            updatedJobIds.add(job.getId());
            companyDocument.setJobIds(updatedJobIds);
            this.companyDocumentRepository.save(companyDocument);
        }
    }

    @Async
    @EventListener(classes = UpdateJobEvent.class)
    public void updateIndex(UpdateJobEvent event ) {
        Job job = this.jobRepository.findById(event.getId())
                .orElseThrow(() -> new BaseException(Error.JOB_NOT_FOUND));

        JobDocument jobDocument = this.jobDocumentRepository.findById(event.getId())
                .orElseThrow(() -> new BaseException(Error.JOB_NOT_FOUND));

        jobDocument.setTitle(job.getTitle());
        jobDocument.setContract(job.getContract());
        jobDocument.setJobType(job.getJobType());
        jobDocument.setSalary(job.getSalary());
        jobDocument.setExperienceYear(job.getExperienceYear());
        jobDocument.setLevel(job.getLevel());
        jobDocument.setSkillIds(job.getSkills().stream().map(Skill::getId).toList());
        jobDocument.setCompanyId(job.getCompany().getId());

        this.jobDocumentRepository.save(jobDocument);
    }

    @Async
    @EventListener(classes = DeleteJobEvent.class)
    public void deleteIndex(DeleteJobEvent event ) {
        JobDocument jobDocument = this.jobDocumentRepository.findById(event.getId())
                .orElseThrow(() -> new BaseException(Error.JOB_NOT_FOUND));

        String companyId = jobDocument.getCompanyId();
        this.jobDocumentRepository.delete(jobDocument);

        CompanyDocument companyDocument = this.companyDocumentRepository.findById(companyId)
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));

        if (companyDocument != null && companyDocument.getJobIds() != null) {
            List<String> updatedJobIds = new ArrayList<>(companyDocument.getJobIds());
            updatedJobIds.removeIf(id -> id.equals(event.getId()));
            companyDocument.setJobIds(updatedJobIds);
            this.companyDocumentRepository.save(companyDocument);
        }
    }
}
