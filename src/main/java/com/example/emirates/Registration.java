package com.example.emirates;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Registration {
    private static String phoneNumber;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String firstName = getValidatedInput(scanner, "Enter First Name: ");
        String lastName = getValidatedInput(scanner, "Enter Last Name: ");
        String email = getValidatedEmail(scanner);
        String phoneNumber = getValidatedPhoneNumber(scanner);
        String zipCode = getValidatedInput(scanner, "Enter Zip Code: ");
        String address = getValidatedInput(scanner, "Enter Address: ");
        String username = getValidatedUsername(scanner);
        String password = getValidatedPassword(scanner);

        String role = "user";

        User newUser = new User(firstName, lastName, email, phoneNumber, zipCode, address, username, password, role);

        try (FileWriter writer = new FileWriter("login.txt", true)) {
            writer.write(newUser.toString() + "\n");
            System.out.println("Registration successful, Welcome to EgyptAir");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private static String getValidatedInput(Scanner scanner, String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine();
        } while (input.isEmpty());
        return input;
    }

    private static String getValidatedEmail(Scanner scanner) {
        String email;
        do {
            System.out.print("""
                    Enter email \t  Preferably to end with @gmail.com, @yahoo.com, or @outlook.com):\s""");
            email = scanner.nextLine();
        } while (!isValidEmail(email));
        return email;

    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[a-zA-Z]{2,4}$";
        return email.matches(emailRegex);
    }

    private static String getValidatedPhoneNumber(Scanner scanner) {
        String phoneNumber;
        do {
            System.out.print("Enter phone number (10 digits, only numbers , The phone number should start with the number 1): ");
            phoneNumber = scanner.nextLine();
        } while (!isValidPhoneNumber(phoneNumber));
        return phoneNumber;
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+20\\d{10}$");
    }

    private static String getValidatedUsername(Scanner scanner) {
        String username;
        do {
            System.out.print("Enter username (minimum of 6 characters, you can only use alphanumeric and underscores): ");
            username = scanner.nextLine();
        } while (!isValidUsername(username));
        return username;
    }

    private static boolean isValidUsername(String username) {
        return username.length() >= 6 && username.matches("^[a-zA-Z0-9_]+$");
    }

    private static String getValidatedPassword(Scanner scanner) {
        String password;
        do {
            System.out.print("Enter password (Preferably minimum 8 characters, containing at least one uppercase, one lowercase, one number, and one special character): ");
            password = scanner.nextLine();
        } while (!isValidPassword(password));
        return password;
    }

    private static boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
        password.matches(".*[!@#$%^&*()_+].*");
    }}



