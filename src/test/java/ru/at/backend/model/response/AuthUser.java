package ru.at.backend.model.response;

public record AuthUser(String id, String name, String email, String token) {
}
