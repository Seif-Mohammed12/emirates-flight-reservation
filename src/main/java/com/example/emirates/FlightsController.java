package com.example.emirates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightsController {

    // =================================================================================
    // FXML Fields
    // =================================================================================
    @FXML
    private VBox flightsContainer;
    @FXML
    private ToggleButton economyButton;
    @FXML
    private ToggleButton businessButton;
    @FXML
    private ToggleButton firstClassButton;
    @FXML
    private Button iconBtnFlights;
    @FXML
    private Button loginBtnFlights;
    @FXML
    private Label titleLabel;
    @FXML
    private Label sortLabel;
    @FXML
    private ChoiceBox<String> sortChoiceBox;

    // =================================================================================
    // Instance Variables
    // =================================================================================
    private String selectedClass;
    private String selectedDestination;
    private String selectedDeparture;
    private String loggedInUsername;
    private int adults;
    private int children;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private List<selectFlights.Flights> flights = new ArrayList<>();

    // =================================================================================
    // Initialization and Setup
    // =================================================================================
    @FXML
    public void initialize() {
        sortChoiceBox.setOnAction(event -> updateFlightCards());
        try {
            flights = FileManager.loadFlightsFromCSV("flights.csv", true, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }

        economyButton.setOnAction(event -> setSelectedClass("Economy"));
        businessButton.setOnAction(event -> setSelectedClass("Business"));
        firstClassButton.setOnAction(event -> setSelectedClass("First"));

        ToggleGroup classToggleGroup = new ToggleGroup();
        economyButton.setToggleGroup(classToggleGroup);
        businessButton.setToggleGroup(classToggleGroup);
        firstClassButton.setToggleGroup(classToggleGroup);

        if (selectedClass == null || selectedClass.isEmpty()) {
            economyButton.setSelected(true);
        } else {
            updateToggleButtons();
        }
        economyButton.setOnAction(event -> setSelectedClass("Economy"));
        businessButton.setOnAction(event -> setSelectedClass("Business"));
        firstClassButton.setOnAction(event -> setSelectedClass("First"));

        if (titleLabel != null) {
            Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 50);
            Font customFontSmall = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 24);
            sortLabel.setFont(customFontSmall);
            titleLabel.setFont(customFontLarge);
        }

        updateFlightCards();

        loginBtnFlights.setOnMouseEntered(event -> {
            if (AppContext.getLoggedInUsername() != null) {
                loginBtnFlights.setText("Logout");
            }
        });

        loginBtnFlights.setOnMouseExited(event -> updateLoginButton());

        updateLoginButton();
    }

    public void updateToggleButtons() {
        if ("Economy".equalsIgnoreCase(selectedClass)) {
            economyButton.setSelected(true);
        } else if ("Business".equalsIgnoreCase(selectedClass)) {
            businessButton.setSelected(true);
        } else if ("First".equalsIgnoreCase(selectedClass)) {
            firstClassButton.setSelected(true);
        }
    }

    private void updateLoginButton() {
        String loggedInFirstName = AppContext.getLoggedInFirstName();

        if (loggedInFirstName != null) {
            loginBtnFlights.setText("Hello, " + loggedInFirstName);
        } else {
            loginBtnFlights.setText("Login");
        }
        loginBtnFlights.setMinWidth(Region.USE_COMPUTED_SIZE);
        loginBtnFlights.setPrefWidth(Region.USE_COMPUTED_SIZE);
        loginBtnFlights.setMaxWidth(Double.MAX_VALUE);
    }

    // =================================================================================
    // Property Setters
    // =================================================================================
    public void setAdults(int adults) {
        this.adults = adults;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public void setSelectedDestination(String destination) {
        this.selectedDestination = destination;
        System.out.println("Selected Destination: " + selectedDestination);
        updateFlightCards();
    }

    public void setSelectedDeparture(String departure) {
        this.selectedDeparture = departure;
        System.out.println("Selected Departure: " + selectedDeparture);
        updateFlightCards();
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
        System.out.println("Logged-in username in FlightsController: " + loggedInUsername);
    }

    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
        System.out.println("Selected Class: " + selectedClass);
        updateFlightCards();
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    // =================================================================================
    // Navigation Methods
    // =================================================================================
    @FXML
    private void goToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            AnchorPane mainPage = loader.load();
            MainController mainController = loader.getController();

            String loggedInUsername = AppContext.getLoggedInUsername();
            mainController.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) iconBtnFlights.getScene().getWindow();
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
            showErrorDialog("Error", "There was an issue loading the main view.");
        }
    }

    @FXML
    private void handleLoginFlightButton(ActionEvent event) {
        System.out.println("Login button clicked");

        String loggedInUsername = AppContext.getLoggedInUsername();

        if (loggedInUsername != null) {
            AppContext.setLoggedInUsername(null);
            AppContext.setLoggedInFirstName(null);
            updateLoginButton();
            System.out.println("User logged out.");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                Parent mainPage = loader.load();

                Stage stage = (Stage) loginBtnFlights.getScene().getWindow();
                Scene currentScene = stage.getScene();

                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);

                fadeOut.setOnFinished(e -> {
                    currentScene.setRoot(mainPage);

                    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), mainPage);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });

                fadeOut.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            updateLoginButton();
        }
    }

    private void showSeatSelection(selectFlights.Flights flight) {
        Stage currentStage = (Stage) flightsContainer.getScene().getWindow();
        Scene currentScene = currentStage.getScene();

        Pane overlayPane = new Pane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        overlayPane.setPrefSize(currentScene.getRoot().getBoundsInLocal().getWidth(),
                currentScene.getRoot().getBoundsInLocal().getHeight());

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(100, 100);
        loadingIndicator.setStyle("-fx-progress-color: #D71920; -fx-background-color: transparent;");
        loadingIndicator.layoutXProperty().bind(overlayPane.widthProperty().divide(2).subtract(50));
        loadingIndicator.layoutYProperty().bind(overlayPane.heightProperty().divide(2).subtract(50));
        overlayPane.getChildren().add(loadingIndicator);

        ((Pane) currentScene.getRoot()).getChildren().add(overlayPane);

        Task<Parent> loadSeatSelectionTask = new Task<>() {
            @Override
            protected Parent call() throws IOException {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("SeatSelection.fxml"));
                Parent seatSelectionLayout = loader.load();

                SeatSelectionController seatSelectionController = loader.getController();
                seatSelectionController.setLoggedInUsername(AppContext.getLoggedInUsername());
                seatSelectionController.setSelectedClass(selectedClass);
                seatSelectionController.setSelectedDestination(selectedDestination);
                seatSelectionController.setSelectedDeparture(selectedDeparture);
                seatSelectionController.setAdults(adults);
                seatSelectionController.setChildren(children);
                seatSelectionController.setSelectedFlight(flight);
                String updatedPrice = getUpdatedPrice(flight);
                seatSelectionController.setUpdatedPrice(updatedPrice);
                seatSelectionController.setDepartureDate(departureDate);
                seatSelectionController.setReturnDate(returnDate);

                return seatSelectionLayout;
            }
        };

        loadSeatSelectionTask.setOnSucceeded(workerStateEvent -> {
            Parent seatSelectionLayout = loadSeatSelectionTask.getValue();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                currentScene.setRoot(seatSelectionLayout);

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), seatSelectionLayout);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
            ((Pane) currentScene.getRoot()).getChildren().remove(overlayPane);
        });

        loadSeatSelectionTask.setOnFailed(workerStateEvent -> {
            Throwable error = loadSeatSelectionTask.getException();
            error.printStackTrace();
            ((Pane) currentScene.getRoot()).getChildren().remove(overlayPane);
        });

        Thread loadThread = new Thread(loadSeatSelectionTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    // =================================================================================
    // Flight Card Management
    // =================================================================================
    public void updateFlightCards() {
        flightsContainer.getChildren().clear();

        List<selectFlights.Flights> filteredFlights = flights.stream()
                .filter(flight -> filterByDestination(flight) && filterByDeparture(flight))
                .sorted(this::sortFlights)
                .collect(Collectors.toList());

        filteredFlights.forEach(flight -> addFlightCard(flight));
    }

    private boolean filterByDestination(selectFlights.Flights flight) {
        return selectedDestination == null || selectedDestination.isEmpty()
                || flight.arrivalCity.equalsIgnoreCase(selectedDestination);
    }

    private boolean filterByDeparture(selectFlights.Flights flight) {
        return selectedDeparture == null || selectedDeparture.isEmpty()
                || flight.departureCity.equalsIgnoreCase(selectedDeparture);
    }

    private int sortFlights(selectFlights.Flights f1, selectFlights.Flights f2) {
        String selectedSort = sortChoiceBox.getValue();
        if ("Lowest price".equals(selectedSort)) {
            return Double.compare(parsePrice(f1.price), parsePrice(f2.price));
        } else if ("Shortest duration".equals(selectedSort)) {
            return Integer.compare(parseDuration(f1.duration), parseDuration(f2.duration));
        }
        return 0;
    }

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replace("EGP", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            return Double.MAX_VALUE;
        }
    }

    private int parseDuration(String duration) {
        String[] parts = duration.split(" ");
        int hours = 0, minutes = 0;
        try {
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].contains("hr")) {
                    hours = Integer.parseInt(parts[i - 1]);
                } else if (parts[i].contains("min")) {
                    minutes = Integer.parseInt(parts[i - 1]);
                }
            }
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
        return hours * 60 + minutes;
    }

    // =================================================================================
    // UI Component Creation
    // =================================================================================
    private void addFlightCard(selectFlights.Flights flight) {
        VBox flightCard = createStyledVBox("flight-card", 10, 15);
        flightCard.setOnMouseEntered(
                event -> flightCard.setStyle("-fx-effect: dropshadow(gaussian, #888, 10, 0, 0, 0);"));
        flightCard.setOnMouseExited(event -> flightCard.setStyle("-fx-effect: none;"));
        flightCard.setOnMouseClicked(event -> showSeatSelection(flight));

        HBox flightDetails = new HBox(20);
        flightDetails.setAlignment(Pos.CENTER);

        flightDetails.getChildren().addAll(
                createDepartureSection(flight),
                createSpacer(),
                createDurationSection(flight),
                createSpacer(),
                createArrivalSection(flight));

        HBox pricingSection = createPricingSection(flight);

        HBox planeInfoSection = createPlaneInfoSection(flight);

        flightCard.getChildren().addAll(
                flightDetails,
                createSeparator(),
                pricingSection,
                planeInfoSection);

        flightsContainer.getChildren().add(flightCard);
    }

    private VBox createStyledVBox(String styleClass, int spacing, int padding) {
        VBox vbox = new VBox(spacing);
        vbox.getStyleClass().add(styleClass);
        vbox.setPadding(new javafx.geometry.Insets(padding));
        return vbox;
    }

    private HBox createDepartureSection(selectFlights.Flights flight) {
        VBox departure = new VBox(5);
        departure.setAlignment(Pos.CENTER_LEFT);

        Label departureTimeLabel = new Label(flight.departureTime);
        departureTimeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label departureCityLabel = new Label(flight.departureCity);
        departureCityLabel
                .setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: rgba(215, 25, 32, 0.8);");

        departure.getChildren().addAll(departureTimeLabel, departureCityLabel);

        return new HBox(departure);
    }

    private VBox createDurationSection(selectFlights.Flights flight) {
        VBox duration = new VBox(5);
        duration.setAlignment(Pos.CENTER);

        duration.getChildren().addAll(
                new Label(flight.duration),
                createArrowLine(),
                new Label(flight.stops));

        return duration;
    }

    private VBox createArrivalSection(selectFlights.Flights flight) {
        VBox arrival = new VBox(5);
        arrival.setAlignment(Pos.CENTER_RIGHT);

        Label arrivalTimeLabel = new Label(flight.arrivalTime);
        arrivalTimeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label arrivalCityLabel = new Label(flight.arrivalCity);
        arrivalCityLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: rgba(215, 25, 32, 0.8);");

        arrival.getChildren().addAll(arrivalTimeLabel, arrivalCityLabel);

        return arrival;
    }

    private VBox createLabeledSection(String label1Text, String label2Text, Pos alignment) {
        VBox box = new VBox(5);
        box.setAlignment(alignment);
        Label label1 = new Label(label1Text);
        label1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label label2 = new Label(label2Text);
        label2.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");
        box.getChildren().addAll(label1, label2);
        return box;
    }

    private HBox createArrowLine() {
        HBox lineWithArrow = new HBox();
        lineWithArrow.setAlignment(Pos.CENTER);
        Line line = new Line(0, 0, 850, 0);
        line.setStrokeWidth(2.5);
        line.setStroke(Color.GRAY);

        Label arrow = new Label("➔");
        arrow.setStyle("-fx-font-size: 18px; -fx-text-fill: #777; -fx-padding: 1 0 0 -2");

        lineWithArrow.getChildren().addAll(line, arrow);
        return lineWithArrow;
    }

    private HBox createPricingSection(selectFlights.Flights flight) {
        HBox pricingSection = new HBox(20);
        pricingSection.setStyle("-fx-alignment: center-right;");

        VBox pricing = new VBox(5);

        Label classLabel = new Label(selectedClass + " Class");
        classLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        int totalPassengers = adults + children;
        String passengerText = totalPassengers + " Passenger" + (totalPassengers > 1 ? "s" : "");
        Label passengersLabel = new Label(passengerText);
        passengersLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Label priceLabel = new Label("from " + getUpdatedPrice(flight));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #D71920;");

        Text emoji = new Text("\uD83E\uDDF3 ");
        emoji.setStyle("-fx-font-size: 24px; -fx-font-family: 'Segoe UI Emoji';");

        Text baggageText = new Text("2x23kg");
        if (!"Economy".equals(selectedClass)) {
            baggageText.setText("2x50kg");
        }
        baggageText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #555;");

        TextFlow baggageTextFlow = new TextFlow(emoji, baggageText);

        pricing.getChildren().addAll(classLabel, passengersLabel, priceLabel, baggageTextFlow);

        if ("Economy".equals(selectedClass)) {
            Label lowestPriceLabel = new Label("Lowest price");
            lowestPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");
            pricing.getChildren().add(lowestPriceLabel);
        }

        pricingSection.getChildren().add(pricing);
        return pricingSection;
    }

    private HBox createPlaneInfoSection(selectFlights.Flights flight) {
        HBox planeInfo = new HBox(10);
        planeInfo.setStyle("-fx-alignment: center-left;");

        Image planeImage = new Image(getClass().getResourceAsStream("/Daco_1321724.png"));
        ImageView planeView = new ImageView(planeImage);
        planeView.setFitWidth(30);
        planeView.setFitHeight(30);
        planeView.setPreserveRatio(true);

        planeInfo.getChildren().addAll(
                planeView,
                new Label(flight.aircraftDetails),
                new Label(flight.getFlightNo()));

        return planeInfo;
    }

    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: rgba(215, 25, 32, 0.6)");
        return separator;
    }

    // =================================================================================
    // Utility Methods
    // =================================================================================
    private String getUpdatedPrice(selectFlights.Flights flight) {
        try {
            double basePrice = parsePrice(flight.price);
            double adjustedPricePerAdult = basePrice;
            double adjustedPricePerChild = basePrice * 0.5;

            if ("Business".equals(selectedClass)) {
                adjustedPricePerAdult *= 1.5;
                adjustedPricePerChild *= 1.5;
            } else if ("First".equals(selectedClass)) {
                adjustedPricePerAdult *= 2.0;
                adjustedPricePerChild *= 2.0;
            }

            int totalPassengers = adults + children;

            double totalPrice = (adults * adjustedPricePerAdult) + (children * adjustedPricePerChild);

            return "EGP " + String.format("%.2f", totalPrice);
        } catch (Exception e) {
            return "Price not available";
        }
    }

    private Region createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("error-dialog");
        alert.showAndWait();
    }

}
