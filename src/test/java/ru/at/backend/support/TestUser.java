package ru.at.backend.support;

public record TestUser(String id, String name, String email, String password, String token) {
    public TestUser withToken(String token) {
        return new TestUser(id, name, email, password, token);
    }

    public TestUser withId(String id) {
        return new TestUser(id, name, email, password, token);
    }
}
