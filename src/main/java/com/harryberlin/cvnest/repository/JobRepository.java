package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findAllByIdIn(List<String> ids);
    List<Job> findByCompanyId(String companyId);
}
