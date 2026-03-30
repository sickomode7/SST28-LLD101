package com.moviebooking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a cinema screen inside a theatre.
 * A screen has a fixed set of seats; multiple shows can run on it
 * at different time slots.
 */
public class Screen {

    private final String     screenId;
    private final String     screenName;
    private final List<Seat> seats;

    public Screen(String screenId, String screenName) {
        this.screenId      = screenId;
        this.screenName    = screenName;
        this.seats         = new ArrayList<>();
    }

    /** Adds a seat to this screen (called during setup). */
    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    /** Total seats defined in this screen. */
    public int getTotalCapacity() { return seats.size(); }

    // ── Getters ─────────────────────────────────────────────────────────────
    public String     getScreenId()   { return screenId; }
    public String     getScreenName() { return screenName; }
    public List<Seat> getSeats()      { return Collections.unmodifiableList(seats); }

    @Override
    public String toString() {
        return String.format("Screen{id='%s', name='%s', seats=%d}",
                screenId, screenName, seats.size());
    }
}
