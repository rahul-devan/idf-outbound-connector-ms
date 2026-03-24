package com.ndash.idsphere.integrations.config.odoo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "integrations.odoo")
@Data
public class OdooConfig {

    private String url;
    private String db;
    private String username;
    private String password;
}
