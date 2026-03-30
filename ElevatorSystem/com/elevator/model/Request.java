package com.elevator.model;

/**
 * Represents a passenger request made from a floor panel (external call)
 * or from inside the cabin (internal destination request).
 *
 * External call: a person on floor N presses UP or DOWN.
 * Internal call: a person inside the elevator selects destination floor M
 *                (direction is IDLE — derived at assignment time).
 */
public class Request {

    private final int    sourceFloor;       // floor where button was pressed
    private final int    destinationFloor;  // -1 for external calls (unknown target)
    private final Direction direction;      // UP / DOWN (IDLE for internal)
    private final boolean isInternal;       // true = pressed inside cabin

    /** External floor-panel request (person waiting on a floor). */
    public Request(int sourceFloor, Direction direction) {
        this.sourceFloor      = sourceFloor;
        this.destinationFloor = -1;
        this.direction        = direction;
        this.isInternal       = false;
    }

    /** Internal cabin request (person selects a destination floor). */
    public Request(int sourceFloor, int destinationFloor) {
        this.sourceFloor      = sourceFloor;
        this.destinationFloor = destinationFloor;
        this.direction        = (destinationFloor >= sourceFloor) ? Direction.UP : Direction.DOWN;
        this.isInternal       = true;
    }

    // ── Getters ─────────────────────────────────────────────────────────────
    public int       getSourceFloor()      { return sourceFloor; }
    public int       getDestinationFloor() { return destinationFloor; }
    public Direction getDirection()        { return direction; }
    public boolean   isInternal()          { return isInternal; }

    @Override
    public String toString() {
        if (isInternal) {
            return String.format("InternalRequest{from=%d, to=%d}", sourceFloor, destinationFloor);
        }
        return String.format("ExternalRequest{floor=%d, dir=%s}", sourceFloor, direction);
    }
}
