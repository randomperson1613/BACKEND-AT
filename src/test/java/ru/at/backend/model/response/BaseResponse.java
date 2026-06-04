package ru.at.backend.model.response;

public record BaseResponse(boolean success, int status, String message) {
}
