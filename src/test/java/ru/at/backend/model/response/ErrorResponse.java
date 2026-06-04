package ru.at.backend.model.response;

public record ErrorResponse(boolean success, int status, String message) {
}
