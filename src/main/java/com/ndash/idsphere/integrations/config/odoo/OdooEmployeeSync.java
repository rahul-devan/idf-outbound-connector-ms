package com.ndash.idsphere.integrations.config.odoo;

import com.ndash.idsphere.integrations.adapter.hr.HrAdapter;
import com.ndash.idsphere.integrations.adapter.hr.factory.HrAdapterFactory;
import com.ndash.idsphere.integrations.domain.Blueprint;
import com.ndash.idsphere.integrations.domain.Department;
import com.ndash.idsphere.integrations.domain.JobTitle;
import com.ndash.idsphere.integrations.domain.enums.ExternalSource;
import com.ndash.idsphere.integrations.dto.hr.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@Transactional
public class OdooEmployeeSync implements ApplicationRunner {

    private final HrAdapterFactory adapterFactory;
    private final DepartmentService departmentService;
    private final UserService userService;
    private final BlueprintService blueprintService;
    private final JobTitleService jobTitleService;

    @Value("${hr.default-department-name:UNKNOWN}")
    private String defaultDeptName;

    @Value("${hr.default-department-external-id:DEFAULT}")
    private String defaultDeptExtId;

    public OdooEmployeeSync(HrAdapterFactory adapterFactory,
                            DepartmentService departmentService,
                            UserService userService, BlueprintService blueprintService, JobTitleService jobTitleService) {
        this.adapterFactory = adapterFactory;
        this.departmentService = departmentService;
        this.userService = userService;
        this.blueprintService = blueprintService;
        this.jobTitleService = jobTitleService;
    }

    public void sync() {

        ExternalSource source = ExternalSource.ODOO;

        HrAdapter adapter = adapterFactory.get(source);

        List<HrEmployee> employees = adapter.getEmployees();

        for (HrEmployee emp : employees) {

            try {

                JobTitle jobTitle = jobTitleService.upsert(
                        emp.getJobTitle(),
                        "ODOO"
                );

                Blueprint blueprint = blueprintService.createIfNotExists(
                        jobTitle.getName()
                );

                blueprintService.linkJobTitle(blueprint, jobTitle);

                Department department;

                // 🔥 HANDLE NULL DEPARTMENT
                if (emp.getDepartmentId() == null || emp.getDepartmentName() == null) {

                    department = departmentService.getOrCreateDefault(
                            defaultDeptName,
                            defaultDeptExtId,
                            source.name()
                    );

                } else {

                    department = departmentService.upsert(
                            emp.getDepartmentId().toString(),
                            emp.getDepartmentName(),
                            source.name()
                    );
                }





                userService.upsert(emp, department, source, blueprint, jobTitle);

            } catch (Exception ex) {
                log.error("Error syncing employee: " + emp.getEmail(), ex);
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        sync();
    }
}
