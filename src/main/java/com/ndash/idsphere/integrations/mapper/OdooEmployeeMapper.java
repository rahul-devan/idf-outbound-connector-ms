package com.ndash.idsphere.integrations.mapper;


import com.ndash.idsphere.integrations.dto.odoo.EmployeeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface OdooEmployeeMapper {

    @Mapping(target = "id", expression = "java((Integer) source.get(\"id\"))")
    @Mapping(target = "name", expression = "java(asString(source.get(\"name\")))")
    @Mapping(target = "email", expression = "java(asString(source.get(\"work_email\")))")
    @Mapping(target = "jobTitle", expression = "java(asString(source.get(\"job_title\")))")
    @Mapping(target = "departmentId", expression = "java(extractDepartmentId(source.get(\"department_id\")))")
    @Mapping(target = "departmentName", expression = "java(extractDepartmentName(source.get(\"department_id\")))")
    EmployeeResponse map(Map<String, Object> source);

    List<EmployeeResponse> mapList(List<Map<String, Object>> source);

    // ✅ SAFE STRING CONVERSION
    default String asString(Object value) {
        if (value == null || value instanceof Boolean) {
            return null;
        }
        return value.toString();
    }

    // ✅ SAFE DEPARTMENT ID
    default Integer extractDepartmentId(Object deptObj) {
        if (deptObj instanceof List<?> list && list.size() == 2) {
            return (Integer) list.get(0);
        }
        return null;
    }

    // ✅ SAFE DEPARTMENT NAME
    default String extractDepartmentName(Object deptObj) {
        if (deptObj instanceof List<?> list && list.size() == 2) {
            return (String) list.get(1);
        }
        return null;
    }
}