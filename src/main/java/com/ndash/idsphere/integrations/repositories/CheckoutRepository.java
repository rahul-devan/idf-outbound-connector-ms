package com.ndash.idsphere.integrations.repositories;

import com.ndash.idsphere.integrations.domain.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    Optional<Checkout> findByUserIdAndApplicationIdAndProcessedFalse(Long userId, Long applicationId);

    List<Checkout> findAllByProcessedFalse();


}
