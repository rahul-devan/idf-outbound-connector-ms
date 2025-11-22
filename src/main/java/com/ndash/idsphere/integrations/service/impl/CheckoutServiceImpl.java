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


        checkoutRepository.findByUserIdAndApplicationIdAndProcessedFalse(
                request.getUserId(), request.getApplicationId()
        ).ifPresent(existing -> {
            throw new CheckoutInProgressException(
                    "A checkout request for this user and application is already under processing. " +
                            "Checkout ID: " + existing.getCheckoutId()
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
}
