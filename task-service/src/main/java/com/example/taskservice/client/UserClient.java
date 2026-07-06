package com.example.taskservice.client;

import com.example.taskservice.dto.UserSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserSummary findById(@PathVariable("id") Long id);
}
