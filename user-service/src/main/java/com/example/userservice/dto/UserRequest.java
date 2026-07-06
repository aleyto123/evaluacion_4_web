package com.example.userservice.dto;

public record UserRequest(String fullName, String email, String password, String role) {
}
