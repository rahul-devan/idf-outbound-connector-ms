package com.ndash.idsphere.integrations.dto.hr;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HrEmployee {

    private Integer id;
    private String name;
    private String email;
    private String jobTitle;

    private Integer departmentId;
    private String departmentName;
}
