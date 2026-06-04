package ru.at.backend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.at.backend.model.NoteCategory;

public record Note(
        String id,
        String title,
        String description,
        NoteCategory category,
        boolean completed,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt,
        @JsonProperty("user_id") String userId
) {
}
