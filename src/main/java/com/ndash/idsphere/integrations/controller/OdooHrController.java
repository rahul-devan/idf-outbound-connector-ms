package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.dto.odoo.EmployeeResponse;
import com.ndash.idsphere.integrations.service.odoo.OdooEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/odoo")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OdooHrController {

    private final OdooEmployeeService employeeService;

    @GetMapping("/hr/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees() {
        return ResponseEntity.ok(employeeService.getEmployees());
    }
}