package ru.at.backend.endpoints;

public enum NotesApiEndpoints {
    HEALTH_CHECK("/health-check"),
    USERS_REGISTER("/users/register"),
    USERS_LOGIN("/users/login"),
    USERS_PROFILE("/users/profile"),
    USERS_LOGOUT("/users/logout"),
    USERS_DELETE_ACCOUNT("/users/delete-account"),
    NOTES("/notes"),
    NOTE_BY_ID("/notes/{id}");

    private final String path;

    NotesApiEndpoints(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
