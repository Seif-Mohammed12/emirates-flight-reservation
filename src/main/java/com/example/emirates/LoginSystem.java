package com.example.emirates;

import java.util.ArrayList;
import java.util.List;

class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String zipCode;
    private String address;
    private String username;
    private String password;
    private String role;

    public User(String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, String username, String password, String role) {
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

    public User(String username, String password, String firstName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return firstName + "," + lastName + "," + email + "," + phoneNumber + "," + zipCode + "," + address + "," + username + "," + password + "," + role;
    }

    public static User fromString(String userData) {
        String[] parts = userData.split(",");
        if (parts.length == 9) {
            return new User(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], parts[8]);
        }
        return null;
    }
}


public class LoginSystem {

    static List<User> users = new ArrayList<>();

    public static void initialize(String filePath) {
        users = FileManager.loadUsersFromFile(filePath);
    }

    public static List<User> getUsers() {
        return users;
    }

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
        return false; // No matching user found
    }
}
