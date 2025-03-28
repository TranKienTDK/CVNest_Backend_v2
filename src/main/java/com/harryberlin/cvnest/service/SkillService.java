package com.harryberlin.cvnest.service;

import com.harryberlin.cvnest.domain.Skill;
import com.harryberlin.cvnest.dto.request.SkillCreateRequest;
import com.harryberlin.cvnest.dto.request.SkillUpdateRequest;
import com.harryberlin.cvnest.dto.response.SkillResponse;
import com.harryberlin.cvnest.exception.BaseException;
import com.harryberlin.cvnest.mapper.skill.SkillMapper;
import com.harryberlin.cvnest.repository.SkillRepository;
import com.harryberlin.cvnest.util.constant.Error;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkillService {
    SkillMapper skillMapper;
    SkillRepository skillRepository;

    public SkillResponse createSkill(SkillCreateRequest request) {
        Skill skill = this.skillMapper.toEntity(request);
        if (this.skillRepository.findByName(skill.getName()) != null) {
            throw new BaseException(Error.SKILL_ALREADY_EXISTED);
        }
        return this.skillMapper.toResponse(this.skillRepository.save(skill));
    }

    public List<SkillResponse> getAllSkills() {
        List<Skill> skills = this.skillRepository.findAll();
        return this.skillMapper.toResponseList(skills);
    }

    public SkillResponse updateSkill(SkillUpdateRequest request) {
        Skill skill = this.skillRepository.findById(request.getId())
                .orElseThrow(() -> new BaseException(Error.SKILL_NOT_FOUND));
        skill.setName(request.getName());
        if (this.skillRepository.findByName(skill.getName()) != null) {
            throw new BaseException(Error.SKILL_ALREADY_EXISTED);
        }
        return this.skillMapper.toResponse(this.skillRepository.save(skill));
    }

    public void deleteSkill(String id) {
        Skill skill = this.skillRepository.findById(id)
                .orElseThrow(() -> new BaseException(Error.SKILL_NOT_FOUND));
        this.skillRepository.delete(skill);
    }
}
