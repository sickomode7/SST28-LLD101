package com.elevator.service;

import com.elevator.model.Direction;
import com.elevator.model.Elevator;
import com.elevator.model.Request;
import com.elevator.strategy.ElevatorSelectionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central controller (dispatcher) for the elevator system.
 *
 * Responsibilities:
 *  - Maintains the list of all elevators in the building.
 *  - Accepts external requests (from floor panels) and dispatches them
 *    to the most suitable elevator via the injected {@link ElevatorSelectionStrategy}.
 *  - Accepts internal requests (from inside a cabin) and routes them to
 *    the correct elevator by ID.
 *  - Provides a status view of the entire system.
 */
public class ElevatorController {

    private final List<Elevator>             elevators;
    private final ElevatorSelectionStrategy  selectionStrategy;
    private final int                        totalFloors;

    public ElevatorController(int totalFloors,
                              int numberOfElevators,
                              ElevatorSelectionStrategy selectionStrategy) {
        if (totalFloors < 1 || numberOfElevators < 1) {
            throw new IllegalArgumentException(
                    "Building must have at least 1 floor and 1 elevator.");
        }
        this.totalFloors       = totalFloors;
        this.selectionStrategy = selectionStrategy;
        this.elevators         = new ArrayList<>();

        for (int i = 1; i <= numberOfElevators; i++) {
            elevators.add(new Elevator("E" + i, 0)); // all start at ground floor
        }
    }

    // ── External Request (Floor Panel) ────────────────────────────────────────

    /**
     * Handles a button press on a floor panel (someone calling an elevator).
     *
     * @param floor     floor where the button was pressed  (0-indexed)
     * @param direction UP or DOWN
     */
    public void requestElevator(int floor, Direction direction) {
        validateFloor(floor);
        Request request = new Request(floor, direction);
        System.out.printf("%nExternal request: Floor %d [%s]%n", floor, direction);

        Elevator chosen = selectionStrategy.selectElevator(elevators, request);
        if (chosen == null) {
            System.out.println("  ⚠ No elevator available to handle the request.");
            return;
        }
        System.out.printf("  → Dispatching %s to floor %d%n",
                chosen.getElevatorId(), floor);
        chosen.addFloorToVisit(floor);
    }

    // ── Internal Request (Cabin Button) ──────────────────────────────────────

    /**
     * Handles a destination button pressed inside a specific elevator cabin.
     *
     * @param elevatorId  ID of the elevator (e.g., "E1")
     * @param destination target floor selected by the passenger
     */
    public void selectFloor(String elevatorId, int destination) {
        validateFloor(destination);
        Elevator elevator = findById(elevatorId);
        System.out.printf("%nInternal request in %s: Destination floor %d%n",
                elevatorId, destination);
        elevator.addFloorToVisit(destination);
    }

    // ── Simulation ────────────────────────────────────────────────────────────

    /**
     * Runs the simulation for all elevators until all pending requests are served.
     */
    public void runAll() {
        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("  SIMULATION START");
        System.out.println("═══════════════════════════════════════════");
        for (Elevator elevator : elevators) {
            if (!elevator.getPendingFloors().isEmpty()) {
                elevator.runUntilIdle();
            }
        }
        System.out.println("═══════════════════════════════════════════");
        System.out.println("  SIMULATION COMPLETE");
        System.out.println("═══════════════════════════════════════════");
    }

    // ── Status ───────────────────────────────────────────────────────────────

    /**
     * Prints a summary of every elevator's current state.
     */
    public void printStatus() {
        System.out.println("\n──────────────── Elevator Status ─────────────────");
        System.out.printf("  Building: %d floors | %d elevators%n",
                totalFloors, elevators.size());
        for (Elevator e : elevators) {
            System.out.printf("  %-4s │ Floor: %-3d │ Dir: %-5s │ State: %-8s │ Pending: %s%n",
                    e.getElevatorId(),
                    e.getCurrentFloor(),
                    e.getDirection(),
                    e.getState(),
                    e.getPendingFloors().isEmpty() ? "none" : e.getPendingFloors());
        }
        System.out.println("───────────────────────────────────────────────────");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void validateFloor(int floor) {
        if (floor < 0 || floor >= totalFloors) {
            throw new IllegalArgumentException(
                    String.format("Floor %d is out of range [0, %d].", floor, totalFloors - 1));
        }
    }

    private Elevator findById(String elevatorId) {
        return elevators.stream()
                .filter(e -> e.getElevatorId().equals(elevatorId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No elevator with ID: " + elevatorId));
    }

    /** Exposes an unmodifiable view of all elevators (useful for tests). */
    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(elevators);
    }
}
