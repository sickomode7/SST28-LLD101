package com.elevator.strategy;

import com.elevator.model.Direction;
import com.elevator.model.Elevator;
import com.elevator.model.Request;

import java.util.List;

/**
 * Selects the elevator that can respond fastest to an external request.
 *
 * Priority (highest → lowest):
 *  1. An elevator already moving in the same direction AND on the way to the request floor.
 *  2. An IDLE elevator — pick the physically nearest one.
 *  3. Any elevator — pick the one with fewest pending stops (least loaded).
 */
public class NearestElevatorStrategy implements ElevatorSelectionStrategy {

    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        Elevator best        = null;
        int      bestScore   = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int score = score(elevator, request);
            if (score < bestScore) {
                bestScore = score;
                best      = elevator;
            }
        }
        return best;
    }

    /**
     * Lower score → better candidate.
     *
     * Score breakdown:
     *  - Same-direction, heading toward floor : distance
     *  - IDLE                                  : distance + small flat penalty (5)
     *  - Opposite direction / moving away      : distance + heavy penalty (100)
     */
    private int score(Elevator elevator, Request request) {
        int distance = Math.abs(elevator.getCurrentFloor() - request.getSourceFloor());

        switch (elevator.getDirection()) {
            case IDLE:
                return distance + 5;

            case UP:
                if (request.getDirection() == Direction.UP
                        && elevator.getCurrentFloor() <= request.getSourceFloor()) {
                    // On the way — best case
                    return distance;
                }
                break;

            case DOWN:
                if (request.getDirection() == Direction.DOWN
                        && elevator.getCurrentFloor() >= request.getSourceFloor()) {
                    // On the way — best case
                    return distance;
                }
                break;

            default:
                break;
        }

        // Moving away or in opposite direction — penalise heavily
        return distance + 100 + elevator.pendingStops() * 2;
    }
}
