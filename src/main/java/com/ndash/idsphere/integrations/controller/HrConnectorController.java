package com.ndash.idsphere.integrations.controller;

import com.ndash.idsphere.integrations.dto.hr.HrEmployee;
import com.ndash.idsphere.integrations.service.HrClientService;
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
public class HrConnectorController {

    private final HrClientService employeeService;

    @GetMapping("/hr/employees")
    public ResponseEntity<List<HrEmployee>> getEmployees() {
        return ResponseEntity.ok(employeeService.getEmployees());
    }
}