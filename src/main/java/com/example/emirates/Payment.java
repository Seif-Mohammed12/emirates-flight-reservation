package com.example.emirates;

public abstract class Payment {
    protected String paymentType;

    public Payment(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public abstract boolean processPayment(double amount);

    protected double calculateKeemamodafaFee(double amount) {
        double taxRate = 0.14;
        return amount * taxRate;
    }

    protected double calculateServiceFee(double amount) {
        double serviceFeeRate = 0.10;
        return amount * serviceFeeRate;
    }

    protected double calculateCancellationFee(double amount) {
        double cancellationFeeRate = 0.10;
        return amount * cancellationFeeRate;
    }
}