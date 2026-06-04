package ru.at.backend.model.request;

public record RegisterUserRequest(String name, String email, String password) {
    @Override
    public String toString() {
        return "RegisterUserRequest[name=%s, email=%s, password=***]".formatted(name, email);
    }
}
