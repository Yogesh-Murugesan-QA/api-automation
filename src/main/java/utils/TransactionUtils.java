package utils;

import io.restassured.response.Response;

public class TransactionUtils {

    public static Response createTransaction(String apiKey, int fromId, int toId, int amount, String currency) {
        String payload = String.format("{ \"fromAccountId\": %d, \"toAccountId\": %d, \"amount\": %d, \"currency\": \"%s\" }",
                fromId, toId, amount, currency);
        return ApiUtils.post("/api/v1/transactions", apiKey, payload);
    }
}
