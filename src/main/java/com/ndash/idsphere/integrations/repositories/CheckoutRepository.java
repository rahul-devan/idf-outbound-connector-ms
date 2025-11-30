package com.ndash.idsphere.integrations.repositories;

import com.ndash.idsphere.integrations.domain.Checkout;
import com.ndash.idsphere.integrations.domain.enums.ProcessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, UUID> {

    Optional<Checkout> findByUserIdAndApplicationIdAndProcessedFalse(Long userId, Long applicationId);

    Optional<Checkout> findByUserIdAndApplicationIdAndProcessTypeAndProcessedFalse(
            Long userId,
            Long applicationId,
            ProcessType processType
    );


    List<Checkout> findAllByProcessedFalse();


}
