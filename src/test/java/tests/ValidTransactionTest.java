package tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import tests.base.BaseTest;
import utils.AccountUtils;
import utils.TransactionUtils;

import static io.restassured.RestAssured.given;

public class ValidTransactionTest extends BaseTest {
    private String apiKey;
    private int fromAccountId;
    private int toAccountId;

    @Test(priority = 1)
    public void generateApiKey() {
        Response res = given()
                .get("/api/v1/auth")
                .then()
                .statusCode(200)
                .extract().response();

        apiKey = res.jsonPath().getString("apiKey");
        Assert.assertNotNull(apiKey, "API Key should not be null");
    }

    @Test(priority = 2)
    public void createFromAccount() {
        fromAccountId = AccountUtils.createAccount(apiKey, "User-From", 50, "COSMIC_COINS");
        Assert.assertTrue(fromAccountId > 0);
    }

    @Test(priority = 3)
    public void createToAccount() {
        toAccountId = AccountUtils.createAccount(apiKey, "User-To", 0, "COSMIC_COINS");
        Assert.assertTrue(toAccountId > 0);
    }

    @Test(priority = 4)
    public void createTransaction() {
        Response res = (Response) TransactionUtils.createTransaction(apiKey, fromAccountId, toAccountId, 20, "COSMIC_COINS");
        Assert.assertTrue(res.jsonPath().getBoolean("success"));
    }

    @Test(priority = 5)
    public void validateBalances() {
        int toBalance = AccountUtils.getBalance(apiKey, toAccountId);
        Assert.assertEquals(toBalance, 20);

        int fromBalance = AccountUtils.getBalance(apiKey, fromAccountId);
        Assert.assertEquals(fromBalance, 30);
    }
}
