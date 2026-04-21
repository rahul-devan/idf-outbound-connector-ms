package com.ndash.idsphere.integrations.dto.hr;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HrEmployee {

    private Integer id;
    private String name;

    @JsonAlias("work_email")
    private String email;

    @JsonAlias("job_title")
    private String jobTitle;

    @JsonAlias("department_id")
    private List<Object> department;

    @JsonAlias("parent_id")
    private List<Object> manager;

    public Integer getDepartmentId() {
        if (department != null && !department.isEmpty()) {
            Object value = department.get(0);
            if (value instanceof Integer) {
                return (Integer) value;
            }
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }
        return null;
    }

    public String getDepartmentName() {
        if (department != null && department.size() > 1) {
            Object value = department.get(1);
            return value != null ? value.toString() : null;
        }
        return null;
    }

    public Integer getManagerId() {
        if (manager != null && !manager.isEmpty()) {
            Object value = manager.get(0);
            if (value instanceof Integer) {
                return (Integer) value;
            }
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }
        return null;
    }

    public String getManagerName() {
        if (manager != null && manager.size() > 1) {
            Object value = manager.get(1);
            return value != null ? value.toString() : null;
        }
        return null;
    }
}
