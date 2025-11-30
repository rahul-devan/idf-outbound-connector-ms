package com.ndash.idsphere.integrations.service.impl;

import com.ndash.idsphere.integrations.domain.Application;
import com.ndash.idsphere.integrations.domain.Checkout;
import com.ndash.idsphere.integrations.domain.User;
import com.ndash.idsphere.integrations.dto.CheckoutRequest;
import com.ndash.idsphere.integrations.dto.CheckoutResponse;
import com.ndash.idsphere.integrations.exception.CheckoutInProgressException;
import com.ndash.idsphere.integrations.exception.ResourceNotFoundException;
import com.ndash.idsphere.integrations.mapper.CheckoutMapper;
import com.ndash.idsphere.integrations.repositories.ApplicationRepository;
import com.ndash.idsphere.integrations.repositories.CheckoutRepository;
import com.ndash.idsphere.integrations.repositories.UserRepository;
import com.ndash.idsphere.integrations.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final CheckoutMapper checkoutMapper;

    @Override
    public CheckoutResponse addToCheckout(CheckoutRequest request, Long createdBy) {
        Application app = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with ID: " + request.getApplicationId()));

        User targetUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + request.getUserId()));

        User creatorUser = userRepository.findById(createdBy)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Creator user not found with ID: " + createdBy));


        checkoutRepository
                .findByUserIdAndApplicationIdAndProcessTypeAndProcessedFalse(
                        request.getUserId(),
                        request.getApplicationId(),
                        request.getProcessType()
                )
                .ifPresent(existing -> {
                    throw new CheckoutInProgressException(
                            "A request with this process type is already under processing. Checkout ID: "
                                    + existing.getCheckoutId()
                    );
                });


        Checkout checkout = new Checkout();
        checkout.setProcessType(request.getProcessType());
        checkout.setApplication(app);
        checkout.setUser(targetUser);
        checkout.setCreatedBy(creatorUser);
        checkout.setRemarks(request.getRemarks());

        Checkout saved = checkoutRepository.save(checkout);
        return checkoutMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteAllCheckouts() {
        checkoutRepository.deleteAll();
    }

    @Override
    public List<CheckoutResponse> getPendingCheckouts() {
        return checkoutRepository.findAllByProcessedFalse()
                .stream()
                .map(checkoutMapper::toDto)
                .toList();
    }

    @Override
    public void markProcessedTrue(UUID checkoutId) {
        Optional<Checkout> optionalCheckout = checkoutRepository.findById(checkoutId);
        if (optionalCheckout.isPresent()) {
            Checkout checkout = optionalCheckout.get();
            checkout.setProcessed(true);
            checkoutRepository.saveAndFlush(checkout);
        }
    }
}
