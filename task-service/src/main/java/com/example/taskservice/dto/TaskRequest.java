package com.example.taskservice.dto;

import java.time.LocalDate;

public record TaskRequest(
        Long projectId,
        Long responsibleId,
        String title,
        String description,
        String status,
        Integer progress,
        LocalDate dueDate
) {
}
