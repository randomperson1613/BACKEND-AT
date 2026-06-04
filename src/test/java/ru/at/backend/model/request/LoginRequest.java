package ru.at.backend.model.request;

public record LoginRequest(String email, String password) {
    @Override
    public String toString() {
        return "LoginRequest[email=%s, password=***]".formatted(email);
    }
}
