package tests;

import com.mongodb.MongoWriteException;
import org.bson.Document;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.MongoDbUtils;

import java.util.Date;

public class MongoDbTests {

    private String testEmail;

    // Before EACH test: create a fresh user with a unique email
    @BeforeMethod(alwaysRun = true)
    public void createTestUser() {
        testEmail = "qa_" + System.currentTimeMillis() + "@test.com";

        Document user = new Document("name", "Test User")
                .append("job", "QA")
                .append("email", testEmail)
                .append("createdAt", new Date());

        MongoDbUtils.insert("users", user);
    }

    // After EACH test: remove it, even if the test failed
    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        MongoDbUtils.delete("users", "email", testEmail);
    }

    @Test(groups = {"regression"}, description = "Saved user has correct fields")
    public void userSavedWithCorrectFields() {
        Document user = MongoDbUtils.findOne("users", "email", testEmail);

        Assert.assertNotNull(user, "User should exist");
        Assert.assertEquals(user.getString("name"), "Test User");
        Assert.assertEquals(user.getString("job"), "QA");
        Assert.assertNotNull(user.getObjectId("_id"), "Mongo should generate an _id");
        Assert.assertNotNull(user.getDate("createdAt"), "createdAt should be set");
    }

    @Test(groups = {"regression"}, description = "Exactly one document per email")
    public void noDuplicateUsers() {
        long count = MongoDbUtils.count("users", "email", testEmail);
        Assert.assertEquals(count, 1, "Expected exactly one user with this email");
    }

    @Test(groups = {"regression"}, description = "Database rejects duplicate emails")
    public void duplicateEmailRejected() {
        Document duplicate = new Document("name", "Copy")
                .append("job", "QA")
                .append("email", testEmail);

        MongoWriteException error = Assert.expectThrows(MongoWriteException.class,
                () -> MongoDbUtils.insert("users", duplicate));

        Assert.assertEquals(error.getError().getCode(), 11000,
                "Expected duplicate key error");
    }

    @Test(groups = {"regression"}, description = "Deleted user is really gone")
    public void deletedUserIsGone() {
        MongoDbUtils.delete("users", "email", testEmail);

        Assert.assertNull(MongoDbUtils.findOne("users", "email", testEmail),
                "User should no longer exist");
    }
}