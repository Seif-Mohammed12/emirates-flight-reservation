package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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

    @FXML
    private Label titleLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private DatePicker dateFrom, dateBack;
    @FXML
    private MenuButton dateMenu;
    @FXML
    private MenuButton passMenu;
    @FXML
    private ComboBox<String> arrBox;
    @FXML
    private ComboBox<String> depBox;
    @FXML
    private ComboBox<String> classMenu;
    @FXML
    private Button goBtn;
    @FXML
    private Button loginBtnMain;
    @FXML
    private Spinner<Integer> adultSpinner;
    @FXML
    private Spinner<Integer> childrenSpinner;

    private String selectedDestination;
    private String selectedDeparture;
    private String loggedInUsername;
    private String selectedClass;


    @FXML
    public void initialize() {
        // Load the custom font
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        Font customFontSmall = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 32);
        titleLabel.setFont(customFontLarge);
        welcomeLabel.setFont(customFontSmall);
        titleLabel.setStyle("-fx-font-weight: bold;");
        welcomeLabel.setStyle("-fx-font-weight: bold; ");
        Platform.runLater(() ->titleLabel.requestFocus());
        LocalDate today = LocalDate.now();
        configureDatePicker(dateFrom, today);
        configureDatePicker(dateBack, today);

        SpinnerValueFactory<Integer> adultsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 1);
        adultSpinner.setValueFactory(adultsValueFactory);

        // Set up children spinner
        SpinnerValueFactory<Integer> childrenValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 2, 0);
        childrenSpinner.setValueFactory(childrenValueFactory);

        // Listener for adults spinner
        adultSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            // Update children spinner max based on the number of adults
            int maxChildren = newValue * 2;
            ((SpinnerValueFactory.IntegerSpinnerValueFactory) childrenSpinner.getValueFactory()).setMax(maxChildren);

            // Adjust children count if it exceeds the new max
            if (childrenSpinner.getValue() > maxChildren) {
                childrenSpinner.getValueFactory().setValue(maxChildren);
            }
            updatePassMenu();
        });

        // Listener for children spinner
        childrenSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updatePassMenu();
        });
        updatePassMenu();


        updateLoginButton();
        loginBtnMain.setOnMouseEntered(event -> {
            if (AppContext.getLoggedInUsername() != null) {
                loginBtnMain.setText("LOGOUT");
            }
        });

        loginBtnMain.setOnMouseExited(event -> updateLoginButton());
        if (AppContext.getLoggedInUsername() != null) {
            loginBtnMain.getStyleClass().add("logged-in");
        } else {
            loginBtnMain.getStyleClass().remove("logged-in");
        }

        classMenu.getItems().addAll("Economy", "Business", "First");
        classMenu.setValue("Economy");
        selectedClass = "Economy";
        classMenu.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            selectedClass = newValue;
        });
    }

    private void updatePassMenu() {
        int adults = adultSpinner.getValue();
        int children = childrenSpinner.getValue();
        passMenu.setText(adults + " Adults, " + children + " Children");
    }
    private void configureDatePicker(DatePicker datePicker, LocalDate today) {
        if (datePicker.getValue() != null && datePicker.getValue().isBefore(today)) {
            datePicker.setValue(today);
        }

        // Prevent selecting past dates
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
                if (string == null || string.isEmpty()) {
                    return null;
                }
                return LocalDate.parse(string, formatter);
            }
        });
    }
    @FXML
    public void refreshDates() {
        String fromDate = dateFrom.getValue() != null ? dateFrom.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
        String backDate = dateBack.getValue() != null ? dateBack.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
        dateMenu.setText(fromDate + " to " + backDate);
    }

    // Refresh method for departure ComboBox
    public void refreshDep(boolean filter) throws IOException {
        String filePath = "src/main/resources/flights.csv";
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



    // Key release handler for departure ComboBox
    @FXML
    public void handleDepKeyRelease(KeyEvent event) throws IOException {
        refreshDep(true);
    }

    // Mouse click handler for departure ComboBox
    @FXML
    public void handleDepMouseClick(MouseEvent event) throws IOException {
        refreshDep(false);
    }


    public void refresh(boolean filter) throws IOException {
        String filePath = "src/main/resources/flights.csv";
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        arrBox.getItems().clear();

        Set<String> uniqueDestinations = new HashSet<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",");
            if (fields.length == 9) {
                String arrivalAirport = fields[2];
                if (!filter || arrivalAirport.toLowerCase().startsWith(arrBox.getEditor().getText().toLowerCase())) {
                    uniqueDestinations.add(arrivalAirport);
                }
            }
        }

        arrBox.getItems().addAll(uniqueDestinations);

        // Clear selection if items are changed
        if (arrBox.getItems().isEmpty()) {
            arrBox.getSelectionModel().clearSelection();
            arrBox.hide();
        } else if (!arrBox.getEditor().getText().isEmpty()) {
            arrBox.show();
        }
    }



    @FXML
    public void handleKeyRelease(KeyEvent event) throws IOException {
        refresh(true);
    }

    @FXML
    public void handleMouseClick(MouseEvent event) throws IOException {
        refresh(false);
    }

    public void handleGoButton(ActionEvent event ) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        AppContext.setSelectedDestination(selectedDestination);
        AppContext.setSelectedDeparture(selectedDeparture);
        if (AppContext.getLoggedInUsername() == null) {
            showStyledAlert("Please log in first to access flights!", currentStage);
            return;
        }

        if (selectedDestination == null || selectedDestination.isEmpty()) {
            showStyledAlert("Please select a destination first!", currentStage);
            return;
        }

        if (dateFrom.getValue() == null || dateBack.getValue() == null) {
            showStyledAlert("Please select a departure and return date!", currentStage);
            return;
        }
        if(selectedDeparture == null || selectedDeparture.isEmpty()) {
            showStyledAlert("Please select a departure first!", currentStage);
            return;
        }
        if(selectedClass == null || selectedClass.isEmpty()) {
            showStyledAlert("Please select a class first!", currentStage);
            return;
        }


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("flights.fxml"));
            Parent newRoot = loader.load();

            FlightsController flightListController = loader.getController();

            // Pass values to FlightsController
            flightListController.setSelectedDestination(selectedDestination);
            flightListController.setSelectedDeparture(selectedDeparture);
            flightListController.setLoggedInUsername(AppContext.getLoggedInUsername());
            flightListController.setSelectedClass(selectedClass);

            Scene currentScene = currentStage.getScene();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
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
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
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
    public void onDestinationSelected(ActionEvent event) throws IOException {
        selectedDestination = arrBox.getValue();
        if (selectedDestination != null && !selectedDestination.isEmpty()) {
            System.out.println("Destination selected: " + selectedDestination);
            AppContext.setSelectedDestination(selectedDestination);

            // Preserve the current departure selection
            String currentDeparture = depBox.getValue();

            // Refresh the departure box while keeping the current selection
            refreshDep(false);

            // Restore the departure selection if valid
            if (currentDeparture != null && depBox.getItems().contains(currentDeparture)) {
                depBox.setValue(currentDeparture);
            }
        }
    }


    @FXML
    public void onDepartureSelected(ActionEvent event) throws IOException {
        selectedDeparture = depBox.getValue();
        if (selectedDeparture != null && !selectedDeparture.isEmpty()) {
            System.out.println("Departure selected: " + selectedDeparture);
            AppContext.setSelectedDeparture(selectedDeparture);

            // Preserve the current arrival selection
            String currentArrival = arrBox.getValue();

            // Refresh the arrival box while keeping the current selection
            refresh(false);

            // Restore the arrival selection if valid
            if (currentArrival != null && arrBox.getItems().contains(currentArrival)) {
                arrBox.setValue(currentArrival);
            }
        }
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
    private void updateLoginButton() {
        String loggedInFirstName = AppContext.getLoggedInFirstName();

        if (loggedInFirstName != null) {
            loginBtnMain.setText("Hello, " + loggedInFirstName);
            if (!loginBtnMain.getStyleClass().contains("logged-in")) {
                loginBtnMain.getStyleClass().add("logged-in");
            }
        } else {
            loginBtnMain.setText("LOG IN");
            loginBtnMain.getStyleClass().remove("logged-in");
        }
    }



}
