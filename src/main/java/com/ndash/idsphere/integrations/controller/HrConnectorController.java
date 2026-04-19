package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.config.odoo.OdooEmployeeSync;
import com.ndash.idsphere.integrations.dto.hr.HrEmployee;
import com.ndash.idsphere.integrations.service.HrClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/odoo")
@RequiredArgsConstructor
@CrossOrigin("*")
public class HrConnectorController {

    private final HrClientService employeeService;
    private final OdooEmployeeSync employeeSync;

    @GetMapping("/hr/employees")
    public ResponseEntity<List<HrEmployee>> getEmployees() {
        return ResponseEntity.ok(employeeService.getEmployees());
    }


    @PostMapping("/hr/employees/sync")
    public ResponseEntity<String> syncEmployees() {
        employeeSync.sync();
        return ResponseEntity.ok("Employee synchronization completed successfully.");
    }
}