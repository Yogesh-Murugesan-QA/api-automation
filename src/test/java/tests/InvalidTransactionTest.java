package tests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import tests.base.BaseTest;
import utils.ApiUtils;
import utils.AccountUtils;
import utils.TransactionUtils;

public class InvalidTransactionTest extends BaseTest {
    private String apiKey;
    private int fromAccountId;
    private int toAccountId;

    @Test(priority = 1)
    public void generateApiKey() {
        Response res = ApiUtils.get("/api/v1/auth", null); // No apiKey needed for auth
        apiKey = res.jsonPath().getString("apiKey");
        Assert.assertNotNull(apiKey, "API Key should not be null");
    }

    @Test(priority = 2)
    public void createFromAccountWithZeroBalance() {
        fromAccountId = AccountUtils.createAccount(apiKey, "User-From", 0, "COSMIC_COINS");
        Assert.assertTrue(fromAccountId > 0, "From account creation failed");
    }

    @Test(priority = 3)
    public void createToAccount() {
        toAccountId = AccountUtils.createAccount(apiKey, "User-To", 0, "COSMIC_COINS");
        Assert.assertTrue(toAccountId > 0, "To account creation failed");
    }

    @Test(priority = 4)
    public void attemptInvalidTransaction() {
        Response res = TransactionUtils.createTransaction(apiKey, fromAccountId, toAccountId, 100, "COSMIC_COINS");

        Assert.assertEquals(res.statusCode(), 400, "Status code mismatch");
        System.out.println("Response body: " + res.asString()); // Debug: print full response

        String errorMsg = res.jsonPath().getString("message");
        Assert.assertNotNull(errorMsg, "API did not return a 'message' field. Response: " + res.asString());
        Assert.assertTrue(errorMsg.toLowerCase().contains("insufficient"), "Error message mismatch");
    }
}
