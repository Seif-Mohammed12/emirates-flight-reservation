package com.example.emirates;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class AdminController {

    @FXML
    private TableView<String> actionTable;

    @FXML
    private TableColumn<String, String> actionColumn;

    @FXML
    private VBox rootPane;

    @FXML
    private GridPane userForm;

    @FXML
    private GridPane flightForm;

    // User form fields
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField zipCodeField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    // Flight form fields
    @FXML
    private TextField flightNoField;
    @FXML
    private TextField departureCityField;
    @FXML
    private TextField arrivalCityField;
    @FXML
    private TextField departureTimeField;
    @FXML
    private TextField arrivalTimeField;
    @FXML
    private TextField durationField;
    @FXML
    private TextField stopsField;
    @FXML
    private TextField aircraftDetailsField;
    @FXML
    private TextField priceField;

    private String loggedInUsername;

    public void initialize() {
        // Populate actions in the TableView
        actionTable.getItems().addAll(
                "Add Flights",
                "Delete Flights",
                "Update Flights",
                "Add User",
                "Delete User"
        );
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        actionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void handleActionSelected(MouseEvent event) {
        String selectedAction = actionTable.getSelectionModel().getSelectedItem();
        if (selectedAction != null && !selectedAction.trim().isEmpty()) {
            toggleVisibility(selectedAction);
        }
    }

    private void toggleVisibility(String selectedAction) {
        rootPane.setVisible(false);
        rootPane.setManaged(false);
        userForm.setVisible(false);
        userForm.setManaged(false);
        flightForm.setVisible(false);
        flightForm.setManaged(false);

        // Show the selected form or menu
        switch (selectedAction) {
            case "Add User":
                userForm.setVisible(true);
                userForm.setManaged(true);
                break;
            case "Add Flights":
                flightForm.setVisible(true);
                flightForm.setManaged(true);
                break;
            case "Delete Flights":
                deleteFlightPrompt();
                break;
            default:
                rootPane.setVisible(true);
                rootPane.setManaged(true);
                showAlert("Action Selected", "This feature is not implemented yet.");
                break;
        }
    }

    @FXML
    private void handleAddUser() {
        // Retrieve input from the form fields
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();
        String zipCode = zipCodeField.getText();
        String address = addressField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate inputs
        if (!isValidUserData(firstName, lastName, email, phoneNumber, zipCode, address, username, password)) {
            return; // Stop execution if validation fails
        }

        try {
            // Load the current list of users
            List<User> users = LoginSystem.getUsers();

            // Check if user already exists
            if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
                showAlert("Error", "A user with this username already exists.");
                return;
            }


            Admin.addNewUser(username, password, firstName, lastName, email, phoneNumber, zipCode, address, users);

            showAlert("Success", "User added successfully!");

            // Clear form
            clearInputFields();
        } catch (Exception e) {
            showAlert("Error", "Failed to add user: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddFlight() {
        // Retrieve input from the flight form fields
        String flightNo = flightNoField.getText();
        String departureCity = departureCityField.getText();
        String arrivalCity = arrivalCityField.getText();
        String departureTime = departureTimeField.getText();
        String arrivalTime = arrivalTimeField.getText();
        String duration = durationField.getText();
        String stops = stopsField.getText();
        String aircraftDetails = aircraftDetailsField.getText();
        String price = priceField.getText();

        if (flightNo.isEmpty() || departureCity.isEmpty() || arrivalCity.isEmpty() ||
                departureTime.isEmpty() || arrivalTime.isEmpty() || duration.isEmpty() ||
                stops.isEmpty() || aircraftDetails.isEmpty() || price.isEmpty()) {
            showAlert("Error", "All fields must be filled out!");
            return;
        }

        try {
            // Call the updated addFlight method from Admin class
            Admin.addFlight(flightNo, departureCity, arrivalCity, departureTime, arrivalTime, duration, stops, aircraftDetails, price);

            // Notify success
            showAlert("Success", "Flight added successfully!");

            // Clear form fields
            clearFlightFields();
        } catch (IOException e) {
            // Handle IO exceptions
            showAlert("Error", "Failed to add flight: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void clearFlightFields() {
        flightNoField.clear();
        departureCityField.clear();
        arrivalCityField.clear();
        departureTimeField.clear();
        arrivalTimeField.clear();
        durationField.clear();
        stopsField.clear();
        aircraftDetailsField.clear();
        priceField.clear();
    }


    private void deleteFlightPrompt() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Flight");
        dialog.setHeaderText("Enter the Flight Number to Delete:");
        dialog.setContentText("Flight Number:");

        dialog.showAndWait().ifPresent(flightNo -> {
            try {
                // Delete flight
                Admin.deleteFlight(flightNo);

                showAlert("Success", "Flight deleted successfully!");
            } catch (Exception e) {
                showAlert("Error", "Failed to delete flight: " + e.getMessage());
            }
        });
    }

    private boolean isValidUserData(String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, String username, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || zipCode.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields must be filled out!");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert("Error", "Please enter a valid email address.");
            return false;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert("Error", "Phone number should start with +20 and be 10 digits.");
            return false;
        }

        if (!isValidUsername(username)) {
            showAlert("Error", "Username must be at least 6 characters and can only contain letters, numbers, and underscores.");
            return false;
        }

        if (!isValidPassword(password)) {
            showAlert("Error", "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[a-zA-Z]{2,4}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+20\\d{10}$");
    }

    private boolean isValidUsername(String username) {
        return username.length() >= 6 && username.matches("^[a-zA-Z0-9_]+$");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*()_+].*");
    }

    private void clearInputFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneNumberField.clear();
        zipCodeField.clear();
        addressField.clear();
        usernameField.clear();
        passwordField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }
}
