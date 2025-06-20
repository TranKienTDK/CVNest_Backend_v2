package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.CV;
import com.harryberlin.cvnest.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CVRepository extends JpaRepository<CV, String> {
    Page<CV> findByUser(User user, Pageable pageable);
    Optional<CV> findByIdAndUser(String id, User user);
    List<CV> findByUserId(String userId);

    @Query("SELECT c FROM cvs c WHERE c.isDefault = true AND c.user.id = :userId")
    CV findByDefaultIsTrue(@Param("userId") String userId);

    List<CV> findAllByIdIn(List<String> ids);
    List<CV> findByIsDefaultTrue();
}
