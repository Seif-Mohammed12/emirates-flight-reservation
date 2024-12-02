package com.example.emirates;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, String username, String password, String role) {
        super(firstName, lastName, email, phoneNumber, zipCode, address, username, password, role);
    }

    // Add a new user
    public void addNewUser(String username, String password, String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, List<User> users) {
        if (userExists(username, users)) {
            System.out.println("User with this username already exists.");
            return;
        }

        User newUser = new User(firstName, lastName, email, phoneNumber, zipCode, address, username, password, "user");
        users.add(newUser);
        saveUsersToFile(users);
    }

    // Delete a user
    public void deleteUser(String username, List<User> users) {
        User userToDelete = null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                userToDelete = user;
                break;
            }
        }

        if (userToDelete != null) {
            users.remove(userToDelete);
            saveUsersToFile(users);
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("User not found.");
        }
    }

    // Save the updated users to a file
    private void saveUsersToFile(List<User> users) {
        try (FileWriter writer = new FileWriter("login.txt")) {
            for (User user : users) {
                writer.write(user.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Check if the username already exists
    private boolean userExists(String username, List<User> users) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
