package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.CV;
import com.harryberlin.cvnest.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CVRepository extends JpaRepository<CV, String> {
    Page<CV> findByUser(User user, Pageable pageable);
    Optional<CV> findByIdAndUser(String id, User user);
}
