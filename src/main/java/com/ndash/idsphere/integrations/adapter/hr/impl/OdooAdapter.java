package com.ndash.idsphere.integrations.adapter.hr.impl;

import com.ndash.idsphere.integrations.adapter.hr.HrAdapter;
import com.ndash.idsphere.integrations.domain.enums.ExternalSource;
import com.ndash.idsphere.integrations.dto.hr.HrEmployee;
import com.ndash.idsphere.integrations.service.odoo.OdooEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OdooAdapter implements HrAdapter {

    private final OdooEmployeeService client;

    @Override
    public List<HrEmployee> getEmployees() {
        return client.getEmployees();
    }

    @Override
    public ExternalSource getSource() {
        return ExternalSource.ODOO;
    }
}
