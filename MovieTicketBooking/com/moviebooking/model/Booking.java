package com.moviebooking.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Represents a ticket booking made by a user for a specific show.
 *
 * Lifecycle:
 *   PENDING  → seats locked, awaiting payment
 *   CONFIRMED → payment successful, booking complete
 *   CANCELLED → user cancelled or payment failed; seats released
 */
public class Booking {

    private final String        bookingId;
    private final User          user;
    private final Show          show;
    private final List<Seat>    bookedSeats;
    private final double        totalAmount;
    private final LocalDateTime bookingTime;
    private       BookingStatus status;

    public Booking(String bookingId, User user, Show show,
                   List<Seat> bookedSeats, double totalAmount) {
        this.bookingId   = bookingId;
        this.user        = user;
        this.show        = show;
        this.bookedSeats = bookedSeats;
        this.totalAmount = totalAmount;
        this.bookingTime = LocalDateTime.now();
        this.status      = BookingStatus.PENDING;
    }

    // ── State Transitions ─────────────────────────────────────────────────
    public void confirm()  { this.status = BookingStatus.CONFIRMED; }
    public void cancel()   { this.status = BookingStatus.CANCELLED; }

    // ── Getters ─────────────────────────────────────────────────────────────
    public String        getBookingId()   { return bookingId; }
    public User          getUser()        { return user; }
    public Show          getShow()        { return show; }
    public List<Seat>    getBookedSeats() { return Collections.unmodifiableList(bookedSeats); }
    public double        getTotalAmount() { return totalAmount; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public BookingStatus getStatus()      { return status; }

    @Override
    public String toString() {
        return String.format(
                "Booking{id='%s', user='%s', movie='%s', show=%s, seats=%s, amount=%.2f, status=%s}",
                bookingId, user.getName(), show.getMovie().getTitle(),
                show.getShowId(), bookedSeats, totalAmount, status);
    }
}
