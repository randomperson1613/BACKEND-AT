package ru.at.backend.config;

public final class ApiConfig {
    public static final String DEFAULT_BASE_URI = "https://practice.expandtesting.com/notes/api";

    private ApiConfig() {
    }

    public static String baseUri() {
        String value = firstNonBlank(
                System.getProperty("baseUri"),
                System.getProperty("baseUrl"),
                System.getenv("BASE_URI"),
                DEFAULT_BASE_URI
        );
        return removeTrailingSlash(value);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return DEFAULT_BASE_URI;
    }

    private static String removeTrailingSlash(String value) {
        String result = value;
        while (result.endsWith("/") && result.length() > 1) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
