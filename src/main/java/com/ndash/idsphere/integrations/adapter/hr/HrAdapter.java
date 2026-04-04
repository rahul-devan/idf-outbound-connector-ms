package com.ndash.idsphere.integrations.adapter.hr;

import com.ndash.idsphere.integrations.domain.enums.ExternalSource;
import com.ndash.idsphere.integrations.dto.hr.HrEmployee;

import java.util.List;

public interface HrAdapter {

    List<HrEmployee> getEmployees();

    ExternalSource getSource();
}