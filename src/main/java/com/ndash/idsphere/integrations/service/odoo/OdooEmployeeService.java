package com.ndash.idsphere.integrations.service.odoo;


import com.ndash.idsphere.integrations.hr.odoo.client.OdooJsonRpcClient;
import com.ndash.idsphere.integrations.dto.hr.HrEmployee;
import com.ndash.idsphere.integrations.mapper.HrEmployeeMapper;
import com.ndash.idsphere.integrations.service.HrClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OdooEmployeeService implements HrClientService {

    private final OdooJsonRpcClient client;
    private final HrEmployeeMapper mapper;

    @Override
    public List<HrEmployee> getEmployees() {

        try {
            Integer uid = client.authenticate();

            Object result = client.execute(
                    uid,
                    "hr.employee.public",
                    "search_read",
                    List.of(),
                    Map.of(
                            "fields",
                            List.of(
                                    "name",
                                    "job_title",
                                    "work_email",
                                    "work_phone",
                                    "mobile_phone",
                                    "department_id",
                                    "parent_id"
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
