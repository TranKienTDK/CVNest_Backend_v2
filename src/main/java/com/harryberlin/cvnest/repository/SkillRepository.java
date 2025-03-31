package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, String> {
    Optional<Skill> findByName(String name);
}
