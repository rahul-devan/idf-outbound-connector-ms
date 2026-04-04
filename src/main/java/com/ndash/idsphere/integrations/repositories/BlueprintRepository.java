package com.ndash.idsphere.integrations.repositories;

import com.ndash.idsphere.integrations.domain.Blueprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlueprintRepository extends JpaRepository<Blueprint, Long> {

    @Query("""
        SELECT b FROM Blueprint b
        JOIN b.jobTitles jt
        WHERE jt.id = :jobTitleId
    """)
    List<Blueprint> findByJobTitleId(Long jobTitleId);
}
