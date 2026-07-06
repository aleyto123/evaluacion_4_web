package com.example.taskservice.dto;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        Long projectId,
        ProjectSummary project,
        Long responsibleId,
        UserSummary responsible,
        String title,
        String description,
        String status,
        int progress,
        LocalDate dueDate
) {
}
