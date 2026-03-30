package com.moviebooking.strategy;

import com.moviebooking.model.Seat;
import com.moviebooking.model.Show;

import java.util.List;

/**
 * Fixed per-seat rate based on SeatType.
 *
 * Rates:
 *   STANDARD  → ₹150 / seat
 *   PREMIUM   → ₹250 / seat
 *   RECLINER  → ₹400 / seat
 */
public class StandardPricingStrategy implements PricingStrategy {

    private static final double STANDARD_RATE  = 150.0;
    private static final double PREMIUM_RATE   = 250.0;
    private static final double RECLINER_RATE  = 400.0;

    @Override
    public double calculatePrice(Show show, List<Seat> selectedSeats) {
        double total = 0;
        for (Seat seat : selectedSeats) {
            switch (seat.getSeatType()) {
                case STANDARD : total += STANDARD_RATE;  break;
                case PREMIUM  : total += PREMIUM_RATE;   break;
                case RECLINER : total += RECLINER_RATE;  break;
            }
        }
        return total;
    }
}
