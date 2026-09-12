package com.ndash.idsphere.integrations.repositories;

import com.ndash.idsphere.integrations.domain.User;
import com.ndash.idsphere.integrations.domain.enums.UserSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByAzureId(String azureId);
    Optional<User> findByEmail(String email);
    List<User> findByActiveTrue();
    Page<User> findByUsernameContainingIgnoreCaseAndActiveTrue(String username, Pageable pageable);
    Optional<User> findByExternalId(String externalId);
    List<User> findBySourceAndExternalSource(UserSource source, String externalSource);


}
