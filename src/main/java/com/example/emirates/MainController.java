package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainController {

    // ----------------------------------------
    // FXML Element References
    // ----------------------------------------

    @FXML
    Label titleLabel;
    @FXML
    private Label welcomeLabel;

    @FXML
    private DatePicker dateFrom, dateBack;

    @FXML
    private MenuButton dateMenu, passMenu;

    @FXML
    private ComboBox<String> arrBox;
    @FXML
    ComboBox<String> depBox;

    @FXML
    private ComboBox<String> classMenu;

    @FXML
    private Button goBtn, loginBtnMain, aboutBtn;

    @FXML
    private Spinner<Integer> adultSpinner, childrenSpinner;
    @FXML
    private MenuBar menuBarMain;


    // ----------------------------------------
    // Class-Level Variables
    // ----------------------------------------

    private boolean dropdownRefreshing = false;
    private boolean isTransitioning = false;

    private String selectedDestination;
    private String selectedDeparture;
    private String loggedInUsername;
    private String selectedClass;

    // ----------------------------------------
    // Initialization
    // ----------------------------------------

    @FXML
    public void initialize() {
        loadCustomFonts();
        configureDatePicker(dateFrom, LocalDate.now());
        configureDatePicker(dateBack, LocalDate.now());
        configureSpinners();
        configureClassMenu();
        configureLoginButton();
        setupComboBoxFocusListeners();
        Platform.runLater(() -> {
            depBox.hide(); // Ensure the dropdown is closed
            titleLabel.requestFocus();
        });
    }

    private void loadCustomFonts() {
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        Font customFontSmall = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 32);
        titleLabel.setFont(customFontLarge);
        welcomeLabel.setFont(customFontSmall);
        titleLabel.setStyle("-fx-font-weight: bold;");
        welcomeLabel.setStyle("-fx-font-weight: bold;");
    }

    // ----------------------------------------
    // Date Picker Configuration
    // ----------------------------------------

    private void configureDatePicker(DatePicker datePicker, LocalDate today) {
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isBefore(today)) {
                datePicker.setValue(today);
            }
        });
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                return (date == null) ? "" : date.format(formatter);
            }

            @Override
            public LocalDate fromString(String string) {
                return (string == null || string.isEmpty()) ? null : LocalDate.parse(string, formatter);
            }
        });
    }

    @FXML
    public void refreshDates() {
        String fromDate = dateFrom.getValue() != null ? dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
        String backDate = dateBack.getValue() != null ? dateBack.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
        dateMenu.setText(fromDate + " to " + backDate);
    }

    // ----------------------------------------
    // Spinner Configuration
    // ----------------------------------------

    private void configureSpinners() {
        SpinnerValueFactory<Integer> adultsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 1);
        adultSpinner.setValueFactory(adultsValueFactory);

        SpinnerValueFactory<Integer> childrenValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 2, 0);
        childrenSpinner.setValueFactory(childrenValueFactory);

        adultSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateMaxChildren(newValue));
        childrenSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updatePassMenu());
        updatePassMenu();
    }

    private void updateMaxChildren(int adults) {
        int maxChildren = adults * 2;
        ((SpinnerValueFactory.IntegerSpinnerValueFactory) childrenSpinner.getValueFactory()).setMax(maxChildren);
        if (childrenSpinner.getValue() > maxChildren) {
            childrenSpinner.getValueFactory().setValue(maxChildren);
        }
        updatePassMenu();
    }

    private void updatePassMenu() {
        int adults = adultSpinner.getValue();
        int children = childrenSpinner.getValue();
        passMenu.setText(adults + " Adults, " + children + " Children");
    }

    // ----------------------------------------
    // Login Button Configuration
    // ----------------------------------------

    private void configureLoginButton() {
        loginBtnMain.setOnMouseEntered(event -> {
            if (AppContext.getLoggedInUsername() != null) {
                loginBtnMain.setText("LOGOUT");
            }
        });

        loginBtnMain.setOnMouseExited(event -> updateLoginButton());
        updateLoginButton();
    }

    private void updateLoginButton() {
        String loggedInFirstName = AppContext.getLoggedInFirstName();

        if (loggedInFirstName != null) {
            loginBtnMain.setText("Hello, " + loggedInFirstName);
            if (!loginBtnMain.getStyleClass().contains("logged-in")) {
                loginBtnMain.getStyleClass().add("logged-in");
            }
        } else {
            loginBtnMain.setText("\uD83D\uDC64 LOG IN");
            loginBtnMain.getStyleClass().remove("logged-in");
        }
        loginBtnMain.setMinWidth(Region.USE_COMPUTED_SIZE);
        loginBtnMain.setPrefWidth(Region.USE_COMPUTED_SIZE);
        loginBtnMain.setMaxWidth(Double.MAX_VALUE);
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene currentScene = currentStage.getScene();
        String loggedInUsername = AppContext.getLoggedInUsername();
        String loggedInPassword = AppContext.getLoggedInPassword();

        if (AppContext.getLoggedInUsername() != null) {
            AppContext.setLoggedInUsername(null);
            AppContext.setLoggedInFirstName(null);
            updateLoginButton();
            System.out.println("User logged out.");
        } else {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                try {
                    if (LoginSystem.isAdmin(loggedInUsername, loggedInPassword)) {
                        // Navigate to admin page
                        Parent adminPage = FXMLLoader.load(getClass().getResource("Admin.fxml"));
                        currentScene.setRoot(adminPage);
                    } else {
                        // Navigate to regular user page
                        Parent loginPage = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
                        currentScene.setRoot(loginPage);
                    }

                    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), currentScene.getRoot());
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            fadeOut.play();
        }
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        System.out.println("Logged-in username in FlightsController: " + loggedInUsername);
    }

    // ----------------------------------------
    // Class Menu Configuration
    // ----------------------------------------

    private void configureClassMenu() {
        classMenu.getItems().addAll("Economy", "Business", "First");
        classMenu.setValue("Economy");
        selectedClass = "Economy";
        classMenu.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> selectedClass = newValue);
    }

    // ----------------------------------------
    // Dropdown Refresh and Handlers
    // ----------------------------------------

    private void setupComboBoxFocusListeners() {
        depBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    refreshDep(false);
                    depBox.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        arrBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    refresh(false);
                    arrBox.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void refreshDep(boolean filter) throws IOException {
        String filePath = "flights.csv";
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        depBox.getItems().clear();

        Set<String> uniqueDepartures = new HashSet<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",");
            if (fields.length == 9) {
                String departureAirport = fields[1];
                if (!filter || departureAirport.toLowerCase().startsWith(depBox.getEditor().getText().toLowerCase())) {
                    if (!departureAirport.equalsIgnoreCase(selectedDestination)) {
                        uniqueDepartures.add(departureAirport);
                    }
                }
            }
        }

        depBox.getItems().addAll(uniqueDepartures);

        // Clear selection if items are changed
        if (depBox.getItems().isEmpty()) {
            depBox.getSelectionModel().clearSelection();
            depBox.hide();
        } else if (!depBox.getEditor().getText().isEmpty()) {
            depBox.show();
        }
    }

    public void refresh(boolean filter) throws IOException {
        if (selectedDeparture == null || selectedDeparture.isEmpty()) {
            arrBox.getItems().clear();
            arrBox.hide();
            return;
        }

        String filePath = "flights.csv";
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        arrBox.getItems().clear();

        Set<String> uniqueDestinations = new HashSet<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",");
            if (fields.length == 9) {
                String arrivalAirport = fields[2];
                String departureAirport = fields[1];

                if (departureAirport.equalsIgnoreCase(selectedDeparture) &&
                        (!filter || arrivalAirport.toLowerCase().startsWith(arrBox.getEditor().getText().toLowerCase()))) {
                    uniqueDestinations.add(arrivalAirport);
                }
            }
        }

        arrBox.getItems().addAll(uniqueDestinations);

        if (uniqueDestinations.isEmpty()) {
            arrBox.getSelectionModel().clearSelection();
            arrBox.hide();
        } else {
            arrBox.show();
        }
    }

    @FXML
    public void handleDepMouseClick(MouseEvent event) throws IOException {
        if (dropdownRefreshing) {
            return; // Skip if already refreshing
        }

        refreshDep(false);
        depBox.getEditor().requestFocus();
    }

    @FXML
    public void handleDepKeyRelease(KeyEvent event) throws IOException {
        refreshDep(true);
        depBox.show();
    }

    @FXML
    public void handleArrMouseClick(MouseEvent event) throws IOException {
        refresh(false);
        arrBox.show();
    }

    @FXML
    public void handleArrKeyRelease(KeyEvent event) throws IOException {
        if (dropdownRefreshing || arrBox.isShowing()) {
            return; // Skip if already refreshing or dropdown is visible
        }
        refresh(true);

        if (!arrBox.isShowing()) {
            arrBox.show();
        }
    }

    @FXML
    public void onDestinationSelected(ActionEvent event) throws IOException {
        selectedDestination = arrBox.getValue();

        if (selectedDestination != null && !selectedDestination.isEmpty()) {
            System.out.println("Destination selected: " + selectedDestination);
            AppContext.setSelectedDestination(selectedDestination);

            // Hide the dropdown after selection
            arrBox.hide();
        }
    }


    @FXML
    public void onDepartureSelected(ActionEvent event) throws IOException {
        selectedDeparture = depBox.getValue();

        if (selectedDeparture == null || selectedDeparture.isEmpty()) {
            // Clear arrivals and hide dropdown
            arrBox.getItems().clear();
            arrBox.getSelectionModel().clearSelection();
            arrBox.hide();
            return;
        }

        System.out.println("Departure selected: " + selectedDeparture);
        AppContext.setSelectedDeparture(selectedDeparture);

        // Refresh arrivals and show dropdown only if items are available
        refresh(false);

        if (arrBox.getItems().isEmpty()) {
            arrBox.getSelectionModel().clearSelection();
            arrBox.hide();
            showStyledAlert("No destinations found for the selected departure: " + selectedDeparture,
                    (Stage) depBox.getScene().getWindow());
        } else {
            arrBox.show();
        }
    }


    // ----------------------------------------
    // Scene Transitions
    // ----------------------------------------
    public void handleGoButton(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        AppContext.setSelectedDestination(selectedDestination);
        AppContext.setSelectedDeparture(selectedDeparture);

        //if (AppContext.getLoggedInUsername() == null) {
        //    showStyledAlert("Please log in first to access flights!", currentStage);
        //    return;
        //}
//
        //if (selectedDestination == null || selectedDestination.isEmpty()) {
        //    showStyledAlert("Please select a destination first!", currentStage);
        //    return;
        //}
//
        //if (dateFrom.getValue() == null || dateBack.getValue() == null) {
        //    showStyledAlert("Please select a departure and return date!", currentStage);
        //    return;
        //}
//
        //if (selectedDeparture == null || selectedDeparture.isEmpty()) {
        //    showStyledAlert("Please select a departure first!", currentStage);
        //    return;
        //}
//
        //if (selectedClass == null || selectedClass.isEmpty()) {
        //    showStyledAlert("Please select a class first!", currentStage);
        //    return;
        //}

        LocalDate departureDate = dateFrom.getValue();
        LocalDate returnDate = dateBack.getValue();

        Parent currentRoot = currentStage.getScene().getRoot();

        currentStage.getScene().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Pane overlayPane = new Pane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);"); // Semi-transparent background
        overlayPane.setPrefSize(currentRoot.getBoundsInLocal().getWidth(), currentRoot.getBoundsInLocal().getHeight());


        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(100, 100);
        loadingIndicator.setStyle("-fx-progress-color: #D71920;" + "-fx-background-color: transparent; " + "-fx-border-color: transparent; " + "-fx-padding: 0;");
        loadingIndicator.layoutXProperty().bind(overlayPane.widthProperty().divide(2).subtract(600)); // Center horizontally
        loadingIndicator.layoutYProperty().bind(overlayPane.heightProperty().divide(2).subtract(50)); // Center vertically

        overlayPane.getChildren().add(loadingIndicator);

        ((Pane) currentRoot).getChildren().add(overlayPane);

        Task<Parent> loadFXMLTask = new Task<>() {
            @Override
            protected Parent call() throws IOException {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("flights.fxml"));
                Parent root = loader.load();

                // Configure the FlightsController
                FlightsController flightListController = loader.getController();
                flightListController.setSelectedDestination(selectedDestination);
                flightListController.setSelectedDeparture(selectedDeparture);
                flightListController.setLoggedInUsername(AppContext.getLoggedInUsername());
                flightListController.setSelectedClass(selectedClass);
                flightListController.setAdults(adultSpinner.getValue());
                flightListController.setChildren(childrenSpinner.getValue());
                flightListController.setDepartureDate(departureDate);
                flightListController.setReturnDate(returnDate);
                flightListController.updateFlightCards();

                return root;
            }
        };


        loadFXMLTask.setOnSucceeded(workerStateEvent -> {
            Parent newRoot = loadFXMLTask.getValue();
            Scene currentScene = currentStage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(newRoot);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        });

        loadFXMLTask.setOnFailed(workerStateEvent -> {
            Throwable error = loadFXMLTask.getException();
            error.printStackTrace();

            ((Pane) currentRoot).getChildren().remove(overlayPane);
            showStyledAlert("Failed to load the flights scene: " + error.getMessage(), currentStage);
        });

        // Run the task in a background thread
        Thread loadThread = new Thread(loadFXMLTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    public void gotosearch(ActionEvent event) throws IOException {
        // Get the current stage and scene
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene currentScene = currentStage.getScene();

        // Load the new FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("flightsearch.fxml"));
        Parent root = loader.load();

        // Get the controller for the new FXML
        FlightsearchController flightsearchController = loader.getController();

        // Pass data to the controller if needed
        // Example: flightsearchController.initializeData(someData);

        // Optional: Add a fade-out transition for smoother experience
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            // Switch to the new scene
            currentScene.setRoot(root);

            // Optional: Add a fade-in transition for the new scene
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    public void gotoAbout(ActionEvent event) {
        // Get the current stage and scene
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene currentScene = currentStage.getScene();

        // Create a loading overlay
        Pane overlayPane = new Pane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);"); // Semi-transparent background
        overlayPane.setPrefSize(currentScene.getRoot().getBoundsInLocal().getWidth(),
                currentScene.getRoot().getBoundsInLocal().getHeight());

        // Add a loading spinner to the overlay
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(100, 100);
        loadingIndicator.setStyle("-fx-progress-color: #D71920;" + "-fx-background-color: transparent;");

        // Center the loading indicator
        loadingIndicator.layoutXProperty().bind(overlayPane.widthProperty().divide(2).subtract(600));
        loadingIndicator.layoutYProperty().bind(overlayPane.heightProperty().divide(2).subtract(50));
        overlayPane.getChildren().add(loadingIndicator);

        // Add the overlay to the current scene
        ((Pane) currentScene.getRoot()).getChildren().add(overlayPane);

        // Task for loading the FXML
        Task<Parent> loadFXMLTask = new Task<>() {
            @Override
            protected Parent call() throws IOException {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("About.fxml"));
                return loader.load(); // Load About.fxml
            }
        };

        // Success handler: Transition to the new scene
        loadFXMLTask.setOnSucceeded(workerStateEvent -> {
            Parent newRoot = loadFXMLTask.getValue();

            // Create a fade-out transition for the current scene
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0); // Start at full opacity
            fadeOut.setToValue(0.0);   // Fade to transparent

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(newRoot); // Replace the root with the new scene

                // Create and play a fade-in transition for the new scene
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), newRoot);
                fadeIn.setFromValue(0.0); // Start from transparent
                fadeIn.setToValue(1.0);   // Fade to full opacity
                fadeIn.play();
            });

            fadeOut.play(); // Play the fade-out animation

            // Remove the overlay once the transition starts
            ((Pane) currentScene.getRoot()).getChildren().remove(overlayPane);
        });

        // Failure handler: Remove the loading indicator and display an error
        loadFXMLTask.setOnFailed(workerStateEvent -> {
            Throwable error = loadFXMLTask.getException();
            error.printStackTrace(); // Log the error for debugging

            // Remove the overlay
            ((Pane) currentScene.getRoot()).getChildren().remove(overlayPane);

            // Show an alert to the user
            showStyledAlert("Failed to load the About page: " + error.getMessage(), currentStage);
        });

        // Start the task in a background thread
        Thread loadThread = new Thread(loadFXMLTask);
        loadThread.setDaemon(true); // Ensure the thread exits with the application
        loadThread.start();
    }


    // ----------------------------------------
    // Alert Dialogs
    // ----------------------------------------

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











}
