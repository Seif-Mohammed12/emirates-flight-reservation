package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private Button loginBtn2;

    @FXML
    private void initialize() {
        LoginSystem.initialize("login.txt");
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        if (validateLogin(username, password)) {
            navigateToMainPage();
        } else {
            showStyledAlert("Invalid credentials. Please try again.", (Stage) usernameTextField.getScene().getWindow());
        }
    }

    private boolean validateLogin(String username, String password) {
        if (LoginSystem.isAdmin(username, password)) {
            System.out.println("Admin login detected!");
            AppContext.setLoggedInUsername(username);
            AppContext.setLoggedInFirstName("Admin");
            return true;
        }

        for (User user : LoginSystem.getUsers()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                System.out.println("User login detected: " + user.getUsername());
                AppContext.setLoggedInUsername(user.getUsername());
                AppContext.setLoggedInFirstName(user.getFirstName());
                return true;
            }
        }
        return false;
    }

    private void navigateToMainPage() {
        try {
            FXMLLoader loader;

            // Check if the logged-in user is an Admin by using the username
            if ("admin".equals(AppContext.getLoggedInUsername())) {
                // Load AdminPage.fxml if the logged-in user is an admin
                loader = new FXMLLoader(getClass().getResource("Admin.fxml"));
            } else {
                // Load UserPage.fxml for regular users
                loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            }

            Parent mainPage = loader.load();

            if ("admin".equals(AppContext.getLoggedInUsername())) {
                // Cast to AdminController for admin login
                AdminController adminController = loader.getController();
                adminController.setLoggedInUsername(AppContext.getLoggedInUsername());
            } else {
                MainController mainController = loader.getController();
                mainController.setLoggedInUsername(AppContext.getLoggedInUsername());
                mainController.depBox.layout();

            }

            // Get the current stage and scene
            Stage stage = (Stage) loginPane.getScene().getWindow();
            Scene currentScene = stage.getScene();

            // Create a fade-out transition effect for smooth page switching
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            // When the fade-out is complete, change the scene root to the new page
            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(mainPage);
                Platform.runLater(() -> {
                    // Ensure MainController is properly cast to access titleLabel
                    MainController mainController = loader.getController();
                    mainController.titleLabel.requestFocus();
                    mainController.depBox.layout();
                });

                // Create a fade-in transition for the new page
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), mainPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            // Play the fade-out effect
            fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private void showStyledAlert(String message, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.initOwner(owner);
        alert.initModality(Modality.APPLICATION_MODAL);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("error-dialog");
        dialogPane.setHeaderText(null);
        dialogPane.setGraphic(null);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        alertStage.getScene().setFill(null);
        dialogPane.setStyle("-fx-background-color: #f8d7da; -fx-background-radius: 20; -fx-border-radius: 20;");

        alert.showAndWait();
    }

    @FXML
    private void goToRegistrationPage(ActionEvent event) {
        try {
            Parent registrationPage = FXMLLoader.load(getClass().getResource("Registration.fxml"));

            Stage stage = (Stage) loginBtn2.getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(registrationPage);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), registrationPage);
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
