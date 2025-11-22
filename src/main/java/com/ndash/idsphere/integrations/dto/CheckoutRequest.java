package com.ndash.idsphere.integrations.dto;

import com.ndash.idsphere.integrations.domain.enums.ProcessType;
import lombok.Data;

@Data
public class CheckoutRequest {

    private ProcessType processType;      // REQUEST / REMOVE
    private Long applicationId;           // FK
    private Long userId;                  // FK
    private String remarks;               // optional
}
