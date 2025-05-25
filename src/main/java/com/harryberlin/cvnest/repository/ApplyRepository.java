package com.harryberlin.cvnest.repository;

import com.harryberlin.cvnest.domain.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, String> {
    List<Apply> findByJobId(String jobId);
    List<Apply> findByCvIdIn(List<String> cvIds);

    @Query(
            "SELECT a " +
                    "FROM applies a, jobs j " +
                    "WHERE a.jobId = j.id " +
                    "  AND j.company.id = :companyId"
    )
    List<Apply> findByCompanyId(@Param("companyId") String companyId);
}
