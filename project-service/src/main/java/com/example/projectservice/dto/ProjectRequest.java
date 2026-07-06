package com.example.projectservice.dto;

import java.time.LocalDate;

public record ProjectRequest(
        String name,
        String description,
        Long responsibleId,
        String status,
        Integer progress,
        LocalDate startDate,
        LocalDate endDate
) {
}
