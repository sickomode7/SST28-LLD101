package com.elevator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Represents one elevator cabin in the building.
 *
 * Uses a SCAN-like (look) algorithm internally:
 *  - Maintains two sorted queues — upQueue (floors to visit going up)
 *    and downQueue (floors to visit going down).
 *  - Services all floors in the current direction before switching.
 */
public class Elevator {

    private final String elevatorId;
    private int          currentFloor;
    private Direction    direction;
    private ElevatorState state;

    /** Floors to visit while going UP (sorted ascending). */
    private final TreeSet<Integer> upQueue   = new TreeSet<>();
    /** Floors to visit while going DOWN (sorted descending). */
    private final TreeSet<Integer> downQueue = new TreeSet<>();

    public Elevator(String elevatorId, int startFloor) {
        this.elevatorId   = elevatorId;
        this.currentFloor = startFloor;
        this.direction    = Direction.IDLE;
        this.state        = ElevatorState.IDLE;
    }

    // ── Request Acceptance ────────────────────────────────────────────────────

    /**
     * Adds a floor that this elevator must service.
     * External calls add the source floor; internal calls add the destination.
     */
    public void addFloorToVisit(int floor) {
        if (floor > currentFloor) {
            upQueue.add(floor);
        } else if (floor < currentFloor) {
            downQueue.add(floor);
        } else {
            // Already on the requested floor — open doors immediately (STOPPED).
            state = ElevatorState.STOPPED;
            System.out.printf("  Elevator %s is already on floor %d. Doors open.%n",
                    elevatorId, currentFloor);
        }
        updateDirectionAndState();
    }

    // ── Movement Simulation ──────────────────────────────────────────────────

    /**
     * Moves the elevator one step (one floor) toward the next target.
     * Returns true if movement occurred, false if elevator is IDLE.
     */
    public boolean step() {
        if (upQueue.isEmpty() && downQueue.isEmpty()) {
            state     = ElevatorState.IDLE;
            direction = Direction.IDLE;
            return false;
        }

        if (direction == Direction.UP || direction == Direction.IDLE) {
            if (!upQueue.isEmpty()) {
                int target = upQueue.first();
                if (currentFloor < target) {
                    currentFloor++;
                    state = ElevatorState.MOVING;
                }
                if (currentFloor == target) {
                    upQueue.remove(target);
                    state = ElevatorState.STOPPED;
                    System.out.printf("  [%s] ▲ Reached floor %d — Doors OPEN%n",
                            elevatorId, currentFloor);
                }
            } else {
                // Exhausted upward requests — switch direction
                direction = Direction.DOWN;
                step();
            }
        } else { // Direction.DOWN
            if (!downQueue.isEmpty()) {
                int target = downQueue.last();
                if (currentFloor > target) {
                    currentFloor--;
                    state = ElevatorState.MOVING;
                }
                if (currentFloor == target) {
                    downQueue.remove(target);
                    state = ElevatorState.STOPPED;
                    System.out.printf("  [%s] ▼ Reached floor %d — Doors OPEN%n",
                            elevatorId, currentFloor);
                }
            } else {
                // Exhausted downward requests — switch direction
                direction = Direction.UP;
                step();
            }
        }

        updateDirectionAndState();
        return true;
    }

    /** Runs all pending steps until IDLE (full simulation). */
    public void runUntilIdle() {
        System.out.printf("%n  [%s] Starting from floor %d, direction=%s%n",
                elevatorId, currentFloor, direction);
        int watchdog = 1000; // guard against infinite loops in tests
        while (step() && watchdog-- > 0) { /* keep stepping */ }
        System.out.printf("  [%s] All requests served. Now IDLE at floor %d.%n%n",
                elevatorId, currentFloor);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void updateDirectionAndState() {
        if (!upQueue.isEmpty() || !downQueue.isEmpty()) {
            state = ElevatorState.MOVING;
        }
        if (direction == Direction.IDLE) {
            if (!upQueue.isEmpty())   direction = Direction.UP;
            else if (!downQueue.isEmpty()) direction = Direction.DOWN;
        }
    }

    /** Number of pending floors left to visit (load indicator). */
    public int pendingStops() {
        return upQueue.size() + downQueue.size();
    }

    /** Estimated floors of travel to respond to a given external request. */
    public int estimatedDistance(int requestFloor) {
        return Math.abs(currentFloor - requestFloor);
    }

    /** Returns all pending floors (for status display). */
    public List<Integer> getPendingFloors() {
        List<Integer> all = new ArrayList<>(upQueue);
        all.addAll(downQueue);
        return all;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String        getElevatorId()  { return elevatorId; }
    public int           getCurrentFloor(){ return currentFloor; }
    public Direction     getDirection()   { return direction; }
    public ElevatorState getState()       { return state; }

    @Override
    public String toString() {
        return String.format("Elevator{id='%s', floor=%d, dir=%s, state=%s, pending=%s}",
                elevatorId, currentFloor, direction, state, getPendingFloors());
    }
}
