package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class AdminController {

    // Instance Variables
    private final Admin admin;
    private String loggedInUsername;

    // FXML Injected Fields
    @FXML
    private Button addflightbtn, loginBtnAdmin, iconBtnAdmin, updateBtn, deleteBtn;
    @FXML
    private Label headerlabel;

    // Constructor
    public AdminController() {
        this.admin = new Admin("Admin", "User", "admin@example.com", "1234567890", "12345",
                "Admin Address", "admin", "aabbcc", "admin");
    }

    // Initialization Methods
    @FXML
    public void initialize() {
        configureLoginButton();
        loadCustomFonts();
    }

    private void loadCustomFonts() {
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        headerlabel.setFont(customFontLarge);
        headerlabel.setStyle("-fx-font-weight: bold;");
    }

    // Navigation Methods
    @FXML
    private void handleAddFlightPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddFlight.fxml"));
            AnchorPane addFlightPage = loader.load();
            AddFlightController addFlightController = loader.getController();
            addFlightController.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) addflightbtn.getScene().getWindow();
            Scene currentScene = stage.getScene();

            // Fade out transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                // Switch scene
                currentScene.setRoot(addFlightPage);

                // Fade in transition
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateFlight.fxml"));
            AnchorPane updateFlightPage = loader.load();

            Stage currentStage = (Stage) updateBtn.getScene().getWindow();
            Scene currentScene = currentStage.getScene();

            // Fade out transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(updateFlightPage);

                // Fade in transition
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), updateFlightPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the Update Flight page.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteFlight() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("deleteflights.fxml"));
            AnchorPane deleteFlightPage = loader.load();

            Stage currentStage = (Stage) deleteBtn.getScene().getWindow();
            Scene currentScene = currentStage.getScene();

            // Fade out transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(deleteFlightPage);

                // Fade in transition
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), deleteFlightPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the Update Flight page.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void gotomain() {
        try {
            AppContext.setLoggedInUsername(null);
            AppContext.setLoggedInFirstName(null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            AnchorPane mainPage = loader.load();
            MainController mainController = loader.getController();
            mainController.setLoggedInUsername(null);

            Stage stage = (Stage) iconBtnAdmin.getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(mainPage);
                Platform.runLater(() -> {
                    mainController.titleLabel.requestFocus();
                });

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), mainPage);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Login/Logout Management Methods
    private void configureLoginButton() {
        loginBtnAdmin.setOnMouseEntered(event -> {
            if (AppContext.getLoggedInUsername() != null) {
                loginBtnAdmin.setText("LOGOUT");
            }
        });

        loginBtnAdmin.setOnMouseExited(event -> updateLoginButton());
        updateLoginButton();
    }

    private void updateLoginButton() {
        String loggedInFirstName = AppContext.getLoggedInFirstName();

        if (loggedInFirstName != null) {
            loginBtnAdmin.setText("Hello, " + loggedInFirstName);
            if (!loginBtnAdmin.getStyleClass().contains("logged-in")) {
                loginBtnAdmin.getStyleClass().add("logged-in");
            }
        } else {
            loginBtnAdmin.setText("\uD83D\uDC64 LOG IN");
            loginBtnAdmin.getStyleClass().remove("logged-in");
        }
        loginBtnAdmin.setMinWidth(Region.USE_COMPUTED_SIZE);
        loginBtnAdmin.setPrefWidth(Region.USE_COMPUTED_SIZE);
        loginBtnAdmin.setMaxWidth(Double.MAX_VALUE);
    }

    // Utility Methods
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Setter Methods
    public void setLoggedInUsername(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
    }
}
