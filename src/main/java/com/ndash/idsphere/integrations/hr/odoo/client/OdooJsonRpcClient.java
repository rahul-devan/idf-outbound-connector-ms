package com.ndash.idsphere.integrations.hr.odoo.client;

import com.ndash.idsphere.integrations.config.odoo.OdooConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OdooJsonRpcClient {

    private final WebClient webClient;
    private final OdooConfig config;

    public Integer authenticate() {

        Map<String, Object> request = Map.of(
                "jsonrpc", "2.0",
                "method", "call",
                "params", Map.of(
                        "service", "common",
                        "method", "authenticate",
                        "args", List.of(
                                config.getDb(),
                                config.getUsername(),
                                config.getPassword(),
                                Map.of()
                        )
                ),
                "id", 1
        );

        Map response = webClient.post()
                .uri(config.getUrl() + "/jsonrpc")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Object result = response.get("result");

        if (result == null || Boolean.FALSE.equals(result)) {

            throw new RuntimeException(
                    "Odoo authentication failed. " +
                            "Check database, username and password."
            );
        }

        if (result instanceof Integer) {
            return (Integer) result;
        }

        if (result instanceof Number) {
            return ((Number) result).intValue();
        }

        throw new RuntimeException(
                "Unexpected Odoo auth response: " + result
        );
    }


    public Object execute(Integer uid,
                          String model,
                          String method,
                          List<Object> domain,
                          Map<String, Object> kwargs) {

        Map<String, Object> request = Map.of(
                "jsonrpc", "2.0",
                "method", "call",
                "params", Map.of(
                        "service", "object",
                        "method", "execute_kw",
                        "args", List.of(
                                config.getDb(),
                                uid,
                                config.getPassword(),
                                model,
                                method,
                                domain,
                                kwargs
                        )
                ),
                "id", 2
        );

        Map response = webClient.post()
                .uri(config.getUrl() + "/jsonrpc")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response.get("result");
    }
}
