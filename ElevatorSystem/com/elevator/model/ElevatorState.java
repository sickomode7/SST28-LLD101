package com.elevator.model;

/**
 * Represents the operational state of an elevator cabin.
 *
 * MOVING  — doors are closed, elevator is travelling between floors.
 * STOPPED — elevator has reached a requested floor; doors open briefly.
 * IDLE    — elevator has no pending requests and is waiting.
 */
public enum ElevatorState {
    MOVING,
    STOPPED,
    IDLE
}
