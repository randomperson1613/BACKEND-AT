package ru.at.backend.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public final class Specifications {
    private static final AllureRestAssured ALLURE_REST_ASSURED = new AllureRestAssured()
            .setRequestTemplate("http-request.ftl")
            .setResponseTemplate("http-response.ftl")
            .setRequestAttachmentName("HTTP request")
            .setResponseAttachmentName("HTTP response");
    private static boolean installed;

    private Specifications() {
    }

    public static synchronized void install() {
        if (installed) {
            return;
        }
        RestAssured.reset();
        RestAssured.baseURI = ApiConfig.baseUri();
        RestAssured.requestSpecification = baseRequestSpec();
        RestAssured.responseSpecification = baseResponseSpec();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        installed = true;
    }

    public static RequestSpecification baseRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ApiConfig.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(ALLURE_REST_ASSURED)
                .build();
    }

    public static ResponseSpecification baseResponseSpec() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .addResponseSpecification(baseResponseSpec())
                .expectStatusCode(statusCode)
                .build();
    }
}
