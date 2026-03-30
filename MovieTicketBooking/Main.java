import com.moviebooking.model.*;
import com.moviebooking.service.BookingService;
import com.moviebooking.strategy.StandardPricingStrategy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Entry point — demonstrates the Movie Ticket Booking system
 * with three real-world scenarios.
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Initialise service ─────────────────────────────────────────
        BookingService service = new BookingService(new StandardPricingStrategy());

        // ── 2. Set up movies ──────────────────────────────────────────────
        Movie movie1 = new Movie("M01", "Dune: Part Two", "Sci-Fi",  "English", 166);
        Movie movie2 = new Movie("M02", "Pushpa 2",       "Action",  "Telugu",  210);
        service.addMovie(movie1);
        service.addMovie(movie2);

        // ── 3. Set up theatres & screens ──────────────────────────────────
        Theatre theatre1 = new Theatre("T01", "PVR Nexus", "Bangalore");
        Screen  screen1  = new Screen("S01", "Screen 1 - Audi");

        // Add seats to Screen 1
        String[] rows1 = {"A","B","C"};
        for (int r = 0; r < rows1.length; r++) {
            SeatType type = (r == 2) ? SeatType.PREMIUM : SeatType.STANDARD;
            for (int c = 1; c <= 5; c++) {
                screen1.addSeat(new Seat(rows1[r] + c, rows1[r] + c, type, r));
            }
        }

        // Add seats to Screen 2 (mix of all types)
        Screen screen2Gold = new Screen("S02", "Screen 2 - Gold");
        String[] rows2 = {"R1","R2","R3"};
        SeatType[] types  = {SeatType.STANDARD, SeatType.PREMIUM, SeatType.RECLINER};
        for (int r = 0; r < rows2.length; r++) {
            for (int c = 1; c <= 4; c++) {
                screen2Gold.addSeat(new Seat(rows2[r] + "-" + c, rows2[r] + "-" + c, types[r], r));
            }
        }

        theatre1.addScreen(screen1);
        theatre1.addScreen(screen2Gold);
        service.addTheatre(theatre1);

        // ── 4. Create shows ───────────────────────────────────────────────
        LocalDateTime show1Start = LocalDateTime.of(2026, 4, 1, 10, 0);
        LocalDateTime show2Start = LocalDateTime.of(2026, 4, 1, 14, 30);
        LocalDateTime show3Start = LocalDateTime.of(2026, 4, 1, 18, 0);

        Show show1 = new Show("SH01", movie1, screen1,     theatre1, show1Start);
        Show show2 = new Show("SH02", movie2, screen2Gold, theatre1, show2Start);
        Show show3 = new Show("SH03", movie1, screen2Gold, theatre1, show3Start);
        service.addShow(show1);
        service.addShow(show2);
        service.addShow(show3);

        // ── 5. Register users ─────────────────────────────────────────────
        User alice = new User("U01", "Alice", "alice@example.com", "9876543210");
        User bob   = new User("U02", "Bob",   "bob@example.com",   "9123456789");
        service.addUser(alice);
        service.addUser(bob);

        // ═══════════════════════════════════════════════════════════════════
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║    MOVIE TICKET BOOKING SYSTEM — LLD DEMO        ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // ── Scenario 1: Search + Book + Pay ──────────────────────────────
        System.out.println("\n▶  SCENARIO 1 — Alice books 2 STANDARD + 1 PREMIUM seat for Dune");
        System.out.println("\n  Search: 'Dune' in 'Bangalore'");
        List<Show> results = service.searchShows("Dune", "Bangalore");
        results.forEach(s -> System.out.println("    Found: " + s));

        System.out.println("\n  Available seats for SH01:");
        service.printShowStatus("SH01");

        System.out.println("\n  Alice selects seats A1, A2 (STANDARD) and C1 (PREMIUM):");
        Booking booking1 = service.bookTickets("U01", "SH01",
                Arrays.asList("A1", "A2", "C1"));

        System.out.println("\n  Processing payment:");
        service.confirmPayment(booking1.getBookingId());

        System.out.println("\n  Show status after booking:");
        service.printShowStatus("SH01");

        // ── Scenario 2: Bob books recliners ──────────────────────────────
        System.out.println("\n▶  SCENARIO 2 — Bob books 2 RECLINER seats for Pushpa 2");
        System.out.println("\n  Available seats for SH02:");
        service.printShowStatus("SH02");

        System.out.println("\n  Bob selects recliners R3-1 and R3-2:");
        Booking booking2 = service.bookTickets("U02", "SH02",
                Arrays.asList("R3-1", "R3-2"));

        System.out.println("\n  Processing payment:");
        service.confirmPayment(booking2.getBookingId());

        System.out.println("\n  Show status after booking:");
        service.printShowStatus("SH02");

        // ── Scenario 3: Book then cancel ─────────────────────────────────
        System.out.println("\n▶  SCENARIO 3 — Alice books then cancels a seat for Dune (SH03)");
        Booking booking3 = service.bookTickets("U01", "SH03",
                Arrays.asList("R1-1", "R1-2"));
        System.out.println("\n  Alice cancels before paying:");
        service.cancelBooking(booking3.getBookingId());

        System.out.println("\n  Show status after cancellation (seats released):");
        service.printShowStatus("SH03");

        // ── Scenario 4: Concurrent conflict ──────────────────────────────
        System.out.println("\n▶  SCENARIO 4 — Conflict: Bob tries to book A3 already taken");
        Booking booking4 = service.bookTickets("U02", "SH01", Arrays.asList("A3"));
        service.confirmPayment(booking4.getBookingId());

        System.out.println("\n  Alice also tries A3 (should throw):");
        try {
            service.bookTickets("U01", "SH01", Arrays.asList("A3"));
        } catch (IllegalStateException e) {
            System.out.println("  ⚠ " + e.getMessage());
        }

        System.out.println("\n✅  All scenarios complete.");
    }
}
