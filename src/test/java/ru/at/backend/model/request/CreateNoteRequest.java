package ru.at.backend.model.request;

import ru.at.backend.model.NoteCategory;

public record CreateNoteRequest(String title, String description, NoteCategory category) {
}
