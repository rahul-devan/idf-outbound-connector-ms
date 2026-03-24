package com.ndash.idsphere.integrations.service.odoo;

import com.ndash.idsphere.integrations.client.odoo.OdooJsonRpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OdooAuthService {

    private final OdooJsonRpcClient client;

    public Integer login() {
        return client.authenticate();
    }
}
