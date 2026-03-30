package com.moviebooking.model;

/**
 * Represents the tier/category of a seat in a cinema screen.
 * Pricing and availability are tracked per SeatType.
 */
public enum SeatType {
    STANDARD,   // regular seats — cheapest
    PREMIUM,    // mid-tier (better view / more legroom)
    RECLINER    // luxury recliners — most expensive
}
