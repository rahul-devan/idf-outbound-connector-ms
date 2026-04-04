package com.ndash.idsphere.integrations.repositories;

import com.ndash.idsphere.integrations.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByExternalIdAndExternalSource(String externalId, String externalSource);
}
