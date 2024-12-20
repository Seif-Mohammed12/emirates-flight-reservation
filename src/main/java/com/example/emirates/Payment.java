package com.example.emirates;

public abstract class Payment {
    // =================================================================================
    // Fields
    // =================================================================================
    protected String paymentType;

    // =================================================================================
    // Constructor
    // =================================================================================
    public Payment(String paymentType) {
        this.paymentType = paymentType;
    }

    // =================================================================================
    // Getters
    // =================================================================================
    public String getPaymentType() {
        return paymentType;
    }

    // =================================================================================
    // Abstract Methods
    // =================================================================================
    public abstract boolean processPayment(double amount);

    // =================================================================================
    // Fee Calculation Methods
    // =================================================================================
    protected double calculateKeemamodafaFee(double amount) {
        double taxRate = 0.14;
        return amount * taxRate;
    }

    protected double calculateServiceFee(double amount) {
        double serviceFeeRate = 0.10;
        return amount * serviceFeeRate;
    }

    protected static double calculateCancellationFee(double amount) {
        double cancellationFeeRate = 0.10;
        return amount * cancellationFeeRate;
    }
}
