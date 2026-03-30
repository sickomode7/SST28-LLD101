package com.moviebooking.service;

import com.moviebooking.model.*;
import com.moviebooking.strategy.PricingStrategy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core service that orchestrates all booking operations.
 *
 * APIs:
 *  - searchShows(movieTitle, location)   → find available shows
 *  - getAvailableSeats(showId)           → list seats still open
 *  - bookTickets(userId, showId, seats)  → lock seats + create booking
 *  - confirmPayment(bookingId)           → pay and confirm booking
 *  - cancelBooking(bookingId)            → release seats + refund
 *  - getBooking(bookingId)               → fetch booking details
 */
public class BookingService {

    private final Map<String, Movie>   movies   = new HashMap<>();
    private final Map<String, Theatre> theatres = new HashMap<>();
    private final Map<String, Show>    shows    = new HashMap<>();
    private final Map<String, User>    users    = new HashMap<>();
    private final Map<String, Booking> bookings = new HashMap<>();
    private final Map<String, Payment> payments = new HashMap<>();

    private final PricingStrategy pricingStrategy;

    private int bookingCounter = 1;
    private int paymentCounter = 1;

    public BookingService(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    // ── Registration APIs (admin setup) ───────────────────────────────────

    public void addMovie(Movie movie)     { movies.put(movie.getMovieId(), movie); }
    public void addTheatre(Theatre theatre) { theatres.put(theatre.getTheatreId(), theatre); }
    public void addShow(Show show)        { shows.put(show.getShowId(), show); }
    public void addUser(User user)        { users.put(user.getUserId(), user); }

    // ── Search ─────────────────────────────────────────────────────────────

    /**
     * Finds all shows for a given movie title at a specific location.
     * Case-insensitive match on both fields.
     */
    public List<Show> searchShows(String movieTitle, String location) {
        String titleLower    = movieTitle.toLowerCase();
        String locationLower = location.toLowerCase();
        return shows.values().stream()
                .filter(s -> s.getMovie().getTitle().toLowerCase().contains(titleLower))
                .filter(s -> s.getTheatre().getLocation().toLowerCase().contains(locationLower))
                .filter(s -> s.availableSeatCount() > 0)
                .collect(Collectors.toList());
    }

    // ── Seat Availability ─────────────────────────────────────────────────

    /** Returns all still-available seats for a given show. */
    public List<Seat> getAvailableSeats(String showId) {
        Show show = getShow(showId);
        return show.getAvailableSeats();
    }

    // ── Booking ───────────────────────────────────────────────────────────

    /**
     * Locks the requested seats and creates a PENDING booking.
     * Payment must follow via {@link #confirmPayment(String)}.
     *
     * @param userId     registered user making the booking
     * @param showId     the show to book for
     * @param seatIds    IDs of the seats the user selected
     * @return           the created (PENDING) Booking
     */
    public Booking bookTickets(String userId, String showId, List<String> seatIds) {
        User user = getUser(userId);
        Show show = getShow(showId);

        // Resolve seat objects from IDs
        Map<String, Seat> screenSeats = show.getScreen().getSeats().stream()
                .collect(Collectors.toMap(Seat::getSeatId, s -> s));

        List<Seat> selectedSeats = new ArrayList<>();
        for (String seatId : seatIds) {
            Seat seat = screenSeats.get(seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Seat not found: " + seatId);
            }
            selectedSeats.add(seat);
        }

        // Lock seats (throws if any already booked)
        show.lockSeats(selectedSeats);

        // Calculate price
        double totalAmount = pricingStrategy.calculatePrice(show, selectedSeats);

        // Create booking
        String bookingId = "BK" + String.format("%04d", bookingCounter++);
        Booking booking  = new Booking(bookingId, user, show, selectedSeats, totalAmount);
        bookings.put(bookingId, booking);

        System.out.printf("  ✅ Booking created: %s | Seats: %s | Amount: ₹%.2f | Status: PENDING%n",
                bookingId, selectedSeats, totalAmount);
        return booking;
    }

    // ── Payment ───────────────────────────────────────────────────────────

    /**
     * Processes payment for a PENDING booking.
     * On success → booking becomes CONFIRMED.
     * On failure → booking is CANCELLED and seats released.
     *
     * @return the processed Payment
     */
    public Payment confirmPayment(String bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot pay for booking in state: " + booking.getStatus());
        }

        String  paymentId = "PAY" + String.format("%04d", paymentCounter++);
        Payment payment   = new Payment(paymentId, booking, booking.getTotalAmount());
        payments.put(paymentId, payment);

        if (payment.processPayment()) {
            booking.confirm();
            System.out.printf("  💳 Payment %s succeeded → Booking %s CONFIRMED%n",
                    paymentId, bookingId);
        } else {
            payment.markFailed();
            booking.cancel();
            booking.getShow().releaseSeats(booking.getBookedSeats());
            System.out.printf("  ❌ Payment %s failed → Booking %s CANCELLED%n",
                    paymentId, bookingId);
        }
        return payment;
    }

    // ── Cancellation ──────────────────────────────────────────────────────

    /**
     * Cancels a PENDING or CONFIRMED booking and releases the locked seats.
     */
    public void cancelBooking(String bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled: " + bookingId);
        }
        booking.cancel();
        booking.getShow().releaseSeats(booking.getBookedSeats());
        System.out.printf("  🔄 Booking %s CANCELLED — seats released%n", bookingId);
    }

    // ── Status Display ────────────────────────────────────────────────────

    /** Prints a summary of available seats per SeatType for a show. */
    public void printShowStatus(String showId) {
        Show show = getShow(showId);
        Map<SeatType, Long> countMap = show.getAvailableSeats().stream()
                .collect(Collectors.groupingBy(Seat::getSeatType, Collectors.counting()));
        System.out.printf("  Show [%s] — %s @ %s%n", showId,
                show.getMovie().getTitle(), show.getStartTime());
        for (SeatType type : SeatType.values()) {
            System.out.printf("    %-10s : %d available%n",
                    type, countMap.getOrDefault(type, 0L));
        }
    }

    // ── Fetch helpers ─────────────────────────────────────────────────────

    public Booking getBooking(String bookingId) {
        Booking b = bookings.get(bookingId);
        if (b == null) throw new IllegalArgumentException("Booking not found: " + bookingId);
        return b;
    }

    private Show getShow(String showId) {
        Show s = shows.get(showId);
        if (s == null) throw new IllegalArgumentException("Show not found: " + showId);
        return s;
    }

    private User getUser(String userId) {
        User u = users.get(userId);
        if (u == null) throw new IllegalArgumentException("User not found: " + userId);
        return u;
    }
}
