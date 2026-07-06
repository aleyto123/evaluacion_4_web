package com.example.taskservice.service;

import com.example.taskservice.client.ProjectClient;
import com.example.taskservice.client.UserClient;
import com.example.taskservice.dto.ProjectSummary;
import com.example.taskservice.dto.UserSummary;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RemoteLookupService {
    private static final Logger log = LoggerFactory.getLogger(RemoteLookupService.class);

    private final ProjectClient projectClient;
    private final UserClient userClient;
    private final String fallbackMessage;

    public RemoteLookupService(ProjectClient projectClient, UserClient userClient,
                               @Value("${app.fallback-message:No fue posible consultar el servicio requerido. Intente nuevamente.}") String fallbackMessage) {
        this.projectClient = projectClient;
        this.userClient = userClient;
        this.fallbackMessage = fallbackMessage;
    }

    @Retry(name = "projectService")
    @CircuitBreaker(name = "projectService", fallbackMethod = "projectFallback")
    public ProjectSummary getProject(Long projectId) {
        if (projectId == null) {
            return null;
        }
        return projectClient.findById(projectId);
    }

    @Retry(name = "userService")
    @CircuitBreaker(name = "userService", fallbackMethod = "userFallback")
    public UserSummary getResponsible(Long responsibleId) {
        if (responsibleId == null) {
            return null;
        }
        return userClient.findById(responsibleId);
    }

    private ProjectSummary projectFallback(Long projectId, Throwable throwable) {
        log.warn("Fallo consultando PROJECT-SERVICE para proyecto {}: {}", projectId, throwable.getMessage());
        return new ProjectSummary(null, fallbackMessage, null, null, null, null, 0, null, null);
    }

    private UserSummary userFallback(Long responsibleId, Throwable throwable) {
        log.warn("Fallo consultando USER-SERVICE para responsable {}: {}", responsibleId, throwable.getMessage());
        return new UserSummary(null, fallbackMessage, null, null, false);
    }
}
