package com.ndash.idsphere.integrations.config.httpclientconfig;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Enumeration;

@Component
@AllArgsConstructor
public class RestConfiguration {
    private RestTemplate restTemplate;

    public ResponseEntity<String> forwardRequest(String targetUrl,
                                                 String body, HttpServletRequest request) {
        // Copy headers from incoming request
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headers.put(name, Collections.list(request.getHeaders(name)));
            }
        }

        // Ensure Content-Type
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // Forward the request
        return restTemplate.exchange(targetUrl, HttpMethod.POST, entity, String.class);
    }
}
