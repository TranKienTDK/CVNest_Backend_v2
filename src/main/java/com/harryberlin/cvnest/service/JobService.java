package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.Company;
import com.harryberlin.cvnest.domain.Job;
import com.harryberlin.cvnest.domain.Skill;
import com.harryberlin.cvnest.dto.request.JobCreateRequest;
import com.harryberlin.cvnest.dto.request.JobUpdateRequest;
import com.harryberlin.cvnest.dto.response.JobResponse;
import com.harryberlin.cvnest.elasticsearch.repository.impl.JobDocumentRepositoryCustomImpl;
import com.harryberlin.cvnest.event.company.UpdateCompanyEvent;
import com.harryberlin.cvnest.event.job.CreateJobEvent;
import com.harryberlin.cvnest.event.job.DeleteJobEvent;
import com.harryberlin.cvnest.event.job.UpdateJobEvent;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.mapper.job.JobMapper;
import com.harryberlin.cvnest.repository.CompanyRepository;
import com.harryberlin.cvnest.repository.JobRepository;
import com.harryberlin.cvnest.repository.SkillRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobService {
    JobMapper jobMapper;
    SkillRepository skillRepository;
    JobRepository jobRepository;
    CompanyRepository companyRepository;
    JobDocumentRepositoryCustomImpl jobDocumentRepositoryCustom;

    private final ApplicationEventPublisher applicationEventPublisher;

    public JobResponse createJob(JobCreateRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));

        Job job = this.jobMapper.toEntity(request);
        job.setCompany(company);

        List<Skill> skillList = this.skillRepository.findAllById(request.getSkillIds());
        if (skillList.size() != request.getSkillIds().size()) {
            throw new BaseException(Error.SKILL_NOT_FOUND);
        }
        job.setSkills(skillList);
        JobResponse response = this.jobMapper.toResponse(jobRepository.save(job));
        response.setSkillNames(job.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toList()));

        CreateJobEvent event = new CreateJobEvent(job.getId());
        applicationEventPublisher.publishEvent(event);

        return response;
    }

    public Page<JobResponse> getAllJob(Pageable pageable) {
        Page<Job> jobs = this.jobRepository.findAll(pageable);
        return jobs.map(this.jobMapper::toResponse);
    }

    public JobResponse getJobById(String id) {
        if (this.jobRepository.findById(id).isEmpty()) {
            throw new BaseException(Error.JOB_NOT_FOUND);
        }
        return this.jobMapper.toResponse(this.jobRepository.findById(id).get());
    }

    public JobResponse updateJob(JobUpdateRequest request) {
        Job jobDB = this.jobRepository.findById(request.getId())
                .orElseThrow(() -> new BaseException(Error.JOB_NOT_FOUND));

        jobDB.setTitle(request.getTitle());
        jobDB.setContract(request.getContract());
        jobDB.setDescription(request.getDescription());
        jobDB.setStartDate(request.getStartDate());
        jobDB.setEndDate(request.getEndDate());
        jobDB.setActive(request.isActive());
        jobDB.setSalary(request.getSalary());

        JobResponse response = this.jobMapper.toResponse(this.jobRepository.save(jobDB));

        UpdateJobEvent event = new UpdateJobEvent(jobDB.getId());
        applicationEventPublisher.publishEvent(event);

        return response;
    }

    @Transactional
    public void deleteJob(String id) {
        Job job = this.jobRepository.findById(id)
                .orElseThrow(() -> new BaseException(Error.JOB_NOT_FOUND));

        Company company = job.getCompany();
        if (company != null && company.getJobs() != null) {
            company.getJobs().remove(job);
            companyRepository.save(company);

            UpdateCompanyEvent companyEvent = new UpdateCompanyEvent(company.getId());
            applicationEventPublisher.publishEvent(companyEvent);
        }

        this.jobRepository.deleteById(id);

        DeleteJobEvent event = new DeleteJobEvent(id);
        applicationEventPublisher.publishEvent(event);
    }

    public Page<JobResponse> searchJobs(String title, String contract, String jobType, String salaryRange,
                                        String experienceYear, String level, List<String> skillIds,
                                        Pageable pageable) {
        Page<String> jobIdsPage = this.jobDocumentRepositoryCustom.searchJobs(
                title, contract, experienceYear, salaryRange, jobType, level, skillIds, pageable);
        if (jobIdsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<String> jobIds = jobIdsPage.getContent();
        List<Job> jobs = this.jobRepository.findAllByIdIn(jobIds);

        Map<String, Job> jobMap = jobs.stream()
                .collect(Collectors.toMap(Job::getId, Function.identity()));

        List<JobResponse> jobResponses = jobIds.stream()
                .map(jobMap::get)
                .filter(Objects::nonNull)
                .map(job -> {
                    JobResponse response = this.jobMapper.toResponse(job);
                    if (job.getSkills() != null) {
                        response.setSkillNames(job.getSkills().stream()
                                .map(Skill::getName)
                                .collect(Collectors.toList()));
                    }
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(jobResponses, pageable, jobIdsPage.getTotalElements());
    }

}
