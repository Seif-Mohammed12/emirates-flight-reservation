package com.example.emirates;

import javafx.animation.FadeTransition;
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
import javafx.stage.Stage;
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
    private ComboBox<String> arrBox;
    @FXML
    private ComboBox<String> classMenu;
    @FXML
    private Button goBtn;
    @FXML
    private Button loginBtnMain;
    private String selectedDestination;
    private String loggedInUsername;


    @FXML
    public void initialize() {
        // Load the custom font
        Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
        Font customFontSmall = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 32);
        titleLabel.setFont(customFontLarge);
        welcomeLabel.setFont(customFontSmall);
        titleLabel.setStyle("-fx-font-weight: bold;");
        welcomeLabel.setStyle("-fx-font-weight: bold; ");

        LocalDate today = LocalDate.now();
        configureDatePicker(dateFrom, today);
        configureDatePicker(dateBack, today);

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

        if (!uniqueDestinations.isEmpty() && !arrBox.getEditor().getText().isEmpty()) {
            arrBox.show();
        } else {
            arrBox.hide();
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

    public void handleGoButton(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        AppContext.setSelectedDestination(selectedDestination);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("flights.fxml"));
            Parent newRoot = loader.load();

            FlightsController flightListController = loader.getController();
            flightListController.setSelectedDestination(selectedDestination);


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
        }
        catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onDestinationSelected(ActionEvent event) {
        selectedDestination = arrBox.getValue();
        if (selectedDestination != null && !selectedDestination.isEmpty()) {
            System.out.println("Destination selected: " + selectedDestination);
            AppContext.setSelectedDestination(selectedDestination);
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
