package com.elevator.strategy;

import com.elevator.model.Elevator;
import com.elevator.model.Request;

import java.util.List;

/**
 * Strategy interface for selecting the best elevator to service an external request.
 *
 * Different implementations can provide different scheduling policies
 * (e.g., nearest elevator, least-loaded, round-robin) without changing
 * {@code ElevatorController}.
 */
public interface ElevatorSelectionStrategy {

    /**
     * Selects the most suitable elevator from the given list to serve the request.
     *
     * @param elevators  all available elevators in the building
     * @param request    the external floor-panel request to be dispatched
     * @return           the chosen {@link Elevator}, or {@code null} if none are available
     */
    Elevator selectElevator(List<Elevator> elevators, Request request);
}
