package ru.at.backend.model.response;

public record NoteResponse(boolean success, int status, String message, Note data) {
}
