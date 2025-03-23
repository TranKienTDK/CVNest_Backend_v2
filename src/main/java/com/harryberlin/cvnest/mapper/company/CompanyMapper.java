package com.harryberlin.cvnest.mapper.company;

import com.harryberlin.cvnest.domain.Company;
import com.harryberlin.cvnest.dto.request.CompanyCreateRequest;
import com.harryberlin.cvnest.dto.response.CompanyResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    Company toEntity(CompanyCreateRequest request);
    CompanyResponse toResponse(Company company);
    List<CompanyResponse> toResponseList(List<Company> companies);
}
