package com.moviebooking.model;

import java.time.LocalDateTime;

/**
 * Represents a payment transaction associated with a booking.
 */
public class Payment {

    private final String        paymentId;
    private final Booking       booking;
    private final double        amount;
    private final LocalDateTime paymentTime;
    private       PaymentStatus paymentStatus;

    public Payment(String paymentId, Booking booking, double amount) {
        this.paymentId     = paymentId;
        this.booking       = booking;
        this.amount        = amount;
        this.paymentTime   = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDING;
    }

    /** Simulates processing — always succeeds in this demo. */
    public boolean processPayment() {
        // In production: call payment gateway here
        this.paymentStatus = PaymentStatus.SUCCESS;
        return true;
    }

    public void markFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    // ── Getters ─────────────────────────────────────────────────────────────
    public String        getPaymentId()     { return paymentId; }
    public Booking       getBooking()       { return booking; }
    public double        getAmount()        { return amount; }
    public LocalDateTime getPaymentTime()   { return paymentTime; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }

    @Override
    public String toString() {
        return String.format("Payment{id='%s', amount=%.2f, status=%s}",
                paymentId, amount, paymentStatus);
    }
}
