package tests;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.CreateUserResponse;
import pojo.GetUserResponse;
import pojo.User;
import pojo.UserData;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class FirstTest extends BaseTest {
    @Epic("User Management")
    @Test
    public void getUser(){

        given().log().headers().queryParam("page", 2)
                .when().get("/api/users")
                .then().spec(successSpec)
                .body("page",equalTo(2))
                .body("data",hasSize(greaterThan(0)))
                .body("data[0].email",notNullValue())
                .body("data.email",everyItem(containsString("@")));

    }
    @Test
    public void createUser(){

       User user= new User("Yogi","SDET");

        String userId=given()
                .body(user)
                .when().post("/api/users")
                .then().statusCode(201)
                .body("name",equalTo("Yogi"))
                .body("job",equalTo("SDET")).log().body()
                .extract().path("id");
        System.out.println("Created user id: " + userId);

    }
    @Test
    public void getUserStoreResponse() {

        Response response = given()
                .pathParam("id", 2)
                .when()
                .get("/api/users/{id}");
        System.out.println(response.asPrettyString());
        // Read values from it
        int statusCode = response.getStatusCode();
        String email = response.jsonPath().getString("data.email");
        int id = response.jsonPath().getInt("data.id");
        long time = response.getTime();

        // Assert with TestNG
        Assert.assertEquals(statusCode, 200);
        Assert.assertEquals(id, 2);
        Assert.assertTrue(email.contains("@"));
        System.out.println("Email: " + email + " | Time: " + time + " ms");
    }
    @Test
    public void getAllEmail(){

        Response response=given().queryParam("page", 2)
                .when().get("/api/users");
        List<String> emails= response.jsonPath().getList("data.email");
        System.out.println("Total emails: " + emails.size());
        for (String email : emails) {
            System.out.println(email);
        }
    }
    @Test
    public void updateUser() {

        String requestBody = """
            {
              "name": "Yogi",
              "job": "Senior SDET"
            }
            """;

        given()
                .pathParam("id", 2)
                .body(requestBody)
                .when()
                .put("/api/users/{id}")
                .then()
                .spec(successSpec)
                .body("job", equalTo("Senior SDET"))
                .body("updatedAt", notNullValue());
    }
    @Test
    public void deleteUser() {

        given()
                .pathParam("id", 2)
                .when()
                .delete("/api/users/{id}")
                .then()
                .statusCode(204).body(emptyOrNullString());
    }
    @Test
    public void createUserWithResponsePojo(){
        User user= new User("Yogi","SDET");
        CreateUserResponse response= given().body(user)
                .when().post("/api/users")
                .then()
                .statusCode(201)
                .extract().as(CreateUserResponse.class);
        Assert.assertEquals(response.getName(),user.getName());
        Assert.assertEquals(response.getJob(),user.getJob());
        Assert.assertNotNull(response.getId());
        Assert.assertNotNull(response.getCreatedAt());
        System.out.println("Created id: "+ response.getId());
    }
    @Test
    public void getUserWithPojo() {
        GetUserResponse response = given()
                .pathParam("id", 2)
                .when()
                .get("/api/users/{id}")
                .then()
                .spec(successSpec)
                .extract().as(GetUserResponse.class);

        UserData user = response.getData();

        Assert.assertEquals(user.getId(), 2);
        Assert.assertEquals(user.getFirstName(), "Janet");
        Assert.assertTrue(user.getEmail().endsWith("@reqres.in"));

        System.out.println(user.getFirstName() + " " + user.getLastName());
    }
    @Test
    public void loginAndGetToken() {
        String token = given()
                .body("""
            {
              "email": "eve.holt@reqres.in",
              "password": "cityslicka"
            }
            """)
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().path("token");

        System.out.println("Token: " + token);
    }
    @Test
    public void loginWithoutPassword() {
        given()
                .body("""
            {
              "email": "eve.holt@reqres.in"
            }
            """)
                .when()
                .post("/api/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"))
                .body("token", nullValue());
    }

    @Test
    public void loginWithUnknownUser() {
        given()
                .body("""
            {
              "email": "nobody@test.com",
              "password": "abc123"
            }
            """)
                .when()
                .post("/api/login")
                .then()
                .statusCode(400)
                .body("error", notNullValue())
                .body("token", nullValue());

    }
    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLoginData() {
        return new Object[][] {
                // email,                 password,      expected error
                { "eve.holt@reqres.in",   null,          "Missing password" },
                { null,                   "cityslicka",  "Missing email or username" },
                { "nobody@test.com",      "abc123",      "user not found" },
                { "",                     "",            "Missing email or username" },
                {"eve.holt@reqres.in",    "wrongpass123","???"}
        };
    }
    @Test(dataProvider = "invalidLogins")
    public void loginNegative(String email, String password, String expectedError){
        Map<String, String> body = new HashMap<>();
        if (email != null) body.put("email", email);
        if (password != null) body.put("password", password);
        given()
                .body(body)
                .when()
                .post("/api/login")
                .then()
                .statusCode(400)
                .body("error", equalTo(expectedError))
                .body("token", nullValue()).log().body();

    }
    @Test
    public void getUserSchemaValidation() {
        given()
                .pathParam("id", 2)
                .when()
                .get("/api/users/{id}")
                .then()
                .spec(successSpec)
                .body(matchesJsonSchemaInClasspath("schemas/get-user-schema.json"));
    }
}
