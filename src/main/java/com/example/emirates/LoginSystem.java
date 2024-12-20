package com.example.emirates;

import java.util.ArrayList;
import java.util.List;

class User {
    // =================================================================================
    // Fields
    // =================================================================================
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String zipCode;
    private String address;
    private String username;
    private String password;
    private String role;

    // =================================================================================
    // Constructors
    // =================================================================================
    public User(String firstName, String lastName, String email, String phoneNumber, String zipCode, String address,
            String username, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.zipCode = zipCode;
        this.address = address;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String firstName, String email, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.email = email;
        this.lastName = lastName;
    }

    // =================================================================================
    // Static Methods
    // =================================================================================
    public static List<User> loadUsers() {
        return FileManager.loadUsersFromFile("login.txt");
    }

    // =================================================================================
    // Getters
    // =================================================================================
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // =================================================================================
    // Object Methods
    // =================================================================================
    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                firstName != null ? firstName : "",
                lastName != null ? lastName : "",
                email != null ? email : "",
                phoneNumber != null ? phoneNumber : "",
                zipCode != null ? zipCode : "",
                address != null ? address : "",
                username != null ? username : "",
                password != null ? password : "",
                role != null ? role : "");
    }
}

public class LoginSystem {
    // =================================================================================
    // Static Fields
    // =================================================================================
    static List<User> users = new ArrayList<>();

    // =================================================================================
    // Initialization
    // =================================================================================
    public static void initialize(String filePath) {
        users = FileManager.loadUsersFromFile(filePath);
    }

    // =================================================================================
    // User Management
    // =================================================================================
    public static List<User> getUsers() {
        return users;
    }

    public static void updateUsers(List<User> updatedUsers) {
        users = new ArrayList<>(updatedUsers);
    }

    // =================================================================================
    // Authentication
    // =================================================================================
    public static boolean isAdmin(String username, String password) {
        return "admin".equals(username) && "aabbcc".equals(password);
    }

    public static boolean authenticateUser(String username, String password) {
        if (isAdmin(username, password)) {
            return true;
        }

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
