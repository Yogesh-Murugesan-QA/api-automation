package pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateBookingResponse {

    private int bookingid;
    private Booking booking;

    public CreateBookingResponse() {
    }

    public int getBookingid() { return bookingid; }
    public void setBookingid(int bookingid) { this.bookingid = bookingid; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
}