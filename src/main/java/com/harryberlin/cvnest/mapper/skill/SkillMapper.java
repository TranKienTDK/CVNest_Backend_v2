package com.harryberlin.cvnest.mapper.skill;

import com.harryberlin.cvnest.domain.Skill;
import com.harryberlin.cvnest.dto.request.SkillCreateRequest;
import com.harryberlin.cvnest.dto.response.SkillResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toEntity(SkillCreateRequest request);
    SkillResponse toResponse(Skill skill);
    List<SkillResponse> toResponseList(List<Skill> skills);
}
