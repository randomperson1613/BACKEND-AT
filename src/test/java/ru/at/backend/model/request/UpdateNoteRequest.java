package ru.at.backend.model.request;

import ru.at.backend.model.NoteCategory;

public record UpdateNoteRequest(String title, String description, boolean completed, NoteCategory category) {
}
