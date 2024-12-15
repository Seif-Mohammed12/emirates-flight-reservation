package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegistrationController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField zipCodeField;
    @FXML
    private TextField addressField;

    @FXML
    public void initialize() {

        if (!phoneField.getText().startsWith("+20")) {
            phoneField.setText("+20");
        }

        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.startsWith("+20")) {
                phoneField.setText("+20" + newValue.replaceFirst("^\\+20", ""));
                phoneField.positionCaret(phoneField.getText().length());
            }
        });

        phoneField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String currentText = phoneField.getText();
            if (currentText.length() <= 3 && event.getCharacter().equals("\b")) {
                event.consume();
            }
        });
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneField.getText();
        String zipCode = zipCodeField.getText();
        String address = addressField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (isValidUserData(firstName, lastName, email, phoneNumber, zipCode, address, username, password)) {
            String role = "user";
            User newUser = new User(firstName, lastName, email, phoneNumber, zipCode, address, username, password, role);

            try {
                List<String> usersData = new ArrayList<>();
                usersData.add(newUser.toString());
                FileManager.writeFile("login.txt", usersData, true);

                showSuccessAlert("Congratulations! ✈ \n" + "\n" +
                        "Welcome aboard the Emirates experience! Your registration was successful. You can now log in to explore our world-class flights, manage your bookings, and embark on unforgettable journeys.\n" +
                        "\n" +
                        "We’re excited to have you as part of the Emirates family. \uD83C\uDF0D", (Stage) usernameField.getScene().getWindow());
            } catch (IOException e) {
                showErrorAlert("There was an error writing to the file: " + e.getMessage(), (Stage) usernameField.getScene().getWindow());
            }

            goBackToLogin(event);
        }
    }

    private boolean isValidUserData(String firstName, String lastName, String email, String phoneNumber, String zipCode, String address, String username, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || zipCode.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showErrorAlert("All fields must be filled.", (Stage) usernameField.getScene().getWindow());
            return false;
        }

        if(!isvalidName(firstName)) {
            showErrorAlert(firstName + " is an invalid first name.", (Stage) usernameField.getScene().getWindow());
            return false;
        }
        if(!isvalidName(lastName)) {
            showErrorAlert(lastName + " is an invalid last name.", (Stage) usernameField.getScene().getWindow());
            return false;
        }

        if (!isValidEmail(email)) {
            showErrorAlert("Please enter a valid email address.", (Stage) usernameField.getScene().getWindow());
            return false;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showErrorAlert("Phone number should start with +20 and be 10 digits.", (Stage) usernameField.getScene().getWindow());
            return false;
        }

        if(!isvalidZipcode(zipCode)) {
            showErrorAlert("Please enter a valid zip code.", (Stage) usernameField.getScene().getWindow());
            return false;
        }

        if (!isValidUsername(username)) {
            showErrorAlert("Username must be at least 6 characters and can only contain letters, numbers, and underscores.", (Stage) usernameField.getScene().getWindow());
            return false;
        }

        if (!isValidPassword(password)) {
            showErrorAlert("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.", (Stage) usernameField.getScene().getWindow());
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

    private static boolean isvalidName(String name) {
        String nameRegex = "^[a-zA-Z]+(?: [a-zA-Z]+)*$";
        return name.matches(nameRegex);
    }

    private static boolean isvalidZipcode(String Zipcode) {
        String ZipRegex = "^\\d{4,10}$";
        return Zipcode.matches(ZipRegex);
    }

    private void showSuccessAlert(String message, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(null); // Prevent the default text content
        alert.initOwner(owner);
        alert.initModality(Modality.APPLICATION_MODAL);

        // Customize DialogPane
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("success-dialog");
        dialogPane.setHeaderText(null);

        // Create a TextFlow for text wrapping
        TextFlow textFlow = new TextFlow();
        textFlow.setMaxWidth(400); // Restrict the width of the text flow
        textFlow.setPrefWidth(400); // Set preferred width

        Text text = new Text(message);
        text.setWrappingWidth(380); // Slightly smaller than TextFlow width for padding
        textFlow.getChildren().add(text);

        // Set the TextFlow as the dialog content
        dialogPane.setContent(textFlow);

        // Style dialog pane
        dialogPane.setStyle("-fx-background-color: #d4edda; -fx-background-radius: 20; -fx-border-radius: 20; " +
                "-fx-border-color: #388e3c; -fx-border-width: 2px;");

        alert.setHeaderText(null);
        alert.setGraphic(null);

        // Ensure the dialog has a nice compact size
        dialogPane.setPrefWidth(420);
        dialogPane.setMaxWidth(420);

        // Make the dialog background transparent
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        alertStage.getScene().setFill(null);

        alert.showAndWait();
    }


    private void showErrorAlert(String message, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.initOwner(owner);
        alert.initModality(Modality.APPLICATION_MODAL);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("error-dialog");
        dialogPane.setHeaderText(null);
        dialogPane.setGraphic(null);

        TextFlow textFlow = new TextFlow();
        Text text = new Text(message);
        text.setWrappingWidth(150);
        textFlow.getChildren().add(text);
        dialogPane.setContent(textFlow);

        dialogPane.setStyle("-fx-background-color: #f8d7da; -fx-background-radius: 20; -fx-border-radius: 20;");

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        alertStage.getScene().setFill(null);

        alert.showAndWait();
    }

    @FXML
    private void goBackToLogin(ActionEvent event) {
        try {
            Parent loginPage = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(loginPage);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), loginPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
