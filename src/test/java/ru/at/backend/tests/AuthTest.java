package ru.at.backend.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.at.backend.model.request.LoginRequest;
import ru.at.backend.model.request.RegisterUserRequest;
import ru.at.backend.model.request.UpdateProfileRequest;
import ru.at.backend.model.response.BaseResponse;
import ru.at.backend.model.response.ErrorResponse;
import ru.at.backend.model.response.LoginResponse;
import ru.at.backend.model.response.ProfileResponse;
import ru.at.backend.model.response.RegisterResponse;
import ru.at.backend.support.TestUser;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEmptyString.emptyOrNullString;
import static ru.at.backend.config.Specifications.responseSpec;

@Epic("Notes API")
@Feature("Users")
@Owner("QA Automation")
class AuthTest extends BaseApiTest {

    @Test
    @Story("Регистрация и авторизация")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /users/register и POST /users/login создают и авторизуют нового пользователя")
    void shouldRegisterAndLoginNewUser() {
        TestUser user = newUser();

        Response registerResponse = api.register(new RegisterUserRequest(user.name(), user.email(), user.password()));
        registerResponse.then()
                .spec(responseSpec(201))
                .body("success", equalTo(true))
                .body("status", equalTo(201))
                .body("data.name", equalTo(user.name()))
                .body("data.email", equalTo(user.email()));

        RegisterResponse registerModel = registerResponse.as(RegisterResponse.class);
        assertThat(registerModel.data().id()).isNotBlank();
        assertThat(registerModel.data().email()).isEqualTo(user.email());

        Response loginResponse = api.login(new LoginRequest(user.email(), user.password()));
        loginResponse.then()
                .spec(responseSpec(200))
                .body("success", equalTo(true))
                .body("data.email", equalTo(user.email()))
                .body("data.token", not(emptyOrNullString()));

        LoginResponse loginModel = loginResponse.as(LoginResponse.class);
        assertThat(loginModel.data().token()).hasSize(64);
        rememberForCleanup(user.withId(loginModel.data().id()).withToken(loginModel.data().token()));
    }

    @Test
    @Story("Профиль пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /users/profile возвращает профиль авторизованного пользователя")
    void shouldReturnAuthorizedUserProfile() {
        TestUser user = registerAndLoginNewUser();

        Response profileResponse = api.getProfile(user.token());
        profileResponse.then()
                .spec(responseSpec(200))
                .body("success", equalTo(true))
                .body("status", equalTo(200))
                .body("data.id", equalTo(user.id()))
                .body("data.email", equalTo(user.email()));

        ProfileResponse profileModel = profileResponse.as(ProfileResponse.class);
        assertThat(profileModel.data().name()).isEqualTo(user.name());
        assertThat(profileModel.data().email()).isEqualTo(user.email());
    }

    @Test
    @Story("Профиль пользователя")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("PATCH /users/profile обновляет name, phone и company")
    void shouldUpdateAuthorizedUserProfile() {
        TestUser user = registerAndLoginNewUser();
        UpdateProfileRequest request = new UpdateProfileRequest("Updated " + user.name(), "79990000000", "AT Backend");

        Response updateResponse = api.updateProfile(user.token(), request);
        updateResponse.then()
                .spec(responseSpec(200))
                .body("success", equalTo(true))
                .body("data.name", equalTo(request.name()))
                .body("data.phone", equalTo(request.phone()))
                .body("data.company", equalTo(request.company()));

        ProfileResponse profileModel = updateResponse.as(ProfileResponse.class);
        assertThat(profileModel.data().name()).isEqualTo(request.name());
        assertThat(profileModel.data().phone()).isEqualTo(request.phone());
        assertThat(profileModel.data().company()).isEqualTo(request.company());
    }

    @Test
    @Story("Негативная авторизация")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /users/profile без x-auth-token возвращает 401")
    void shouldRejectProfileRequestWithoutToken() {
        Response response = api.getProfileWithoutToken();

        response.then()
                .spec(responseSpec(401))
                .body("success", equalTo(false))
                .body("status", equalTo(401))
                .body("message", equalTo("No authentication token specified in x-auth-token header"));

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.message()).contains("No authentication token");
    }

    @Test
    @Story("Негативная авторизация")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /users/login с неизвестным email возвращает 401")
    void shouldRejectLoginWithUnknownEmail() {
        String email = "missing-" + UUID.randomUUID().toString().replace("-", "") + "@example.com";

        Response response = api.login(new LoginRequest(email, "Pass12345"));
        response.then()
                .spec(responseSpec(401))
                .body("success", equalTo(false))
                .body("status", equalTo(401))
                .body("message", equalTo("Incorrect email address or password"));

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.success()).isFalse();
        assertThat(error.status()).isEqualTo(401);
    }

    @Test
    @Story("Выход из системы")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("DELETE /users/logout инвалидирует текущий токен")
    void shouldLogoutUserAndInvalidateToken() {
        TestUser user = registerAndLoginNewUser();

        Response logoutResponse = api.logout(user.token());
        logoutResponse.then()
                .spec(responseSpec(200))
                .body("success", equalTo(true))
                .body("status", equalTo(200));

        BaseResponse logoutModel = logoutResponse.as(BaseResponse.class);
        assertThat(logoutModel.success()).isTrue();

        api.getProfile(user.token())
                .then()
                .spec(responseSpec(401))
                .body("success", equalTo(false))
                .body("status", equalTo(401));
    }
}
