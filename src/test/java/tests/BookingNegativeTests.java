package tests;

import api.BookingApi;
import base.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.Booking;
import pojo.BookingDates;

import static org.hamcrest.Matchers.equalTo;

public class BookingNegativeTests extends BaseTest {

    private int bookingId;
    private String validToken;

    // Runs once before the tests: create a booking to test against
    @BeforeClass(alwaysRun = true)
    public void createTestBooking() {
        validToken = BookingApi.getToken();

        Booking booking = new Booking("Neg", "Test", 1000, true,
                new BookingDates("2026-12-10", "2026-12-12"), "None");

        bookingId = BookingApi.createBooking(booking)
                .then().statusCode(200)
                .extract().path("bookingid");
    }

    @DataProvider(name = "invalidTokens")
    public Object[][] invalidTokens() {
        return new Object[][] {
                { null,          "no token" },
                { "",            "empty token" },
                { "abc123wrong", "wrong token" }
        };
    }

    @Test(groups = {"regression"}, dataProvider = "invalidTokens",
            description = "Update must be rejected without a valid token")
    public void updateWithInvalidToken(String token, String description) {
        Booking hacked = new Booking("Hacked", "User", 9999, false,
                new BookingDates("2026-12-10", "2026-12-12"), "None");

        BookingApi.updateBooking(bookingId, hacked, token)
                .then().log().ifValidationFails()
                .statusCode(403);

        // The real check: did the rejected update secretly apply?
        BookingApi.getBooking(bookingId)
                .then().statusCode(200)
                .body("firstname", equalTo("Neg"))
                .body("totalprice", equalTo(1000));
    }

    @Test(groups = {"regression"}, dataProvider = "invalidTokens",
            description = "Delete must be rejected without a valid token")
    public void deleteWithInvalidToken(String token, String description) {
        BookingApi.deleteBooking(bookingId, token)
                .then().log().ifValidationFails()
                .statusCode(403);

        // The booking must still exist
        BookingApi.getBooking(bookingId)
                .then().statusCode(200);
    }

    @Test(groups = {"regression"},
            description = "Getting a booking that doesn't exist returns 404")
    public void getNonExistentBooking() {
        BookingApi.getBooking(999999999)
                .then().log().ifValidationFails()
                .statusCode(404);
    }

    // Runs once after all tests: clean up what we created
    @AfterClass(alwaysRun = true)
    public void cleanup() {
        BookingApi.deleteBooking(bookingId, validToken);
    }
}