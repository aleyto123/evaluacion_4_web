package com.example.projectservice.service;

import com.example.projectservice.client.UserClient;
import com.example.projectservice.dto.UserSummary;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RemoteUserService {
    private static final Logger log = LoggerFactory.getLogger(RemoteUserService.class);

    private final UserClient userClient;
    private final String fallbackMessage;

    public RemoteUserService(UserClient userClient,
                             @Value("${app.fallback-message:No fue posible consultar el servicio de usuarios. Intente nuevamente.}") String fallbackMessage) {
        this.userClient = userClient;
        this.fallbackMessage = fallbackMessage;
    }

    @Retry(name = "userService")
    @CircuitBreaker(name = "userService", fallbackMethod = "responsibleFallback")
    public UserSummary getResponsible(Long responsibleId) {
        if (responsibleId == null) {
            return null;
        }
        return userClient.findById(responsibleId);
    }

    private UserSummary responsibleFallback(Long responsibleId, Throwable throwable) {
        log.warn("Fallo consultando USER-SERVICE para responsable {}: {}", responsibleId, throwable.getMessage());
        return new UserSummary(null, fallbackMessage, null, null, false);
    }
}
