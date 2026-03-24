package com.ndash.idsphere.integrations.dto.odoo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeResponse {

    private Integer id;
    private String name;
    private String email;
    private String jobTitle;

    private Integer departmentId;
    private String departmentName;
}
