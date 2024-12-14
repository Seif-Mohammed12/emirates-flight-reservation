package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class AdminController {

    private final Admin admin;
    private String loggedInUsername;
    @FXML
    private Button addflightbtn;


    public AdminController() {
        // Replace with the actual Admin instance
        this.admin = new Admin("Admin", "User", "admin@example.com", "1234567890", "12345",
                "Admin Address", "admin", "aabbcc", "admin");
    }

    public void setLoggedInUsername(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
    }

    @FXML
    private void handleAddFlightPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddFlight.fxml"));
            AnchorPane addFlightPage = loader.load();
            AddFlightController addFlightController = loader.getController();

            // Pass the logged-in username to the AddFlightController
            addFlightController.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) addflightbtn.getScene().getWindow(); // Replace homeBtnAbt with your actual button ID
            Scene currentScene = stage.getScene();

            // Create fade-out animation for the current scene
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                // Transition to the Add Flight page after fade-out
                currentScene.setRoot(addFlightPage);

                // Create fade-in animation for the new scene
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), addFlightPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleUpdateFlight() {
        // Placeholder for handling Update Flight
        showAlert("Info", "Update Flight functionality is coming soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleDeleteFlight() {
        // Placeholder for handling Delete Flight
        showAlert("Info", "Delete Flight functionality is coming soon!", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
