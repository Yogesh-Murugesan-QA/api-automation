package utils;

import io.restassured.response.Response;

public class AccountUtils {

    public static int createAccount(String apiKey, String owner, int balance, String currency) {
        String payload = String.format("{ \"owner\": \"%s\", \"balance\": %d, \"currency\": \"%s\" }",
                owner, balance, currency);

        Response res = ApiUtils.post("/api/v1/accounts", apiKey, payload);
        return res.jsonPath().getInt("account.id");
    }

    public static int getBalance(String apiKey, int accountId) {
        Response res = ApiUtils.get("/api/v1/accounts/" + accountId, apiKey);
        return res.jsonPath().getInt("account.balance");
    }
}
