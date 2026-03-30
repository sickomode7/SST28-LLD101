package com.moviebooking.model;

/**
 * Represents a physical seat inside a cinema screen.
 *
 * Seat availability is managed at the {@link Show} level (per show),
 * not here — so the same seat object is reused across shows.
 */
public class Seat {

    private final String   seatId;
    private final String   seatNumber;   // e.g. "A1", "B12"
    private final SeatType seatType;
    private final int      row;          // numeric row index (for display sorting)

    public Seat(String seatId, String seatNumber, SeatType seatType, int row) {
        this.seatId     = seatId;
        this.seatNumber = seatNumber;
        this.seatType   = seatType;
        this.row        = row;
    }

    // ── Getters ─────────────────────────────────────────────────────────────
    public String   getSeatId()     { return seatId; }
    public String   getSeatNumber() { return seatNumber; }
    public SeatType getSeatType()   { return seatType; }
    public int      getRow()        { return row; }

    @Override
    public String toString() {
        return String.format("Seat{%s [%s]}", seatNumber, seatType);
    }
}
