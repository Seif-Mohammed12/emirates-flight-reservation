        package com.example.emirates;

        import javafx.animation.FadeTransition;
        import javafx.application.Platform;
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
        import javafx.scene.text.Text;
        import javafx.scene.text.TextFlow;
        import javafx.stage.Modality;
        import javafx.stage.Stage;
        import javafx.stage.StageStyle;
        import javafx.util.Duration;

        import java.io.IOException;
        import java.time.LocalDate;
        import java.time.format.DateTimeFormatter;
        import java.util.Objects;
        import java.util.UUID;

        public class PaymentController {

            @FXML
            private VBox paymentContainer;
            @FXML
            private TextField cardNumberField;
            @FXML
            private ImageView cardIcon;

            @FXML
            private TextField expirationDateField;

            @FXML
            private TextField cvvField;
            @FXML
            private Button payButton;
            @FXML
            private Button goBackButton;
            @FXML
            private ToggleGroup paymentMethodGroup;
            @FXML
            private RadioButton visaRadioButton;
            @FXML
            private RadioButton digitalWalletRadioButton;
            @FXML
            private TextField walletIdField;
            @FXML
            private VBox visaPaymentSection;
            @FXML
            private VBox digitalWalletPaymentSection;

            private selectFlights.Flights selectedFlight;
            private BookingConfirmation.Passenger passenger;
            private String selectedClass;
            private int totalPassengers;
            private int adults;
            private int children;
            private String selectedSeat;
            private LocalDate departureDate;
            private LocalDate returnDate;
            private final double serviceFeeRate = 0.10;
            private final double taxRate = 0.14;
            private String updatedPrice;
            private String loggedInUsername;
            private String selectedDestination;
            private String selectedDeparture;

            @FXML
            private void initialize() {
                setupCardNumberFormatter();
                setupExpirationDateFormatter();
                setupCardIconUpdater();

                paymentMethodGroup = new ToggleGroup();
                visaRadioButton.setToggleGroup(paymentMethodGroup);
                digitalWalletRadioButton.setToggleGroup(paymentMethodGroup);

                paymentMethodGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
                    if (visaRadioButton.isSelected()) {
                        visaPaymentSection.setVisible(true);
                        visaPaymentSection.setManaged(true);
                        digitalWalletPaymentSection.setVisible(false);
                        digitalWalletPaymentSection.setManaged(false);
                    } else if (digitalWalletRadioButton.isSelected()) {
                        visaPaymentSection.setVisible(false);
                        visaPaymentSection.setManaged(false);
                        digitalWalletPaymentSection.setVisible(true);
                        digitalWalletPaymentSection.setManaged(true);
                    }
                });
            }
            private void setupCardIconUpdater() {
                cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
                    
                    String rawCardNumber = newValue.replaceAll("\\D", "");
                    String firstFourDigits = rawCardNumber.length() >= 4 ? rawCardNumber.substring(0, 4) : "";

                    
                    String cardType = getCardType(firstFourDigits);
                    if (cardType != null) {
                        cardIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/" + cardType + ".png"))));
                    } else {
                        cardIcon.setImage(null);
                    }
                });
            }

            private String getCardType(String firstFourDigits) {
                if (firstFourDigits.matches("^4[0-9]{3}$")) {
                    return "visa"; 
                } else if (firstFourDigits.matches("^5[1-5][0-9]{2}$")) {
                    return "mastercard"; 
                } else if (firstFourDigits.matches("^3[47][0-9]{2}$")) {
                    return "amex"; 
                } else if (firstFourDigits.matches("^6(?:011|5[0-9]{2})$")) {
                    return "discover"; 
                } else if (firstFourDigits.matches("^(?:2131|1800|35\\d{2})$")) {
                    return "jcb"; 
                }
                return null; 
            }

            private void setupExpirationDateFormatter() {
                expirationDateField.textProperty().addListener((observable, oldValue, newValue) -> {

                    String digitsOnly = newValue.replaceAll("\\D", "");

                    if (digitsOnly.length() > 4) {
                        digitsOnly = digitsOnly.substring(0, 4);
                    }

                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < digitsOnly.length(); i++) {
                        if (i == 2) {
                            formatted.append("/");
                        }
                        formatted.append(digitsOnly.charAt(i));
                    }

                    String formattedText = formatted.toString();
                    if (!newValue.equals(formattedText)) {
                        int caretPosition = expirationDateField.getCaretPosition();
                        expirationDateField.setText(formattedText);

                        expirationDateField.positionCaret(Math.min(caretPosition, formattedText.length()));
                    }
                });
            }

            private boolean isValidExpiryDate(String expiryDate) {
                if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                    return false; 
                }

                String[] parts = expiryDate.split("/");
                int month = Integer.parseInt(parts[0]);
                int year = Integer.parseInt(parts[1]) + 2000;

                LocalDate today = LocalDate.now();
                LocalDate expiration = LocalDate.of(year, month, 1).withDayOfMonth(1).plusMonths(1).minusDays(1);

                return !expiration.isBefore(today);
            }


            private void setupCardNumberFormatter() {
                cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
                    String digitsOnly = newValue.replaceAll("\\D", "");

                    
                    if (digitsOnly.length() > 16) {
                        digitsOnly = digitsOnly.substring(0, 16);
                    }

                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < digitsOnly.length(); i++) {
                        if (i > 0 && i % 4 == 0) {
                            formatted.append(" ");
                        }
                        formatted.append(digitsOnly.charAt(i));
                    }

                    String formattedText = formatted.toString();
                    if (!newValue.equals(formattedText)) {
                        int caretPosition = cardNumberField.getCaretPosition();
                        cardNumberField.setText(formattedText);

                        cardNumberField.positionCaret(Math.min(caretPosition, formattedText.length()));
                    }
                });
            }

            public void setPassengerInfo(BookingConfirmation.Passenger passenger) {
                this.passenger = passenger;
            }
            public void setFlightDetails(selectFlights.Flights flight, String selectedClass, int adults, int children,
                                         LocalDate departureDate, LocalDate returnDate, String selectedSeat, String updatedPrice, String selectedDestination, String selectedDeparture) {
                this.selectedFlight = flight;
                this.selectedClass = selectedClass;
                this.adults = adults;
                this.children = children;
                this.departureDate = departureDate;
                this.returnDate = returnDate;
                this.selectedSeat = selectedSeat;
                this.updatedPrice = updatedPrice;
                this.selectedDestination = selectedDestination;
                this.selectedDeparture = selectedDeparture;
                totalPassengers = adults + children;

                paymentContainer.getChildren().clear();
                if (flight != null) {
                    paymentContainer.getChildren().add(addFlightCard(flight));
                    paymentContainer.getChildren().add(createPaymentSummary(flight));
                }
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            private VBox addFlightCard(selectFlights.Flights flight) {
                VBox flightCard = createStyledVBox("flight-card", 10, 15);
                flightCard.setOnMouseEntered(event -> flightCard.setStyle("-fx-effect: dropshadow(gaussian, #888, 10, 0, 0, 0);"));
                flightCard.setOnMouseExited(event -> flightCard.setStyle("-fx-effect: none;"));

                
                HBox flightDetails = new HBox(20);
                flightDetails.setAlignment(Pos.CENTER);

                flightDetails.getChildren().addAll(
                        createDepartureSection(flight),
                        createSpacer(),
                        createDurationSection(flight),
                        createSpacer(),
                        createArrivalSection(flight)
                );

                HBox bottomSection = new HBox(20);
                bottomSection.setAlignment(Pos.CENTER_LEFT);
                bottomSection.setSpacing(50);
                HBox.setHgrow(bottomSection, Priority.ALWAYS);

                VBox additionalInfo = createAdditionalInfoSection();
                VBox pricingSection = createPricingSection(flight);

                HBox.setHgrow(additionalInfo, Priority.ALWAYS);
                HBox.setHgrow(pricingSection, Priority.ALWAYS);

                bottomSection.getChildren().addAll(additionalInfo, pricingSection);

                
                flightCard.getChildren().addAll(
                        flightDetails,
                        createSeparator(),
                        bottomSection,
                        createPlaneInfoSection(flight)
                );

                return flightCard;
            }


            private VBox createAdditionalInfoSection() {
                VBox additionalInfo = new VBox(10);
                additionalInfo.setAlignment(Pos.CENTER_LEFT);

                Label departureDateLabel = new Label("Departure Date: " + (departureDate != null ? departureDate.format(formatter) : "N/A"));
                departureDateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                Label returnDateLabel = new Label("Return Date: " + (returnDate != null ? returnDate.format(formatter) : "N/A"));
                returnDateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                Label seatLabel = new Label("Selected Seat: " + (selectedSeat != null ? selectedSeat : "Not Assigned"));
                seatLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                additionalInfo.getChildren().addAll(departureDateLabel, returnDateLabel, seatLabel);
                return additionalInfo;
            }


            private Region createSpacer() {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                return spacer;
            }

            private VBox createStyledVBox(String styleClass, int spacing, int padding) {
                VBox vbox = new VBox(spacing);
                vbox.getStyleClass().add(styleClass);
                vbox.setPadding(new javafx.geometry.Insets(padding));
                return vbox;
            }

            private VBox createDepartureSection(selectFlights.Flights flight) {
                VBox departure = new VBox(5);
                departure.setAlignment(Pos.CENTER_LEFT);

                Label departureTimeLabel = new Label(flight.departureTime);
                departureTimeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

                Label departureCityLabel = new Label(flight.departureCity);
                departureCityLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: rgba(215, 25, 32, 0.8);");

                departure.getChildren().addAll(departureTimeLabel, departureCityLabel);
                return departure;
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
                VBox arrival = new VBox(5);
                arrival.setAlignment(Pos.CENTER_RIGHT);

                Label arrivalTimeLabel = new Label(flight.arrivalTime);
                arrivalTimeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

                Label arrivalCityLabel = new Label(flight.arrivalCity);
                arrivalCityLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: rgba(215, 25, 32, 0.8);");

                arrival.getChildren().addAll(arrivalTimeLabel, arrivalCityLabel);
                return arrival;
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

            private VBox createPricingSection(selectFlights.Flights flight) {
                VBox pricing = new VBox(5);
                pricing.setAlignment(Pos.CENTER_RIGHT); 

                Label classLabel = new Label(selectedClass + " Class");
                classLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                String passengerText = totalPassengers + " Passenger" + (totalPassengers > 1 ? "s" : "");
                Label passengersLabel = new Label(passengerText);
                passengersLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                Label priceLabel = new Label("Base: " + updatedPrice);
                priceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #D71920;");

                HBox baggageInfo = new HBox(5);
                baggageInfo.setAlignment(Pos.CENTER_RIGHT);

                Text emoji = new Text("\uD83E\uDDF3 ");
                emoji.setStyle("-fx-font-size: 24px; -fx-font-family: 'Segoe UI Emoji';");

                Text baggageText = new Text("2x23kg");
                if (!"Economy".equals(selectedClass)) {
                    baggageText.setText("2x50kg");
                }
                baggageText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #555;");

                baggageInfo.getChildren().addAll(emoji, baggageText);

                pricing.getChildren().addAll(classLabel, passengersLabel, priceLabel, baggageInfo);
                if ("Economy".equals(selectedClass)) {
                    Label lowestPriceLabel = new Label("Lowest price");
                    lowestPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #777;");
                    pricing.getChildren().add(lowestPriceLabel);
                }

                return pricing;
            }


            private HBox createPlaneInfoSection(selectFlights.Flights flight) {
                HBox planeInfo = new HBox(10);
                planeInfo.setAlignment(Pos.CENTER_LEFT);

                Image planeImage = new Image(getClass().getResourceAsStream("/Daco_1321724.png"));
                ImageView planeView = new ImageView(planeImage);
                planeView.setFitWidth(30);
                planeView.setFitHeight(30);
                planeView.setPreserveRatio(true);

                planeInfo.getChildren().addAll(
                        planeView,
                        new Label(flight.aircraftDetails),
                        new Label(flight.getFlightNo())
                );

                return planeInfo;
            }

            private Separator createSeparator() {
                return new Separator();
            }

            private VBox createPaymentSummary(selectFlights.Flights flight) {
                VBox paymentSummary = new VBox(10);
                paymentSummary.getStyleClass().add("payment-summary");

                double basePrice = parsePrice(updatedPrice);
                double serviceFee = basePrice * serviceFeeRate;
                double tax = basePrice * taxRate;
                double totalAmount = basePrice + serviceFee + tax;

                Label summaryTitle = new Label("Payment Summary");
                summaryTitle.getStyleClass().add("payment-summary-title");

                Label serviceFeeLabel = new Label("Service Fee: EGP " + String.format("%.2f", serviceFee));
                serviceFeeLabel.getStyleClass().add("payment-summary-label");

                Label taxLabel = new Label("Taxes: EGP " + String.format("%.2f", tax));
                taxLabel.getStyleClass().add("payment-summary-label");

                Label totalLabel = new Label("To Be Paid: EGP " + String.format("%.2f", totalAmount));
                totalLabel.getStyleClass().add("payment-summary-total");

                paymentSummary.getChildren().addAll(summaryTitle, serviceFeeLabel, taxLabel, totalLabel);
                return paymentSummary;
            }

            private double parsePrice(String price) {
                try {
                    return Double.parseDouble(price.replace("EGP", "").replace(",", "").trim());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }

            @FXML
            private void handlePayButton() {
                String cardNumber = cardNumberField.getText().trim();
                String expiryDate = expirationDateField.getText().trim();
                String cvv = cvvField.getText().trim();
                String walletId = walletIdField.getText().trim();
                Stage currentStage = (Stage) payButton.getScene().getWindow();

                if (!validateInputs(cardNumber, expiryDate, cvv, walletId)) {
                    showStyledAlert("Please fill in all fields correctly.", currentStage);
                    return;
                }

                double basePrice = parsePrice(updatedPrice);
                double serviceFee = basePrice * serviceFeeRate;
                double tax = basePrice * taxRate;
                double totalAmount = basePrice + serviceFee + tax;

                if (visaRadioButton.isSelected()) {
                    VisaPayment payment = new VisaPayment(cardNumber.replaceAll("\\s", ""), expiryDate, cvv);
                    if (!payment.processPayment(totalAmount)) {
                        showStyledAlert("Invalid card number. Please check your details.", currentStage);
                        return;
                    }
                } else if (digitalWalletRadioButton.isSelected()) {
                    DigitalWalletPayment payment = new DigitalWalletPayment(walletId);
                    if (!payment.processPayment(totalAmount)) {
                        showStyledAlert("Invalid wallet ID. Please check your details.", currentStage);
                        return;
                    }
                }

                String paymentId = generatePaymentId();
                String bookingId = BookingConfirmation.Booking.generateBookingId();

                BookingConfirmation bookingConfirmation = new BookingConfirmation();

                BookingConfirmation.Passenger passenger = bookingConfirmation.createPassenger(selectedSeat, selectedClass, adults, children);

                BookingConfirmation.Booking booking = bookingConfirmation.createBooking(selectedFlight, passenger, departureDate, returnDate, totalAmount);

                AppContext.addBooking(booking);

                showSuccessAlert(
                        "Your payment of EGP " + String.format("%.2f", totalAmount) +
                                " has been processed successfully!\nPayment ID: " + paymentId,
                        currentStage,
                        () -> navigateToMain(currentStage)
                );
            }



            private void navigateToMain(Stage currentStage) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                    Parent mainPage = loader.load();

                    MainController mainController = loader.getController();

                    String loggedInUsername = AppContext.getLoggedInUsername();
                    if (loggedInUsername != null) {
                        mainController.setLoggedInUsername(loggedInUsername);
                    }

                    Scene currentScene = currentStage.getScene();

                    
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);

                    fadeOut.setOnFinished(event -> {
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



            @FXML
            private void handlegoBack() {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("BookingConfirmation.fxml"));
                    Parent bookingConfirmationPage = loader.load();

                    BookingConfirmationController bookingConfirmationController = loader.getController();

                    String passengerName = AppContext.getLoggedInFirstName() + " " + AppContext.getLoggedInLastName();
                    String contactMethod = AppContext.getLoggedInEmail();
                    BookingConfirmation.Passenger passenger = new BookingConfirmation.Passenger(passengerName, selectedSeat, contactMethod, selectedClass, adults, children);

                    bookingConfirmationController.setBookingDetails(selectedFlight, passenger, selectedSeat, selectedClass, updatedPrice, departureDate, returnDate, adults, children, selectedDestination, selectedDeparture);
                    bookingConfirmationController.setLoggedInUsername(AppContext.getLoggedInUsername());


                    Stage stage = (Stage) goBackButton.getScene().getWindow();
                    Scene currentScene = stage.getScene();

                    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentScene.getRoot());
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);

                    fadeOut.setOnFinished(event -> {
                        currentScene.setRoot(bookingConfirmationPage);

                        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), bookingConfirmationPage);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                    });

                    fadeOut.play();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            private boolean validateInputs(String cardNumber, String expiryDate, String cvv, String walletId) {
                String rawCardNumber = cardNumber.replaceAll("\\D", "");
                Stage currentStage = (Stage) payButton.getScene().getWindow();

                if (visaRadioButton.isSelected()) {
                    if (rawCardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
                        showStyledAlert("All fields are required.", currentStage);
                        return false;
                    }

                    if (!isValidCardNumber(rawCardNumber)) {
                        showStyledAlert("Invalid card number. Ensure it's 16 digits and valid.", currentStage);
                        return false;
                    }

                    if (rawCardNumber.length() != 16) {
                        showStyledAlert("Card number must be 16 digits.", currentStage);
                        return false;
                    }

                    if (!isValidExpiryDate(expiryDate)) {
                        showStyledAlert("Invalid expiry date. Ensure it's in MM/YY format and not expired.", currentStage);
                        return false;
                    }

                    if (!cvv.matches("\\d{3}")) {
                        showStyledAlert("CVV must be a 3-digit number.", currentStage);
                        return false;
                    }
                } else if (digitalWalletRadioButton.isSelected()) {
                    if (walletId.isEmpty()) {
                        showStyledAlert("Wallet ID is required.", currentStage);
                        return false;
                    }
                }

                return true;
            }


            private String generatePaymentId() {
                return UUID.randomUUID().toString().toUpperCase();
            }

            private boolean isValidCardNumber(String cardNumber) {
                String cardRegex = "^(?:4[0-9]{12}(?:[0-9]{3})?" + 
                        "|5[1-5][0-9]{14}" +                       
                        "|3[47][0-9]{13}" +                        
                        "|6(?:011|5[0-9]{2})[0-9]{12}" +           
                        "|(?:2131|1800|35\\d{3})\\d{11})$";        

                boolean matches = cardNumber.matches(cardRegex);

                return matches;
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

                dialogPane.setStyle("-fx-background-color: #f8d7da; -fx-background-radius: 20; -fx-border-radius: 20;");

                dialogPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                dialogPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

                Stage alertStage = (Stage) dialogPane.getScene().getWindow();
                alertStage.initStyle(StageStyle.TRANSPARENT);
                alertStage.getScene().setFill(null);

                alert.showAndWait();
            }

            private void showSuccessAlert(String message, Stage owner, Runnable onCloseAction) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText(message);
                alert.initOwner(owner);
                alert.initModality(Modality.APPLICATION_MODAL);

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                dialogPane.getStyleClass().add("success-dialog");
                dialogPane.setHeaderText(null);

                TextFlow textFlow = new TextFlow();
                Text text = new Text(message);
                text.setWrappingWidth(300);
                textFlow.getChildren().add(text);
                dialogPane.setContent(textFlow);

                dialogPane.setStyle("-fx-background-color: #d4edda; -fx-background-radius: 20; -fx-border-radius: 20; " +
                        "-fx-border-color: #388e3c; -fx-border-width: 2px;");

                alert.setHeaderText(null);
                alert.setGraphic(null);

                
                if (onCloseAction != null) {
                    alert.setOnHidden(event -> onCloseAction.run());
                }

                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.initStyle(StageStyle.TRANSPARENT);
                alertStage.getScene().setFill(null);

                alert.showAndWait();
            }

            public void setLoggedInUsername(String username) {
                this.loggedInUsername = username;
            }
        }
