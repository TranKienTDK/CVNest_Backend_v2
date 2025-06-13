package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, String> {
    
    Optional<Evaluation> findByCvId(String cvId);
    
    Optional<Evaluation> findByCvIdAndJobId(String cvId, String jobId);
    
    List<Evaluation> findAllByCvId(String cvId);
}
