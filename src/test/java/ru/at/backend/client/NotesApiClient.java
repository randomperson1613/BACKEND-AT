package ru.at.backend.client;

import io.qameta.allure.Allure;
import io.restassured.response.Response;
import ru.at.backend.model.request.CreateNoteRequest;
import ru.at.backend.model.request.LoginRequest;
import ru.at.backend.model.request.RegisterUserRequest;
import ru.at.backend.model.request.UpdateCompletedRequest;
import ru.at.backend.model.request.UpdateNoteRequest;
import ru.at.backend.model.request.UpdateProfileRequest;

import static io.restassured.RestAssured.given;
import static ru.at.backend.endpoints.NotesApiEndpoints.HEALTH_CHECK;
import static ru.at.backend.endpoints.NotesApiEndpoints.NOTES;
import static ru.at.backend.endpoints.NotesApiEndpoints.NOTE_BY_ID;
import static ru.at.backend.endpoints.NotesApiEndpoints.USERS_DELETE_ACCOUNT;
import static ru.at.backend.endpoints.NotesApiEndpoints.USERS_LOGIN;
import static ru.at.backend.endpoints.NotesApiEndpoints.USERS_LOGOUT;
import static ru.at.backend.endpoints.NotesApiEndpoints.USERS_PROFILE;
import static ru.at.backend.endpoints.NotesApiEndpoints.USERS_REGISTER;

public class NotesApiClient {
    private static final String AUTH_TOKEN_HEADER = "x-auth-token";

    public Response healthCheck() {
        return Allure.step("GET /health-check", () -> given()
                .when()
                .get(HEALTH_CHECK.path()));
    }

    public Response register(RegisterUserRequest request) {
        return Allure.step("POST /users/register: создать пользователя " + request.email(), () -> given()
                .body(request)
                .when()
                .post(USERS_REGISTER.path()));
    }

    public Response login(LoginRequest request) {
        return Allure.step("POST /users/login: авторизовать пользователя " + request.email(), () -> given()
                .body(request)
                .when()
                .post(USERS_LOGIN.path()));
    }

    public Response getProfile(String token) {
        return Allure.step("GET /users/profile", () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .when()
                .get(USERS_PROFILE.path()));
    }

    public Response getProfileWithoutToken() {
        return Allure.step("GET /users/profile без токена", () -> given()
                .when()
                .get(USERS_PROFILE.path()));
    }

    public Response updateProfile(String token, UpdateProfileRequest request) {
        return Allure.step("PATCH /users/profile", () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .body(request)
                .when()
                .patch(USERS_PROFILE.path()));
    }

    public Response logout(String token) {
        return Allure.step("DELETE /users/logout", () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .when()
                .delete(USERS_LOGOUT.path()));
    }

    public Response deleteAccount(String token) {
        return Allure.step("DELETE /users/delete-account", () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .when()
                .delete(USERS_DELETE_ACCOUNT.path()));
    }

    public Response createNote(String token, CreateNoteRequest request) {
        return Allure.step("POST /notes: создать заметку " + request.title(), () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .body(request)
                .when()
                .post(NOTES.path()));
    }

    public Response getNotes(String token) {
        return Allure.step("GET /notes", () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .when()
                .get(NOTES.path()));
    }

    public Response getNote(String token, String noteId) {
        return Allure.step("GET /notes/" + noteId, () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .pathParam("id", noteId)
                .when()
                .get(NOTE_BY_ID.path()));
    }

    public Response updateNote(String token, String noteId, UpdateNoteRequest request) {
        return Allure.step("PUT /notes/" + noteId, () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .pathParam("id", noteId)
                .body(request)
                .when()
                .put(NOTE_BY_ID.path()));
    }

    public Response updateCompleted(String token, String noteId, UpdateCompletedRequest request) {
        return Allure.step("PATCH /notes/" + noteId + ": изменить completed", () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .pathParam("id", noteId)
                .body(request)
                .when()
                .patch(NOTE_BY_ID.path()));
    }

    public Response deleteNote(String token, String noteId) {
        return Allure.step("DELETE /notes/" + noteId, () -> given()
                .header(AUTH_TOKEN_HEADER, token)
                .pathParam("id", noteId)
                .when()
                .delete(NOTE_BY_ID.path()));
    }
}
