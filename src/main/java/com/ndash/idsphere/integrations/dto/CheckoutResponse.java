package com.ndash.idsphere.integrations.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CheckoutResponse {

    private UUID checkoutId;
    private String processType;
    private Long applicationId;
    private Long userId;
    private Long createdByUserId;
    private LocalDateTime createdTime;
    private boolean processed;
    private String remarks;
    private String applicationName;
    private String requestedFor;
}
