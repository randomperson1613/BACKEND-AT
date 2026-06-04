package ru.at.backend.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.at.backend.client.NotesApiClient;
import ru.at.backend.config.Specifications;
import ru.at.backend.model.request.LoginRequest;
import ru.at.backend.model.request.RegisterUserRequest;
import ru.at.backend.model.response.LoginResponse;
import ru.at.backend.support.TestUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.at.backend.config.Specifications.responseSpec;

public abstract class BaseApiTest {
    protected final NotesApiClient api = new NotesApiClient();
    private final List<TestUser> usersForCleanup = new ArrayList<>();

    @BeforeAll
    static void configureRestAssured() {
        Specifications.install();
    }

    @AfterEach
    void deleteCreatedUsers() {
        for (int i = usersForCleanup.size() - 1; i >= 0; i--) {
            TestUser user = usersForCleanup.get(i);
            deleteAccountIfExists(user);
        }
        usersForCleanup.clear();
    }

    protected TestUser newUser() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String nameSuffix = suffix.substring(0, 8);
        return new TestUser(
                null,
                "AT User " + nameSuffix,
                "at-backend-" + suffix + "@example.com",
                "Pass12345",
                null
        );
    }

    protected TestUser registerAndLoginNewUser() {
        TestUser user = newUser();

        api.register(new RegisterUserRequest(user.name(), user.email(), user.password()))
                .then()
                .spec(responseSpec(201));

        LoginResponse loginResponse = api.login(new LoginRequest(user.email(), user.password()))
                .then()
                .spec(responseSpec(200))
                .extract()
                .as(LoginResponse.class);

        TestUser authorizedUser = user
                .withId(loginResponse.data().id())
                .withToken(loginResponse.data().token());
        rememberForCleanup(authorizedUser);
        return authorizedUser;
    }

    protected void rememberForCleanup(TestUser user) {
        usersForCleanup.add(user);
    }

    protected String loginForCleanup(TestUser user) {
        LoginResponse loginResponse = api.login(new LoginRequest(user.email(), user.password()))
                .then()
                .spec(responseSpec(200))
                .extract()
                .as(LoginResponse.class);
        assertThat(loginResponse.data().token()).isNotBlank();
        return loginResponse.data().token();
    }

    private void deleteAccountIfExists(TestUser user) {
        String token = user.token();
        if (token == null || token.isBlank()) {
            token = tryLogin(user);
        }

        if (token == null || token.isBlank()) {
            return;
        }

        int deleteStatus = api.deleteAccount(token).statusCode();
        if (deleteStatus == 401) {
            String refreshedToken = tryLogin(user);
            if (refreshedToken != null && !refreshedToken.isBlank()) {
                api.deleteAccount(refreshedToken);
            }
        }
    }

    private String tryLogin(TestUser user) {
        try {
            return api.login(new LoginRequest(user.email(), user.password()))
                    .then()
                    .extract()
                    .as(LoginResponse.class)
                    .data()
                    .token();
        } catch (RuntimeException ignored) {
            return null;
        }
    }
}
