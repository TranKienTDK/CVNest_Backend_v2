package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {

}
