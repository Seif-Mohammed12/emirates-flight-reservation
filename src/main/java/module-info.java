module com.example.emirates {
    requires javafx.controls;
    requires javafx.fxml;
    requires javax.mail.api;


    opens com.example.emirates to javafx.fxml;
    exports com.example.emirates;
}