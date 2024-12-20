package com.example.emirates;

public class DigitalWalletPayment extends Payment {
    // =================================================================================
    // Fields
    // =================================================================================
    private String walletId;

    // =================================================================================
    // Constructor
    // =================================================================================
    public DigitalWalletPayment(String walletId) {
        super("Digital Wallet");
        this.walletId = walletId;
    }

    // =================================================================================
    // Payment Processing Methods
    // =================================================================================
    @Override
    public boolean processPayment(double amount) {
        if (!isValidWalletId(walletId)) {
            System.out.println("Invalid wallet ID.");
            return false;
        }

        double keemamodafaFee = calculateKeemamodafaFee(amount);
        double serviceFee = calculateServiceFee(amount);
        double totalAmount = amount + keemamodafaFee + serviceFee;

        System.out.println("Simulating InstaPay payment...");
        System.out.println("Wallet ID: " + walletId);
        System.out.println("Wallet Provider: InstaPay");
        System.out.println("Total Amount (including fees): EGP " + String.format("%.2f", totalAmount));
        System.out.println("Payment successfully processed!");

        return true;
    }

    public static double calculateRefundAmount(double amount) {
        double cancellationFee = calculateCancellationFee(amount);
        return amount - cancellationFee;
    }

    // =================================================================================
    // Validation Methods
    // =================================================================================
    private boolean isValidWalletId(String walletId) {
        // Simulating wallet ID validation with a basic alphanumeric regex
        String walletIdRegex = "^[a-zA-Z0-9]{5,20}$"; // Assuming a simple alphanumeric ID
        return walletId != null && walletId.matches(walletIdRegex);
    }
}
