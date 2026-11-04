package data;

import org.testng.annotations.DataProvider;

public class UserTestData {
    @DataProvider(name="invalidCreateUser")
    public static Object[][] invalidCreateUserData() {
        return new Object[][] {
                { null, "SDET", 201, "name missing" },
                { "Yogi", null, 201, "Job missing" }
        };
}
}
