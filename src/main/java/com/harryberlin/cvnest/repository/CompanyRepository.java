package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    List<Company> findAllByIdIn(List<String> ids);
}
