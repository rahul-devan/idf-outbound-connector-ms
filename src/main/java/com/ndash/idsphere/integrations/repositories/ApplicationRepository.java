package com.ndash.idsphere.integrations.repositories;

import com.ndash.idsphere.integrations.domain.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByName(String name);
    Page<Application> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
