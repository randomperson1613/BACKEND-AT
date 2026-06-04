package ru.at.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum NoteCategory {
    HOME("Home"),
    WORK("Work"),
    PERSONAL("Personal");

    private final String apiValue;

    NoteCategory(String apiValue) {
        this.apiValue = apiValue;
    }

    @JsonValue
    public String apiValue() {
        return apiValue;
    }

    @JsonCreator
    public static NoteCategory fromApiValue(String value) {
        return Arrays.stream(values())
                .filter(category -> category.apiValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown note category: " + value));
    }

    @Override
    public String toString() {
        return apiValue;
    }
}
