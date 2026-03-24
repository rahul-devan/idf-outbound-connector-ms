package com.ndash.idsphere.integrations.service.odoo;


import com.ndash.idsphere.integrations.client.odoo.OdooJsonRpcClient;
import com.ndash.idsphere.integrations.dto.odoo.EmployeeResponse;
import com.ndash.idsphere.integrations.mapper.OdooEmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OdooEmployeeService {

    private final OdooJsonRpcClient client;
    private final OdooEmployeeMapper mapper;

    public List<EmployeeResponse> getEmployees() {

        try {
            Integer uid = client.authenticate();

            Object result = client.execute(
                    uid,
                    "hr.employee",
                    "search_read",
                    List.of(),
                    Map.of(
                            "fields",
                            List.of(
                                    "name",
                                    "work_email",
                                    "job_title",
                                    "department_id"
                            )
                    )
            );
            List<Map<String, Object>> employees =
                    (List<Map<String, Object>>) result;

            return mapper.mapList(employees);
        }catch (Exception e) {
            log.error("Error fetching employees from Odoo", e);
            throw new RuntimeException("Failed to fetch employees from Odoo", e);
        }
    }
}
