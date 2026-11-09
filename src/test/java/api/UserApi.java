package api;

import io.restassured.response.Response;
import specs.ReqresSpec;

import static io.restassured.RestAssured.given;

public class UserApi {

    public static Response getUser(int id) {
        return given()
                .spec(ReqresSpec.withAuth())
                .pathParam("id", id)
                .when()
                .get("/api/users/{id}");
    }

    public static Response getUsers(int page) {
        return given()
                .spec(ReqresSpec.withAuth())
                .queryParam("page", page)
                .when()
                .get("/api/users");
    }

    public static Response createUser(Object body) {
        return given()
                .spec(ReqresSpec.withAuth())
                .body(body)
                .when()
                .post("/api/users");
    }

    public static Response updateUser(int id, Object body) {
        return given()
                .spec(ReqresSpec.withAuth())
                .pathParam("id", id)
                .body(body)
                .when()
                .put("/api/users/{id}");
    }

    public static Response patchUser(int id, Object body) {
        return given()
                .spec(ReqresSpec.withAuth())
                .pathParam("id", id)
                .body(body)
                .when()
                .patch("/api/users/{id}");
    }

    public static Response deleteUser(int id) {
        return given()
                .spec(ReqresSpec.withAuth())
                .pathParam("id", id)
                .when()
                .delete("/api/users/{id}");
    }
}