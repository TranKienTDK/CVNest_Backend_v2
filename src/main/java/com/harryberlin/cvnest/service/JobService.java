package com.harryberlin.cvnest.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harryberlin.cvnest.domain.Company;
import com.harryberlin.cvnest.domain.Job;
import com.harryberlin.cvnest.domain.Skill;
import com.harryberlin.cvnest.domain.User;
import com.harryberlin.cvnest.dto.request.JobCreateImportRequest;
import com.harryberlin.cvnest.dto.request.JobCreateRequest;
import com.harryberlin.cvnest.dto.request.JobUpdateRequest;
import com.harryberlin.cvnest.dto.response.JobResponse;
import com.harryberlin.cvnest.elasticsearch.repository.impl.JobDocumentRepositoryCustomImpl;
import com.harryberlin.cvnest.event.company.UpdateCompanyEvent;
import com.harryberlin.cvnest.event.job.CreateJobEvent;
import com.harryberlin.cvnest.event.job.DeleteJobEvent;
import com.harryberlin.cvnest.event.job.ImportJobsEvent;
import com.harryberlin.cvnest.event.job.UpdateJobEvent;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.mapper.job.JobMapper;
import com.harryberlin.cvnest.repository.CompanyRepository;
import com.harryberlin.cvnest.repository.JobRepository;
import com.harryberlin.cvnest.repository.SkillRepository;
import com.harryberlin.cvnest.repository.UserRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
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
    UserRepository userRepository;
    JobDocumentRepositoryCustomImpl jobDocumentRepositoryCustom;

    private final ObjectMapper objectMapper;
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
        Job job = this.jobRepository.findById(id).get();
        JobResponse response = this.jobMapper.toResponse(job);
        response.setSkillNames(job.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toList()));
        return response;
    }

    public JobResponse updateJob(JobUpdateRequest request) {
        Job jobDB = this.jobRepository.findById(request.getId())
                .orElseThrow(() -> new BaseException(Error.JOB_NOT_FOUND));

        jobDB.setTitle(request.getTitle());
        jobDB.setContract(request.getContract());
        jobDB.setJobType(request.getJobType());
        jobDB.setLevel(request.getLevel());
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

    // IMPORT DATA FROM JSON
    @Transactional
    public void importJobsFromJson() throws IOException {
        Resource resource = new ClassPathResource("data/jobs.json");
        List<JobCreateImportRequest> jobRequestList = this.objectMapper.readValue(resource.getFile(),
                new TypeReference<List<JobCreateImportRequest>>() {});

        List<String> jobIds = new ArrayList<>();

        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            throw new BaseException(Error.COMPANY_NOT_FOUND);
        }

        int companyCount = companies.size();

        for (int i = 0; i < jobRequestList.size(); i++) {
            JobCreateImportRequest request = jobRequestList.get(i);

            Company company = companies.get(i % companyCount);
            request.setCompanyId(company.getId());

            Company companyFromDb = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));

            List<Skill> skills = request.getSkills().stream()
                    .map(skillName -> this.skillRepository.findByName(skillName)
                            .orElseGet(() -> this.skillRepository.save(new Skill(skillName))))
                    .toList();

            Job job = this.jobMapper.toEntityImport(request);
            job.setSkills(skills);
            job.setCompany(companyFromDb);
            this.jobRepository.save(job);
            jobIds.add(job.getId());
        }

        if (!jobIds.isEmpty()) {
            ImportJobsEvent event = new ImportJobsEvent(jobIds);
            this.applicationEventPublisher.publishEvent(event);
        }
    }

    // Clean duplicate description
    @Transactional
    public void cleanDuplicateDescription() {
        List<Job> jobs = this.jobRepository.findAll();
        for (Job job : jobs) {
            String original = job.getDescription();
            if (original == null || original.isBlank()) continue;
            String cleaned = removeDuplicateDescription(original);

            if (!original.equals(cleaned)) {
                job.setDescription(cleaned);
                this.jobRepository.save(job);
            }
        }
    }

    private String removeDuplicateDescription(String input) {
        String[] lines = input.split("\\r?\\n");
        Set<String> seen = new LinkedHashSet<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                seen.add(trimmed);
            }
        }

        return String.join("\n", seen);
    }

    public List<JobResponse> getListJobByCompanyId(String companyId) {
        if (this.companyRepository.findById(companyId).isEmpty()) {
            throw new BaseException(Error.COMPANY_NOT_FOUND);
        }
        List<Job> jobs = this.jobRepository.findByCompanyId(companyId);
        return jobs.stream()
                .map(job -> {
                    JobResponse response = this.jobMapper.toResponse(job);
                    response.setSkillNames(job.getSkills().stream()
                            .map(Skill::getName)
                            .collect(Collectors.toList()));
                    return response;
                })
                .toList();
    }

    public List<JobResponse> getListJobByHR(String hrId) {
        User hrUser = this.userRepository.findById(hrId).orElseThrow(
                () -> new BaseException(Error.USER_NOT_FOUND));

        String companyId = hrUser.getCompany().getId();
        List<Job> jobs = this.jobRepository.findByCompanyId(companyId);
        return jobs.stream()
                .map(job -> {
                    JobResponse response = this.jobMapper.toResponse(job);
                    response.setSkillNames(job.getSkills().stream()
                            .map(Skill::getName)
                            .collect(Collectors.toList()));
                    return response;
                })
                .toList();
    }

}
