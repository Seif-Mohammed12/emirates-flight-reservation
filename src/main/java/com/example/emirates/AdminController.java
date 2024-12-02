package com.example.emirates;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class AdminController {

    @FXML
    private TableView<String> actionTable;

    @FXML
    private TableColumn<String, String> actionColumn;
    @FXML
    private VBox rootPane;

    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    public void initialize() {
        // Clear existing items and add actions to the TableView
        actionTable.getItems().clear();
        actionTable.getItems().addAll(
                "Add Flights",
                "Delete Flights",
                "Update Flights",
                "Add User",
                "Delete User"
        );

        // Set the cell value factory to display the string in the action column
        actionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        actionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    // Handle when an action is selected
    @FXML
    private void handleActionSelected(MouseEvent event) {
        String selectedAction = actionTable.getSelectionModel().getSelectedItem();
        if (selectedAction != null && !selectedAction.trim().isEmpty()) {
            System.out.println("Selected Action: " + selectedAction);
            performAction(selectedAction);
        }
    }

    private void performAction(String selectedAction) {
        switch (selectedAction) {
            case "Add Flights":
                showAlert("Action Selected", "Add Flights action triggered.");
                break;
            case "Delete Flights":
                // Handle Delete Flights action
                showAlert("Action Selected", "Delete Flights action triggered.");
                break;
            case "Update Flights":
                // Handle Update Flights action
                showAlert("Action Selected", "Update Flights action triggered.");
                break;
            case "Add User":
                // Handle Add User action
                showAlert("Action Selected", "Add User action triggered.");
                break;
            case "Delete User":
                // Handle Delete User action
                showAlert("Action Selected", "Delete User action triggered.");
                break;
            default:
                System.out.println("Unknown action");
                break;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
