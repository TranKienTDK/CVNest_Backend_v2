package com.harryberlin.cvnest.mapper.company;

import com.harryberlin.cvnest.dto.response.CompanyResponse;
import com.harryberlin.cvnest.elasticsearch.document.CompanyDocument;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyDocumentMapper {
    CompanyResponse toResponse(CompanyDocument companyDocument);
    List<CompanyResponse> toResponses(List<CompanyDocument> companyDocumentList);
}
