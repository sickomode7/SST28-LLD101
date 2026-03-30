package com.moviebooking.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a single screening (show) of a movie on a specific screen.
 *
 * Key design decision: seat availability is tracked in a per-show map
 * ({@code seatAvailability}), NOT on the Seat object itself. This allows
 * the same screen / seat objects to be reused across multiple shows without
 * interference.
 */
public class Show {

    private final String        showId;
    private final Movie         movie;
    private final Screen        screen;
    private final Theatre       theatre;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    /** true = available, false = booked/locked. */
    private final Map<String, Boolean> seatAvailability;  // key = seatId

    public Show(String showId, Movie movie, Screen screen,
                Theatre theatre, LocalDateTime startTime) {
        this.showId    = showId;
        this.movie     = movie;
        this.screen    = screen;
        this.theatre   = theatre;
        this.startTime = startTime;
        this.endTime   = startTime.plusMinutes(movie.getDurationMinutes());

        // All seats start as available
        this.seatAvailability = new HashMap<>();
        for (Seat seat : screen.getSeats()) {
            seatAvailability.put(seat.getSeatId(), true);
        }
    }

    // ── Seat Availability Operations ──────────────────────────────────────

    public boolean isSeatAvailable(String seatId) {
        return seatAvailability.getOrDefault(seatId, false);
    }

    /**
     * Locks (marks as unavailable) the requested seats.
     * Throws if any seat is already taken.
     */
    public void lockSeats(List<Seat> seats) {
        for (Seat seat : seats) {
            if (!isSeatAvailable(seat.getSeatId())) {
                throw new IllegalStateException(
                        "Seat " + seat.getSeatNumber() + " is already booked for show " + showId);
            }
        }
        seats.forEach(s -> seatAvailability.put(s.getSeatId(), false));
    }

    /** Releases seats back to available (on cancellation). */
    public void releaseSeats(List<Seat> seats) {
        seats.forEach(s -> seatAvailability.put(s.getSeatId(), true));
    }

    /** Returns all seats that are currently available. */
    public List<Seat> getAvailableSeats() {
        return screen.getSeats().stream()
                .filter(s -> isSeatAvailable(s.getSeatId()))
                .collect(Collectors.toList());
    }

    public int availableSeatCount() {
        return (int) seatAvailability.values().stream().filter(v -> v).count();
    }

    // ── Getters ─────────────────────────────────────────────────────────────
    public String        getShowId()    { return showId; }
    public Movie         getMovie()     { return movie; }
    public Screen        getScreen()    { return screen; }
    public Theatre       getTheatre()   { return theatre; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime()   { return endTime; }

    @Override
    public String toString() {
        return String.format("Show{id='%s', movie='%s', theatre='%s', screen='%s', start=%s, available=%d}",
                showId, movie.getTitle(), theatre.getName(),
                screen.getScreenName(), startTime, availableSeatCount());
    }
}
