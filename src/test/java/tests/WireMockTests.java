package tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class WireMockTests {

    private WireMockServer kycMock;

    @BeforeClass(alwaysRun = true)
    public void startMock() {
        kycMock = new WireMockServer(options().dynamicPort());
        kycMock.start();
    }

    @AfterMethod(alwaysRun = true)
    public void resetMock() {
        kycMock.resetAll();
    }

    @AfterClass(alwaysRun = true)
    public void stopMock() {
        kycMock.stop();
    }

    @Test(description = "KYC provider returns VERIFIED")
    public void kycSuccess() {
        kycMock.stubFor(WireMock.get(WireMock.urlEqualTo("/kyc/status/123"))
                .willReturn(WireMock.okJson("{\"userId\":\"123\",\"status\":\"VERIFIED\"}")));

        given()
                .baseUri("http://localhost")
                .port(kycMock.port())
                .when()
                .get("/kyc/status/123")
                .then()
                .statusCode(200)
                .body("status", equalTo("VERIFIED"));

        // Check the call actually happened, exactly once
        kycMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/kyc/status/123")));
    }

    @Test(description = "KYC provider is down: returns 500")
    public void kycServerError() {
        kycMock.stubFor(WireMock.get(WireMock.urlEqualTo("/kyc/status/123"))
                .willReturn(WireMock.serverError()
                        .withBody("{\"error\":\"internal error\"}")));

        given()
                .baseUri("http://localhost")
                .port(kycMock.port())
                .when()
                .get("/kyc/status/123")
                .then()
                .statusCode(500);
    }

    @Test(description = "KYC provider is slow: 3 second delay")
    public void kycSlowResponse() {
        kycMock.stubFor(WireMock.get(WireMock.urlEqualTo("/kyc/status/123"))
                .willReturn(WireMock.okJson("{\"status\":\"VERIFIED\"}")
                        .withFixedDelay(3000)));

        Response response = given()
                .baseUri("http://localhost")
                .port(kycMock.port())
                .when()
                .get("/kyc/status/123");

        Assert.assertTrue(response.getTime() >= 3000,
                "Expected at least 3s delay, got " + response.getTime() + "ms");
    }
}