package ru.at.backend.model.response;

import java.util.List;

public record NotesResponse(boolean success, int status, String message, List<Note> data) {
}
