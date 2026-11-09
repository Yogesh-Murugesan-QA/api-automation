package api;

import io.restassured.response.Response;
import utils.ConfigReader;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BookingApi {

    private static final String BASE = ConfigReader.get("booker.base.uri");

    public static String getToken() {
        return given()
                .baseUri(BASE)
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
                .baseUri(BASE)
                .body(booking)
                .when()
                .post("/booking");
    }

    public static Response getBooking(int id) {
        return given()
                .baseUri(BASE)
                .pathParam("id", id)
                .when()
                .get("/booking/{id}");
    }

    public static Response updateBooking(int id, Object booking, String token) {
        return given()
                .baseUri(BASE)
                .cookie("token", token)
                .header("Accept", "application/json")
                .pathParam("id", id)
                .body(booking)
                .when()
                .put("/booking/{id}");
    }

    public static Response deleteBooking(int id, String token) {
        return given()
                .baseUri(BASE)
                .cookie("token", token)
                .pathParam("id", id)
                .when()
                .delete("/booking/{id}");
    }
}