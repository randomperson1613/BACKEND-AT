package ru.at.backend.model.response;

public record ProfileResponse(boolean success, int status, String message, User data) {
}
