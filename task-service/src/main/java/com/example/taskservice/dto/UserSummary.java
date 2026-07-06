package com.example.taskservice.dto;

public record UserSummary(Long id, String fullName, String email, String role, boolean active) {
}
