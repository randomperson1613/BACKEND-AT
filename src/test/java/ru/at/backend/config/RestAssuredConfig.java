package ru.at.backend.config;

import io.restassured.RestAssured;

public class RestAssuredConfig {
    private static boolean installed;

    public static synchronized void install() {
        if (installed) {
            return;
        }

        RestAssured.reset();
        RestAssured.baseURI = ApiConfig.baseUri();
        RestAssured.requestSpecification = Specifications.baseRequestSpec();
        RestAssured.responseSpecification = Specifications.baseResponseSpec();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        installed = true;
    }
}
