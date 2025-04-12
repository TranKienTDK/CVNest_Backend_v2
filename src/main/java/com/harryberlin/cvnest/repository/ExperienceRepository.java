package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, String> {

}
