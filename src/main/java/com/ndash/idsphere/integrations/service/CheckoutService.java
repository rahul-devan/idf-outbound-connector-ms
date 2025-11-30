package com.ndash.idsphere.integrations.service;

import com.ndash.idsphere.integrations.domain.Checkout;
import com.ndash.idsphere.integrations.dto.CheckoutRequest;
import com.ndash.idsphere.integrations.dto.CheckoutResponse;

import java.util.List;
import java.util.UUID;

public interface CheckoutService {
    CheckoutResponse addToCheckout(CheckoutRequest checkoutRequest, Long createdBy);
    void deleteAllCheckouts();
    List<CheckoutResponse> getPendingCheckouts();
    void markProcessedTrue(UUID checkoutId);
}
