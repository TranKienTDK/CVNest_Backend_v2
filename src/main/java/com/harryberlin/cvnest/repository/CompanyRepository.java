package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String>, JpaSpecificationExecutor<Company> {
    List<Company> findAllByIdIn(List<String> ids);
    List<Company> findAllByNameIn(List<String> names);
}
