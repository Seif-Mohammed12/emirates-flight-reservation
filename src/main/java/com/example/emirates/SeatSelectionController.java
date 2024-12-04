package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SeatSelectionController {

    private String selectedClass;
    private String selectedDestination;
    private String selectedDeparture;
    private int adults;
    private int children;
    private String loggedInUsername;
    private selectFlights.Flights selectedFlight; // If flight-specific details are needed

    @FXML
    private GridPane economyGrid, businessGrid, firstGrid;
    @FXML
    private ScrollPane economyScroll, businessScroll, firstScroll;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Button iconBtnSeat;

    private final SeatSelection seatSelection = new SeatSelection();

    public void initialize() {
        Font customFontSmall = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 36);
        titleLabel.setFont(customFontSmall);
        titleLabel.setStyle("-fx-font-weight: bold;");
        seatSelection.createSeatingPlan(economyGrid, 20, 6, "Economy");
        seatSelection.createSeatingPlan(businessGrid, 10, 4, "Business");
        seatSelection.createSeatingPlan(firstGrid, 6, 2, "First");

        economyScroll.setVisible(false);
        businessScroll.setVisible(false);
        firstScroll.setVisible(false);

        loginButton.setOnMouseEntered(event -> {
            if (AppContext.getLoggedInUsername() != null) {
                loginButton.setText("Logout");
            }
        });
        loginButton.setOnMouseExited(event -> updateLoginButton());

        updateLoginButton();
        if (selectedClass == null) {
            selectedClass = "Economy"; // Default to "Economy" if not set
        }
        displaySelectedClassGrid();
    }

    public void setSelectedClass(String classType) {
        this.selectedClass = classType;
        displaySelectedClassGrid();
    }

    public void setSelectedDestination(String destination) {
        this.selectedDestination = destination;
    }

    public void setSelectedDeparture(String departure) {
        this.selectedDeparture = departure;
    }

    public void setAdults(int adults) {
        this.adults = adults;
        updateTotalPassengers();
    }

    public void setChildren(int children) {
        this.children = children;
        updateTotalPassengers();
    }

    private void updateTotalPassengers() {
        int totalPassengers = adults + children;
        seatSelection.setTotalPassengers(totalPassengers);
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    public void setSelectedFlight(selectFlights.Flights flight) {
        this.selectedFlight = flight;
    }

    private void displaySelectedClassGrid() {
        economyScroll.setVisible(false);
        businessScroll.setVisible(false);
        firstScroll.setVisible(false);

        switch (selectedClass) {
            case "Economy":
                economyScroll.setVisible(true);
                break;
            case "Business":
                businessScroll.setVisible(true);
                break;
            case "First":
                firstScroll.setVisible(true);
                break;
        }

        seatSelection.resetSelectedSeats();
    }

    private void updateLoginButton() {
        String loggedInFirstName = AppContext.getLoggedInFirstName();

        if (loggedInFirstName != null) {
            loginButton.setText("Hello, " + loggedInFirstName);
        } else {
            loginButton.setText("Login");
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        // Create a loading overlay using a Pane
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Semi-transparent background
        overlay.setPrefSize(backButton.getScene().getWidth(), backButton.getScene().getHeight()); // Cover the entire scene

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setStyle("-fx-progress-color: #D71920;" + "-fx-background-color: transparent;");
        loadingIndicator.setLayoutX(backButton.getScene().getWidth() / 2 - 20); // Center horizontally
        loadingIndicator.setLayoutY(backButton.getScene().getHeight() / 2 - 20); // Center vertically
        overlay.getChildren().add(loadingIndicator);
        overlay.setVisible(false); // Initially hidden

        // Get the root pane of the current scene
        Parent currentRoot = backButton.getScene().getRoot();
        if (currentRoot instanceof Pane) {
            ((Pane) currentRoot).getChildren().add(overlay);
        } else {
            throw new IllegalStateException("Root is not a Pane. Cannot add overlay.");
        }

        // Show the overlay before starting the task
        overlay.setVisible(true);

        // Create a background task to load the FXML
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws IOException {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Flights.fxml"));
                Parent flightsLayout = loader.load();

                // Update FlightsController with data
                FlightsController flightsController = loader.getController();
                flightsController.setLoggedInUsername(loggedInUsername);
                flightsController.setSelectedClass(selectedClass);
                flightsController.setSelectedDestination(selectedDestination);
                flightsController.setSelectedDeparture(selectedDeparture);
                flightsController.setAdults(adults);
                flightsController.setChildren(children);
                flightsController.updateFlightCards();

                return flightsLayout;
            }
        };

        // When loading is successful, update the UI
        loadTask.setOnSucceeded(workerStateEvent -> {
            Parent flightsLayout = loadTask.getValue();
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene currentScene = stage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(flightsLayout);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), flightsLayout);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();

            // Hide the loading overlay
            overlay.setVisible(false);
            ((Pane) currentRoot).getChildren().remove(overlay); // Clean up the overlay after transition
        });

        // Handle failure
        loadTask.setOnFailed(workerStateEvent -> {
            Throwable error = loadTask.getException();
            error.printStackTrace();

            // Hide the loading overlay in case of failure
            overlay.setVisible(false);
            ((Pane) currentRoot).getChildren().remove(overlay); // Clean up the overlay
        });

        // Run the task in a background thread
        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true); // Mark as a daemon thread to prevent it from blocking application exit
        loadThread.start();
    }




    @FXML
    private void goToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            AnchorPane mainPage = loader.load();
            MainController mainController = loader.getController();

            String loggedInUsername = AppContext.getLoggedInUsername();
            mainController.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) iconBtnSeat.getScene().getWindow();
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
}

