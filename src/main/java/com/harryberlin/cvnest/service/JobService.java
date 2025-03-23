package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.Company;
import com.harryberlin.cvnest.domain.Job;
import com.harryberlin.cvnest.dto.request.JobCreateRequest;
import com.harryberlin.cvnest.dto.request.JobUpdateRequest;
import com.harryberlin.cvnest.dto.response.JobResponse;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.mapper.job.JobMapper;
import com.harryberlin.cvnest.repository.CompanyRepository;
import com.harryberlin.cvnest.repository.JobRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobService {
    JobMapper jobMapper;
    JobRepository jobRepository;
    CompanyRepository companyRepository;

    public JobResponse createJob(JobCreateRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new BaseException(Error.COMPANY_NOT_FOUND));
        Job job = this.jobMapper.toEntity(request);
        job.setCompany(company);
        JobResponse response = this.jobMapper.toResponse(jobRepository.save(job));
        response.setActive(true);
        return response;
    }

    public List<JobResponse> getAllJob() {
        List<Job> jobs = this.jobRepository.findAll();
        return jobs.stream().map(this.jobMapper::toResponse).collect(Collectors.toList());
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
        jobDB.setType(request.getType());
        jobDB.setDescription(request.getDescription());
        jobDB.setStartDate(request.getStartDate());
        jobDB.setEndDate(request.getEndDate());
        jobDB.setActive(request.isActive());
        jobDB.setStartSalary(request.getStartSalary());
        jobDB.setEndSalary(request.getEndSalary());

        return this.jobMapper.toResponse(this.jobRepository.save(jobDB));
    }

    public void deleteJob(String id) {
        if (this.jobRepository.findById(id).isEmpty()) {
            throw new BaseException(Error.JOB_NOT_FOUND);
        }
        this.jobRepository.deleteById(id);
    }
}
