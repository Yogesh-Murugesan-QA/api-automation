package specs;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import utils.ConfigReader;

public class BookerSpec {

    public static RequestSpecification base() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("booker.base.uri"))
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .addFilter(new AllureRestAssured())
                .build();
    }
}