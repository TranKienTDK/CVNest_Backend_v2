package com.harryberlin.cvnest.mapper.job;

import com.harryberlin.cvnest.domain.Job;
import com.harryberlin.cvnest.dto.request.JobCreateRequest;
import com.harryberlin.cvnest.dto.response.JobResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobMapper {

    Job toEntity(JobCreateRequest request);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "companyId", source = "company.id")
    JobResponse toResponse(Job job);
}
