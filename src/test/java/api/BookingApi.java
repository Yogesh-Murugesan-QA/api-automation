package api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import specs.BookerSpec;
import utils.ConfigReader;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BookingApi {

    public static String getToken() {
        return given()
                .spec(BookerSpec.base())
                .body(Map.of(
                        "username", ConfigReader.get("booker.username"),
                        "password", ConfigReader.get("booker.password")))
                .when()
                .post("/auth")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().path("token");
    }

    public static Response createBooking(Object booking) {
        return given()
                .spec(BookerSpec.base())
                .body(booking)
                .when()
                .post("/booking");
    }

    public static Response getBooking(int id) {
        return given()
                .spec(BookerSpec.base())
                .pathParam("id", id)
                .when()
                .get("/booking/{id}");
    }

    public static Response updateBooking(int id, Object booking, String token) {
        RequestSpecification request = given()
                .spec(BookerSpec.base())
                .pathParam("id", id)
                .body(booking);

        if (token != null) {
            request.cookie("token", token);
        }

        return request.when().put("/booking/{id}");
    }

    public static Response deleteBooking(int id, String token) {
        RequestSpecification request = given()
                .spec(BookerSpec.base())
                .pathParam("id", id);

        if (token != null) {
            request.cookie("token", token);
        }

        return request.when().delete("/booking/{id}");
    }
}