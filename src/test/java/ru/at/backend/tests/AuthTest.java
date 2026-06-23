package ru.at.backend.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.at.backend.model.request.LoginRequest;
import ru.at.backend.model.request.RegisterUserRequest;
import ru.at.backend.model.request.UpdateProfileRequest;
import ru.at.backend.model.response.ErrorResponse;
import ru.at.backend.model.response.LoginResponse;
import ru.at.backend.model.response.ProfileResponse;
import ru.at.backend.model.response.RegisterResponse;
import ru.at.backend.support.TestUser;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

        RegisterResponse registerModel = api.register(new RegisterUserRequest(user.name(), user.email(), user.password()))
                .then()
                .spec(responseSpec(201, true))
                .extract()
                .as(RegisterResponse.class);

        assertThat(registerModel.data().id()).isNotBlank();
        assertThat(registerModel.data().name()).isEqualTo(user.name());
        assertThat(registerModel.data().email()).isEqualTo(user.email());

        LoginResponse loginModel = api.login(new LoginRequest(user.email(), user.password()))
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(LoginResponse.class);

        assertThat(loginModel.data().email()).isEqualTo(user.email());
        assertThat(loginModel.data().token()).hasSize(64);
        rememberForCleanup(user.withId(loginModel.data().id()).withToken(loginModel.data().token()));
    }

    @Test
    @Story("Профиль пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /users/profile возвращает профиль авторизованного пользователя")
    void shouldReturnAuthorizedUserProfile() {
        TestUser user = registerAndLoginNewUser();

        ProfileResponse profileModel = api.getProfile(user.token())
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(ProfileResponse.class);

        assertThat(profileModel.data().id()).isEqualTo(user.id());
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

        ProfileResponse profileModel = api.updateProfile(user.token(), request)
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(ProfileResponse.class);

        assertThat(profileModel.data().name()).isEqualTo(request.name());
        assertThat(profileModel.data().phone()).isEqualTo(request.phone());
        assertThat(profileModel.data().company()).isEqualTo(request.company());
    }

    @Test
    @Story("Негативная авторизация")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /users/profile без x-auth-token возвращает 401")
    void shouldRejectProfileRequestWithoutToken() {
        ErrorResponse error = api.getProfileWithoutToken()
                .then()
                .spec(responseSpec(401, false))
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.message()).isEqualTo("No authentication token specified in x-auth-token header");
    }

    @Test
    @Story("Негативная авторизация")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("POST /users/login с неизвестным email возвращает 401")
    void shouldRejectLoginWithUnknownEmail() {
        String email = "missing-" + UUID.randomUUID().toString().replace("-", "") + "@example.com";

        ErrorResponse error = api.login(new LoginRequest(email, "Pass12345"))
                .then()
                .spec(responseSpec(401, false))
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.message()).isEqualTo("Incorrect email address or password");
    }

    @Test
    @Story("Выход из системы")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("DELETE /users/logout инвалидирует текущий токен")
    void shouldLogoutUserAndInvalidateToken() {
        TestUser user = registerAndLoginNewUser();

        api.logout(user.token())
                .then()
                .spec(responseSpec(200, true));

        api.getProfile(user.token())
                .then()
                .spec(responseSpec(401, false));
    }
}
