package com.moviebooking.model;

/**
 * Lifecycle states of a booking.
 */
public enum BookingStatus {
    PENDING,    // seats locked, payment not yet made
    CONFIRMED,  // payment successful
    CANCELLED   // cancelled by user or timeout
}
