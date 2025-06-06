package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, String> {

}
