package com.example.emirates;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightsController {

    @FXML
    private VBox flightsContainer;

    @FXML
    private ToggleButton economyButton;

    @FXML
    private ToggleButton businessButton;

    @FXML
    private ToggleButton firstClassButton;

    @FXML
    private Label titleLabel;

    @FXML
    private ChoiceBox<String> sortChoiceBox;

    private String selectedClass = "Economy";
    private String selectedDestination;

    private List<selectFlights.Flights> flights = new ArrayList<>();

    @FXML
    public void initialize() {
        sortChoiceBox.setOnAction(event -> updateFlightCards());
        try {
            flights = FileManager.loadFlightsFromCSV("src/main/resources/flights.csv", true, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }

        economyButton.setOnAction(event -> onClassSelection("Economy"));
        businessButton.setOnAction(event -> onClassSelection("Business"));
        firstClassButton.setOnAction(event -> onClassSelection("First"));

        ToggleGroup classToggleGroup = new ToggleGroup();
        economyButton.setToggleGroup(classToggleGroup);
        businessButton.setToggleGroup(classToggleGroup);
        firstClassButton.setToggleGroup(classToggleGroup);

        economyButton.setSelected(true);

        // Optimize Font Loading
        if (titleLabel != null) {
            Font customFontLarge = Font.loadFont(getClass().getResourceAsStream("/fonts/Emirates_Medium.ttf"), 60);
            titleLabel.setFont(customFontLarge);
        }

        // Initial load
        updateFlightCards();
    }

    public void setSelectedDestination(String destination) {
        this.selectedDestination = destination;
        updateFlightCards();
    }

    private void onClassSelection(String className) {
        selectedClass = className;
        updateFlightCards();
    }

    private void updateFlightCards() {
        // Clear container
        flightsContainer.getChildren().clear();

        // Filter and sort flights
        List<selectFlights.Flights> filteredFlights = flights.stream()
                .filter(this::filterByDestination)
                .sorted(this::sortFlights)
                .collect(Collectors.toList());

        // Add cards
        filteredFlights.forEach(this::addFlightCard);
    }

    private boolean filterByDestination(selectFlights.Flights flight) {
        return selectedDestination == null || selectedDestination.isEmpty()
                || flight.arrivalCity.equalsIgnoreCase(selectedDestination);
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

    private void addFlightCard(selectFlights.Flights flight) {
        // Flight Card (VBox)
        VBox flightCard = createStyledVBox("flight-card", 10, 15);
        flightCard.setOnMouseEntered(event -> flightCard.setStyle("-fx-effect: dropshadow(gaussian, #888, 10, 0, 0, 0);"));
        flightCard.setOnMouseExited(event -> flightCard.setStyle("-fx-effect: none;"));
        flightCard.setOnMouseClicked(event -> showFlightDetails(flight));

        // Flight Details
        HBox flightDetails = new HBox(20);
        flightDetails.setAlignment(Pos.CENTER);

        flightDetails.getChildren().addAll(
                createDepartureSection(flight),
                createSpacer(),
                createDurationSection(flight),
                createSpacer(),
                createArrivalSection(flight)
        );

        // Pricing Section
        HBox pricingSection = createPricingSection(flight);

        // Plane Info Section
        HBox planeInfoSection = createPlaneInfoSection(flight);

        // Add to Flight Card
        flightCard.getChildren().addAll(
                flightDetails,
                createSeparator(),
                pricingSection,
                planeInfoSection
        );

        flightsContainer.getChildren().add(flightCard);
    }

    private VBox createStyledVBox(String styleClass, int spacing, int padding) {
        VBox vbox = new VBox(spacing);
        vbox.getStyleClass().add(styleClass);
        vbox.setPadding(new javafx.geometry.Insets(padding));
        return vbox;
    }

    private HBox createDepartureSection(selectFlights.Flights flight) {
        VBox departure = createLabeledSection(
                flight.departureTime,
                flight.departureCity,
                Pos.CENTER_LEFT
        );
        return new HBox(departure);
    }

    private VBox createDurationSection(selectFlights.Flights flight) {
        VBox duration = new VBox(5);
        duration.setAlignment(Pos.CENTER);

        duration.getChildren().addAll(
                new Label(flight.duration),
                createArrowLine(),
                new Label(flight.stops)
        );

        return duration;
    }

    private VBox createArrivalSection(selectFlights.Flights flight) {
        return createLabeledSection(
                flight.arrivalTime,
                flight.arrivalCity,
                Pos.CENTER_RIGHT
        );
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
        arrow.setStyle("-fx-font-size: 18px; -fx-text-fill: #777;");

        lineWithArrow.getChildren().addAll(line, arrow);
        return lineWithArrow;
    }

    private HBox createPricingSection(selectFlights.Flights flight) {
        HBox pricingSection = new HBox(20);
        pricingSection.setStyle("-fx-alignment: center-right;");

        VBox pricing = new VBox(5);
        pricing.getChildren().addAll(
                new Label(selectedClass + " Class"),
                new Label("from " + getUpdatedPrice(flight))
        );

        if ("Economy".equals(selectedClass)) {
            pricing.getChildren().add(new Label("Lowest price"));
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
                new Label(flight.flightNo)
        );

        return planeInfo;
    }

    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #cccccc;");
        return separator;
    }

    private String getUpdatedPrice(selectFlights.Flights flight) {
        try {
            double basePrice = parsePrice(flight.price);
            double adjustedPrice = switch (selectedClass) {
                case "Business" -> basePrice * 1.5;
                case "First" -> basePrice * 2;
                default -> basePrice;
            };
            return "EGP " + String.format("%.2f", adjustedPrice);
        } catch (Exception e) {
            return "Price not available";
        }
    }

    private Region createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private void showFlightDetails(selectFlights.Flights flight) {
        System.out.println("Details for Flight No: " + flight.flightNo);
    }
}

