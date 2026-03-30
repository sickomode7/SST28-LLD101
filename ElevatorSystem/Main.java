import com.elevator.model.Direction;
import com.elevator.service.ElevatorController;
import com.elevator.strategy.NearestElevatorStrategy;

/**
 * Entry point — demonstrates the elevator system with two scenarios.
 *
 * Building: 10 floors (0 – 9), 2 elevators, all starting at floor 0.
 */
public class Main {

    public static void main(String[] args) {

        ElevatorController controller = new ElevatorController(
                10,                          // total floors
                2,                           // number of elevators
                new NearestElevatorStrategy() // selection strategy
        );

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║        ELEVATOR SYSTEM — LLD DEMO            ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        // ── Initial state ──────────────────────────────────────────────────
        controller.printStatus();

        // ── Scenario 1: external calls ─────────────────────────────────────
        System.out.println("\n▶  SCENARIO 1 — External Floor Requests");
        controller.requestElevator(3, Direction.UP);   // person on floor 3 going up
        controller.requestElevator(7, Direction.DOWN); // person on floor 7 going down
        controller.requestElevator(1, Direction.UP);   // person on floor 1 going up

        controller.printStatus();
        controller.runAll();
        controller.printStatus();

        // ── Scenario 2: internal (cabin) requests ──────────────────────────
        System.out.println("\n▶  SCENARIO 2 — Internal Cabin Requests");

        // Passengers board E1 (now at floor 3) and select destinations
        controller.selectFloor("E1", 6);
        controller.selectFloor("E1", 9);

        // Passengers board E2 (now at floor 7) and select destinations
        controller.selectFloor("E2", 4);
        controller.selectFloor("E2", 2);
        controller.selectFloor("E2", 0);

        controller.printStatus();
        controller.runAll();
        controller.printStatus();

        // ── Scenario 3: mixed (external + internal) concurrent requests ────
        System.out.println("\n▶  SCENARIO 3 — Mixed Concurrent Requests");
        controller.requestElevator(5, Direction.UP);
        controller.selectFloor("E1", 8);
        controller.requestElevator(2, Direction.DOWN);
        controller.selectFloor("E2", 0);

        controller.printStatus();
        controller.runAll();
        controller.printStatus();

        System.out.println("\n✅  All scenarios complete.");
    }
}
