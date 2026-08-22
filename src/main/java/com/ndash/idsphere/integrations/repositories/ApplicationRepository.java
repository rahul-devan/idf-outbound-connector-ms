package com.ndash.idsphere.integrations.repositories;

import com.ndash.idsphere.integrations.domain.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByName(String name);
    Page<Application> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Application> findByEssentialTrue();
    Optional<Application> findByIntegrationNameIgnoreCase(String integrationName);
    Optional<Application> findByNameIgnoreCase(String name);
}
