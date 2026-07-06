package com.example.taskservice.client;

import com.example.taskservice.dto.ProjectSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PROJECT-SERVICE")
public interface ProjectClient {
    @GetMapping("/api/projects/{id}")
    ProjectSummary findById(@PathVariable("id") Long id);
}
