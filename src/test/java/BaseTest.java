import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;

import java.util.HashMap;
import java.util.Map;
import io.qameta.allure.restassured.AllureRestAssured;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
public class BaseTest {
    protected ResponseSpecification successSpec;
    protected String token;
    @BeforeClass(alwaysRun = true)
    public void setup(){
        RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig()
                        .blacklistHeader("Authorization", "x-api-key"));

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("base.uri"))
                .addHeader("x-api-key",ConfigReader.get("api.key"))
                .setContentType(ContentType.JSON).addFilter(new AllureRestAssured());
        RestAssured.requestSpecification = builder.build();
        token=getToken();
        builder.addHeader("Authorization", "Bearer " + token);
        RestAssured.requestSpecification = builder.build();

        successSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(3000L))
                .build();
    }
    private String getToken() {
        Map<String, String> creds = new HashMap<>();
        creds.put("email", ConfigReader.get("login.email"));
        creds.put("password", ConfigReader.get("login.password"));

        return given()
                .body(creds)
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .extract().path("token");
    }

}
