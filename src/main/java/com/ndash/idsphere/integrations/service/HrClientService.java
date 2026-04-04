package com.ndash.idsphere.integrations.service;

import com.ndash.idsphere.integrations.dto.hr.HrEmployee;

import java.util.List;

public interface HrClientService {

    List<HrEmployee> getEmployees();
}
