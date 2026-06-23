package ru.at.backend.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.equalTo;
import static ru.at.backend.helpers.CustomApiListener.withCustomTemplates;

public class Specifications {
    public static RequestSpecification baseRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ApiConfig.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(withCustomTemplates())
                .build();
    }

    public static ResponseSpecification baseResponseSpec() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpec(int statusCode, boolean success) {
        return new ResponseSpecBuilder()
                .addResponseSpecification(baseResponseSpec())
                .expectStatusCode(statusCode)
                .expectBody("success", equalTo(success))
                .expectBody("status", equalTo(statusCode))
                .build();
    }
}
