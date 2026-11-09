package specs;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import utils.ConfigReader;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ReqresSpec {

    private static RequestSpecification authSpec;

    // Basic spec: URL, API key, JSON. No login token.
    public static RequestSpecification base() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.uri"))
                .addHeader("x-api-key", ConfigReader.getRequired("api.key"))
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    // Base spec + Bearer token. Logs in only the first time it's called.
    public static synchronized RequestSpecification withAuth() {
        if (authSpec == null) {
            String token = given()
                    .spec(base())
                    .body(Map.of(
                            "email", ConfigReader.get("login.email"),
                            "password", ConfigReader.get("login.password")))
                    .when()
                    .post("/api/login")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .extract().path("token");

            authSpec = new RequestSpecBuilder()
                    .addRequestSpecification(base())
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
        }
        return authSpec;
    }
}