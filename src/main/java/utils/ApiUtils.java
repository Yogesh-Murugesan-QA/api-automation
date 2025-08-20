package utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiUtils {

    public static Response get(String endpoint, String apiKey) {
        if (apiKey != null) {
            return RestAssured.given()
                    .header("api-key", apiKey)
                    .get(endpoint)
                    .then()
                    .extract().response();
        } else {
            return RestAssured.given()
                    .get(endpoint)
                    .then()
                    .extract().response();
        }
    }

    public static Response post(String endpoint, String apiKey, String payload) {
        if (apiKey != null) {
            return RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header("api-key", apiKey)
                    .body(payload)
                    .post(endpoint)
                    .then()
                    .extract().response();
        } else {
            return RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .post(endpoint)
                    .then()
                    .extract().response();
        }
    }
}
