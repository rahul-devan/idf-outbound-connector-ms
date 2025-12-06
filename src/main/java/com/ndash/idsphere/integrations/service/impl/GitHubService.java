package com.ndash.idsphere.integrations.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndash.idsphere.integrations.dto.IntegrationRoleResponse;
import com.ndash.idsphere.integrations.dto.IntegrationUserRequest;
import com.ndash.idsphere.integrations.dto.IntegrationUserResponse;
import com.ndash.idsphere.integrations.exception.ExternalServiceException;
import com.ndash.idsphere.integrations.service.IntegrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service("github")
public class GitHubService implements IntegrationService {

    @Value("${integrations.github.token}")
    private String githubToken;

    @Value("${integrations.github.org}")
    private String githubOrg;

    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public String getServiceName() {
        return "github";
    }

    @Override
    public IntegrationUserResponse createUser(IntegrationUserRequest request) {
        try {
            var url = "https://api.github.com/orgs/" + request.gitHubOrg() + "/invitations";

            var body = mapper.writeValueAsString(
                    java.util.Map.of(
                            "email", request.email(),
                            "role", "direct_member"
                    )
            );

            var req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github+json")
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            var resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            return switch (resp.statusCode()) {
                case 201 -> new IntegrationUserResponse(
                        request.email(), request.email(), request.displayName(), "INVITED"
                );
                case 422 -> throw new ExternalServiceException("GITHUB_ALREADY_MEMBER",
                        "User is already in the organization");
                default -> throw new ExternalServiceException("GITHUB_CREATE_ERROR",
                        "GitHub Error " + resp.statusCode() + ": " + resp.body());
            };

        } catch (Exception e) {
            log.log(Level.SEVERE, "GitHub user creation failed", e);
            throw new ExternalServiceException("GITHUB_CREATE_EXCEPTION", e.getMessage());
        }
    }

    public List<String> getOrganizations() {
        try {
            var url = "https://api.github.com/user/orgs";

            var req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + githubToken)
                    .header("Accept", "application/vnd.github+json")
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            var resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200)
                throw new ExternalServiceException("GITHUB_ORG_FETCH_FAILED",
                        resp.statusCode() + ": " + resp.body());

            var array = mapper.readTree(resp.body());
            return array.findValuesAsText("login");

        } catch (Exception e) {
            throw new ExternalServiceException("GITHUB_ORG_EXCEPTION", e.getMessage());
        }
    }

    @Override
    public List<IntegrationRoleResponse> getRoles() {
        return List.of();
    }
}
