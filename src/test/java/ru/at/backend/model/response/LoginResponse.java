package ru.at.backend.model.response;

public record LoginResponse(boolean success, int status, String message, AuthUser data) {
}
