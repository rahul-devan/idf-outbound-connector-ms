package com.ndash.idsphere.integrations.mapper;

import com.ndash.idsphere.integrations.dto.hr.HrEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface HrEmployeeMapper {

    @Mapping(target = "id", expression = "java(asInteger(source.get(\"id\")))")
    @Mapping(target = "name", expression = "java(asString(source.get(\"name\")))")
    @Mapping(target = "email", expression = "java(asString(source.get(\"work_email\")))")
    @Mapping(target = "jobTitle", expression = "java(asString(source.get(\"job_title\")))")
    @Mapping(target = "department", expression = "java(asList(source.get(\"department_id\")))")
    @Mapping(target = "manager", expression = "java(asList(source.get(\"parent_id\")))")
    HrEmployee map(Map<String, Object> source);

    List<HrEmployee> mapList(List<Map<String, Object>> source);

    default String asString(Object value) {
        if (value == null || value instanceof Boolean) {
            return null;
        }
        return value.toString();
    }

    default Integer asInteger(Object value) {
        if (value == null || value instanceof Boolean) {
            return null;
        }

        if (value instanceof Integer integer) {
            return integer;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        return null;
    }

    default List<Object> asList(Object value) {
        if (value instanceof List<?> list) {
            return (List<Object>) list;
        }
        return null;
    }
}