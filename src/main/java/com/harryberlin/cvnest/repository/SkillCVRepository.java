package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.SkillCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillCVRepository extends JpaRepository<SkillCV, String> {

}
