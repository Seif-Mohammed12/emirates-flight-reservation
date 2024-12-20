package com.example.emirates;

public class VisaPayment extends Payment {

    // =================================================================================
    // Instance Variables
    // =================================================================================
    private String cardNumber;
    private String expiryDate;
    private String cvv;

    // =================================================================================
    // Constructor
    // =================================================================================
    public VisaPayment(String cardNumber, String expiryDate, String cvv) {
        super("Visa");
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    // =================================================================================
    // Payment Processing Methods
    // =================================================================================
    @Override
    public boolean processPayment(double amount) {
        if (!isValidCardNumber(cardNumber)) {
            System.out.println("Invalid card number.");
            return false;
        }

        double keemamodafaFee = calculateKeemamodafaFee(amount);
        double serviceFee = calculateServiceFee(amount);
        double totalAmount = amount + keemamodafaFee + serviceFee;

        System.out.println("Processing Visa payment...");
        System.out.println("Card Number: " + cardNumber);
        System.out.println("Total Amount (including fees): EGP " + String.format("%.2f", totalAmount));

        return true;
    }

    public static double calculateRefundAmount(double amount) {
        double cancellationFee = calculateCancellationFee(amount);
        return amount - cancellationFee;
    }

    // =================================================================================
    // Validation Methods
    // =================================================================================
    private boolean isValidCardNumber(String cardNumber) {
        String cardRegex = "^(?:4[0-9]{12}(?:[0-9]{3})?" + // Visa
                "|5[1-5][0-9]{14}" + // MasterCard
                "|3[47][0-9]{13}" + // American Express
                "|6(?:011|5[0-9]{2})[0-9]{12}" + // Discover
                "|(?:2131|1800|35\\d{3})\\d{11})$"; // JCB
        return cardNumber != null && cardNumber.matches(cardRegex);
    }
}
