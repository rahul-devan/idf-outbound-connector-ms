package com.ndash.idsphere.integrations.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndash.idsphere.integrations.dto.IntegrationRoleResponse;
import com.ndash.idsphere.integrations.dto.IntegrationUserRequest;
import com.ndash.idsphere.integrations.dto.IntegrationUserResponse;
import com.ndash.idsphere.integrations.dto.jira.JiraProjectResponse;
import com.ndash.idsphere.integrations.dto.jira.JiraRoleResponse;
import com.ndash.idsphere.integrations.exception.ExternalServiceException;
import com.ndash.idsphere.integrations.service.IntegrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class JiraService implements IntegrationService {


    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final String jiraBaseUrl;
    private final String adminEmail;
    private final String apiToken;

    public JiraService(
            @Value("${integrations.jira.base-url}") String jiraBaseUrl,
            @Value("${integrations.jira.admin-email}") String adminEmail,
            @Value("${integrations.jira.api-token}") String apiToken) {
        this.jiraBaseUrl = jiraBaseUrl;
        this.adminEmail = adminEmail;
        this.apiToken = apiToken;
    }

    @Override
    public String getServiceName() {
        return "jira";
    }

    @Override
    public IntegrationUserResponse createUser(IntegrationUserRequest request) {
        try {
            var encodedAuth = Base64.getEncoder()
                    .encodeToString((adminEmail + ":" + apiToken).getBytes(StandardCharsets.UTF_8));

            // Step 1️⃣ — Async user creation
            var createUserFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    var createUrl = jiraBaseUrl + "/rest/api/3/user";
                    var body = mapper.writeValueAsString(Map.of(
                            "emailAddress", request.email(),
                            "displayName", request.displayName(),
                            "products", List.of("jira-software")
                    ));

                    var req = HttpRequest.newBuilder()
                            .uri(URI.create(createUrl))
                            .timeout(Duration.ofSeconds(20))
                            .header("Authorization", "Basic " + encodedAuth)
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(body))
                            .build();

                    var resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

                    if (resp.statusCode() != 201 && resp.statusCode() != 200)
                        throw new ExternalServiceException("JIRA_USER_CREATE_FAILED",
                                "Failed to create user: " + resp.statusCode() + " - " + resp.body());

                    var accountId = mapper.readTree(resp.body()).path("accountId").asText();
                    log.info("✅ Jira user created: " + accountId);
                    return accountId;
                } catch (Exception e) {
                    throw new CompletionException(new ExternalServiceException("JIRA_USER_CREATE_ERROR", e.getMessage()));
                }
            });

            // Step 2️⃣ — Once creation succeeds, assign the role asynchronously
            createUserFuture.thenAcceptAsync(accountId -> {
                try {
                    if (request.roleId() != null && request.projectKey() != null) {
                        assignRoleToUser(accountId, request.projectKey(), request.roleId(), encodedAuth);
                    } else {
                        log.info("ℹ️ Skipping role assignment — projectKey or roleId not provided");
                    }
                } catch (Exception e) {
                    log.log(Level.SEVERE, "⚠️ Role assignment failed after user creation", e);
                }
            });

            // Step 3️⃣ — Return response immediately (non-blocking)
            var accountId = createUserFuture.join();
            return new IntegrationUserResponse(accountId, request.email(), request.displayName(), "CREATED");

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error creating Jira user (async)", e);
            throw new ExternalServiceException("JIRA_ASYNC_CREATE_ERROR", e.getMessage());
        }
    }


    @Override
    public List<IntegrationRoleResponse> getRoles() {
        try {
            String url = jiraBaseUrl + "/rest/api/3/role";

            String encodedAuth = Base64.getEncoder()
                    .encodeToString((adminEmail + ":" + apiToken).getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ExternalServiceException("JIRA_ROLE_FETCH_FAILED",
                        "Failed to fetch roles: " + response.statusCode() + " - " + response.body());
            }

            // Jira returns a map of roleName -> roleURL, so we map it manually
            List<JiraRoleResponse> jiraRoles = mapper.readValue(response.body(), new TypeReference<>() {});

            var roles = jiraRoles.stream()
                    .map(role -> new IntegrationRoleResponse(role.id(), role.name(), role.description()))
                    .toList();

            return roles;

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error fetching Jira roles", e);
            throw new ExternalServiceException("JIRA_ROLE_ERROR", e.getMessage());
        }
    }

    public List<JiraProjectResponse> getProjects() {
        try {
            var url = jiraBaseUrl + "/rest/api/3/project/search";

            var encodedAuth = Base64.getEncoder()
                    .encodeToString((adminEmail + ":" + apiToken).getBytes(StandardCharsets.UTF_8));

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new ExternalServiceException(
                        "JIRA_PROJECT_FETCH_FAILED",
                        "Failed to fetch projects: " + response.statusCode() + " - " + response.body()
                );

            // ✅ Parse "values" array
            var json = mapper.readTree(response.body());
            var values = json.path("values");

            return values.isArray()
                    ? mapper.convertValue(values, new TypeReference<List<JiraProjectResponse>>() {})
                    : List.of();

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error fetching Jira projects", e);
            throw new ExternalServiceException("JIRA_PROJECT_ERROR", e.getMessage());
        }
    }


    private IntegrationUserResponse handleResponse(HttpResponse<String> response, IntegrationUserRequest req) throws Exception {
        var status = response.statusCode();
        var body = response.body();

        return switch (status) {
            case 200, 201 -> parseSuccess(body, req);
            case 400 -> throw new ExternalServiceException("JIRA_BAD_REQUEST", "Invalid request: "+ body);
            case 401 -> throw new ExternalServiceException("JIRA_UNAUTHORIZED", "Unauthorized: Invalid credentials");
            case 403 -> throw new ExternalServiceException("JIRA_FORBIDDEN", "Forbidden: Insufficient permissions");
            default -> throw new ExternalServiceException("JIRA_UNKNOWN_ERROR", "Unexpected status " + status + ": " + body);
        };
    }

    private IntegrationUserResponse parseSuccess(String body, IntegrationUserRequest req) throws Exception {
        JsonNode node = mapper.readTree(body);
        var accountId = node.path("accountId").asText("UNKNOWN");
        var email = node.path("emailAddress").asText(req.email());
        var name = node.path("displayName").asText(req.displayName());
        var active = node.path("active").asBoolean(true);

        log.info("[JiraService] User created successfully: "+accountId);
        return new IntegrationUserResponse(accountId, email, name, active ? "CREATED" : "INACTIVE");
    }


    private void assignRoleToUser(String accountId, String projectKey, String roleId, String encodedAuth) {
        try {
            var roleUrl = jiraBaseUrl + "/rest/api/3/project/" + projectKey + "/role/" + roleId;
            var body = mapper.writeValueAsString(Map.of("user", List.of(accountId)));

            var req = HttpRequest.newBuilder()
                    .uri(URI.create(roleUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            var resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200 || resp.statusCode() == 204)
                log.info("✅ Assigned Jira user " + accountId + " to role " + roleId + " in project " + projectKey);
            else
                log.warning("⚠️ Failed to assign user to role: " + resp.statusCode() + " - " + resp.body());

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error assigning Jira role to user", e);
            throw new ExternalServiceException("JIRA_ROLE_ASSIGN_ERROR", e.getMessage());
        }
    }

}
