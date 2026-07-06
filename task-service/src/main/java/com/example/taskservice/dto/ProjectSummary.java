package com.example.taskservice.dto;

import java.time.LocalDate;

public record ProjectSummary(
        Long id,
        String name,
        String description,
        Long responsibleId,
        UserSummary responsible,
        String status,
        int progress,
        LocalDate startDate,
        LocalDate endDate
) {
}
