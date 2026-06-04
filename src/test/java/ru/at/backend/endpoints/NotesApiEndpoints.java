package ru.at.backend.endpoints;

public final class NotesApiEndpoints {
    public static final String HEALTH_CHECK = "/health-check";
    public static final String USERS_REGISTER = "/users/register";
    public static final String USERS_LOGIN = "/users/login";
    public static final String USERS_PROFILE = "/users/profile";
    public static final String USERS_LOGOUT = "/users/logout";
    public static final String USERS_DELETE_ACCOUNT = "/users/delete-account";
    public static final String NOTES = "/notes";
    public static final String NOTE_BY_ID = "/notes/{id}";

    private NotesApiEndpoints() {
    }
}
