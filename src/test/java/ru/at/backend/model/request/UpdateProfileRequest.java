package ru.at.backend.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateProfileRequest(String name, String phone, String company) {
}
