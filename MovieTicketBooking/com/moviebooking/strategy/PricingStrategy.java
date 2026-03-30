package com.moviebooking.strategy;

import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;

import java.util.List;

/**
 * Strategy interface for computing the total ticket price for a booking.
 *
 * Different implementations can apply flat rates, dynamic pricing,
 * time-based surcharges, or discount logic without changing
 * {@code BookingService}.
 */
public interface PricingStrategy {

    /**
     * Calculates the total price for the selected seats in a given show.
     *
     * @param show          the screening being booked
     * @param selectedSeats the seats the user wants to book
     * @return              total payable amount
     */
    double calculatePrice(Show show, List<Seat> selectedSeats);
}
