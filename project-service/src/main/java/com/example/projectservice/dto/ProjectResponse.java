package com.example.projectservice.dto;

import java.time.LocalDate;

public record ProjectResponse(
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
