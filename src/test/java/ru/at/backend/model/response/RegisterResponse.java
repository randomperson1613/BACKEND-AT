package ru.at.backend.model.response;

public record RegisterResponse(boolean success, int status, String message, User data) {
}
