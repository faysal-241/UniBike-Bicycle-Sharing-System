module com.example.unibike_version_4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.unibike_version_4 to javafx.fxml;
    exports com.example.unibike_version_4.controller;
    opens com.example.unibike_version_4.controller to javafx.fxml;
}