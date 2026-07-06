package com.example.projectservice.dto;

public record UserSummary(Long id, String fullName, String email, String role, boolean active) {
}
