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
import ru.at.backend.model.response.BaseResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.at.backend.config.Specifications.responseSpec;

@Epic("Notes API")
@Feature("Health")
@Owner("QA Automation")
class HealthTest extends BaseApiTest {

    @Test
    @Story("Проверка доступности сервиса")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("GET /health-check возвращает статус работающего API")
    void shouldReturnHealthCheckStatus() {
        Response response = api.healthCheck();

        response.then()
                .spec(responseSpec(200))
                .body("success", equalTo(true))
                .body("status", equalTo(200))
                .body("message", equalTo("Notes API is Running"));

        BaseResponse model = response.as(BaseResponse.class);
        assertThat(model).isEqualTo(new BaseResponse(true, 200, "Notes API is Running"));
    }
}
