package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.SavedCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedCVRepository extends JpaRepository<SavedCV, String> {
    SavedCV findByCvIdAndHrId(String cvId, String hrId);

    @Query("SELECT s.cvId FROM saved_cv s WHERE s.hrId = :hrId")
    List<String> getAllCvIdByHrId(@Param("hrId") String hrId);
}
