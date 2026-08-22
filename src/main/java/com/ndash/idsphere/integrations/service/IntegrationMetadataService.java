package com.ndash.idsphere.integrations.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndash.idsphere.integrations.cache.IntegrationMetadataCache;
import com.ndash.idsphere.integrations.domain.Application;
import com.ndash.idsphere.integrations.dto.IntegrationRoleResponse;
import com.ndash.idsphere.integrations.dto.github.GitHubProjectResponse;
import com.ndash.idsphere.integrations.dto.jira.JiraProjectResponse;
import com.ndash.idsphere.integrations.exception.IntegrationException;
import com.ndash.idsphere.integrations.service.impl.GitHubService;
import com.ndash.idsphere.integrations.service.impl.JiraService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class IntegrationMetadataService {

    private final ApplicationService applicationService;
    private final IntegrationMetadataCache metadataCache;
    private final GitHubService gitHubService;
    private final JiraService jiraService;
    private final ObjectMapper objectMapper;

    @Value("${integrations.github.org}")
    private String defaultGitHubOrg;

    public IntegrationMetadataService(
            ApplicationService applicationService,
            IntegrationMetadataCache metadataCache,
            GitHubService gitHubService,
            JiraService jiraService,
            ObjectMapper objectMapper
    ) {
        this.applicationService = applicationService;
        this.metadataCache = metadataCache;
        this.gitHubService = gitHubService;
        this.jiraService = jiraService;
        this.objectMapper = objectMapper;
    }

    public List<GitHubProjectResponse> getGitHubProjects() {
        return getProjects("github", GitHubProjectResponse.class);
    }

    public List<JiraProjectResponse> getJiraProjects() {
        return getProjects("jira", JiraProjectResponse.class);
    }

    public List<GitHubProjectResponse> syncGitHubProjects() {
        return syncProjects("github", GitHubProjectResponse.class);
    }

    public List<JiraProjectResponse> syncJiraProjects() {
        return syncProjects("jira", JiraProjectResponse.class);
    }

    public List<IntegrationRoleResponse> getGitHubRoles(String org) {
        return getRoles("github", resolveGitHubOrg(org));
    }

    public List<IntegrationRoleResponse> getJiraRoles() {
        return getRoles("jira", null);
    }

    public List<IntegrationRoleResponse> syncGitHubRoles(String org) {
        return syncRoles("github", resolveGitHubOrg(org));
    }

    public List<IntegrationRoleResponse> syncJiraRoles() {
        return syncRoles("jira", null);
    }

    public <T> List<T> getProjects(String integrationCode, Class<T> responseType) {
        Application application = applicationService.requireActive(integrationCode);
        String code = applicationService.resolveIntegrationCode(application);
        String cacheKey = projectsCacheKey(code);

        return metadataCache.get(cacheKey)
                .map(cached -> castList(cached, responseType))
                .orElseGet(() -> syncProjects(integrationCode, responseType));
    }

    public <T> List<T> syncProjects(String integrationCode, Class<T> responseType) {
        Application application = applicationService.requireActive(integrationCode);
        String code = applicationService.resolveIntegrationCode(application);
        String cacheKey = projectsCacheKey(code);
        List<T> activeItems = deduplicateProjects(code, fetchProjectsFromProvider(code, responseType));
        metadataCache.put(cacheKey, activeItems);
        return activeItems;
    }

    public List<IntegrationRoleResponse> getRoles(String integrationCode, String org) {
        Application application = applicationService.requireActive(integrationCode);
        String code = applicationService.resolveIntegrationCode(application);
        String cacheKey = rolesCacheKey(code, org);

        return metadataCache.get(cacheKey)
                .map(this::castRoleList)
                .orElseGet(() -> syncRoles(integrationCode, org));
    }

    public List<IntegrationRoleResponse> syncRoles(String integrationCode, String org) {
        Application application = applicationService.requireActive(integrationCode);
        String code = applicationService.resolveIntegrationCode(application);
        String cacheKey = rolesCacheKey(code, org);
        List<IntegrationRoleResponse> activeItems = deduplicateRoles(fetchRolesFromProvider(code, org));
        metadataCache.put(cacheKey, activeItems);
        return activeItems;
    }

    private <T> List<T> deduplicateProjects(String integrationCode, List<T> items) {
        Map<String, T> uniqueItems = new LinkedHashMap<>();
        for (T item : items) {
            uniqueItems.put(extractProjectExternalId(integrationCode, item), item);
        }
        return List.copyOf(uniqueItems.values());
    }

    private List<IntegrationRoleResponse> deduplicateRoles(List<IntegrationRoleResponse> items) {
        Map<String, IntegrationRoleResponse> uniqueItems = new LinkedHashMap<>();
        for (IntegrationRoleResponse item : items) {
            uniqueItems.put(item.id(), item);
        }
        return List.copyOf(uniqueItems.values());
    }

    private <T> String extractProjectExternalId(String integrationCode, T item) {
        return switch (integrationCode.toLowerCase()) {
            case "github" -> objectMapper.convertValue(item, GitHubProjectResponse.class).id();
            case "jira" -> objectMapper.convertValue(item, JiraProjectResponse.class).id();
            default -> throw new IntegrationException(
                    "UNSUPPORTED_INTEGRATION",
                    "Unsupported integration: " + integrationCode
            );
        };
    }

    private <T> List<T> fetchProjectsFromProvider(String integrationCode, Class<T> responseType) {
        return switch (integrationCode.toLowerCase()) {
            case "github" -> castList(gitHubService.getProjects(), responseType);
            case "jira" -> castList(jiraService.getProjects(), responseType);
            default -> throw new IntegrationException(
                    "UNSUPPORTED_INTEGRATION",
                    "Unsupported integration: " + integrationCode
            );
        };
    }

    private List<IntegrationRoleResponse> fetchRolesFromProvider(String integrationCode, String org) {
        return switch (integrationCode.toLowerCase()) {
            case "github" -> gitHubService.getRoles(org);
            case "jira" -> jiraService.getRoles();
            default -> throw new IntegrationException(
                    "UNSUPPORTED_INTEGRATION",
                    "Unsupported integration: " + integrationCode
            );
        };
    }

    private String projectsCacheKey(String integrationCode) {
        return integrationCode.toLowerCase() + ":projects";
    }

    private String rolesCacheKey(String integrationCode, String org) {
        if ("github".equalsIgnoreCase(integrationCode) && org != null && !org.isBlank()) {
            return "github:roles:" + org.toLowerCase();
        }
        return integrationCode.toLowerCase() + ":roles";
    }

    private String resolveGitHubOrg(String org) {
        if (org == null || org.isBlank()) {
            return defaultGitHubOrg;
        }
        return org;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> castList(List<?> items, Class<T> type) {
        return items.stream()
                .map(item -> type.isInstance(item) ? type.cast(item) : objectMapper.convertValue(item, type))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<IntegrationRoleResponse> castRoleList(List<?> items) {
        return items.stream().map(item -> (IntegrationRoleResponse) item).toList();
    }
}
