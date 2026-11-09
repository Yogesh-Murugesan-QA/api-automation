package tests;

import api.BookingApi;
import base.BaseTest;
import org.testng.annotations.Test;
import pojo.Booking;
import pojo.BookingDates;
import pojo.CreateBookingResponse;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class BookingFlowTests extends BaseTest {

    @Test(groups = {"regression"},
            description = "Booking lifecycle: create, read, update, delete")
    public void bookingLifecycle() {
        String token = BookingApi.getToken();

        // 1. Create
        Booking booking = new Booking("Yogi", "M", 1500, true,
                new BookingDates("2026-12-01", "2026-12-05"), "Breakfast");

        CreateBookingResponse created = BookingApi.createBooking(booking)
                .then().log().ifValidationFails()
                .statusCode(200)
                .extract().as(CreateBookingResponse.class);

        int id = created.getBookingid();
        assertTrue(id > 0, "Booking id should be generated");
        assertEquals(created.getBooking().getFirstname(), booking.getFirstname());

        // 2. Read it back: did it really save?
        Booking fetched = BookingApi.getBooking(id)
                .then().log().ifValidationFails()
                .statusCode(200)
                .extract().as(Booking.class);

        assertEquals(fetched.getLastname(), booking.getLastname());
        assertEquals(fetched.getTotalprice(), booking.getTotalprice());
        assertEquals(fetched.getBookingdates().getCheckin(), "2026-12-01");

        // 3. Update
        booking.setTotalprice(2000);
        booking.setAdditionalneeds("Late checkout");

        BookingApi.updateBooking(id, booking, token)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("totalprice", equalTo(2000));

        // 4. Read again: did the update stick?
        BookingApi.getBooking(id)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("totalprice", equalTo(2000))
                .body("additionalneeds", equalTo("Late checkout"));

        // 5. Delete
        BookingApi.deleteBooking(id, token)
                .then().log().ifValidationFails()
                .statusCode(201);

        // 6. Read again: is it really gone?
        BookingApi.getBooking(id)
                .then().log().ifValidationFails()
                .statusCode(404);
    }
}