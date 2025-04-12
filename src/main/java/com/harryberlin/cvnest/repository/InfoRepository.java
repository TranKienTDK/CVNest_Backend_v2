package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoRepository extends JpaRepository<Info, String> {

}
