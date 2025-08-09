package com.example.unibike_version_4.controller;
import com.example.unibike_version_4.model.User;
import com.example.unibike_version_4.model.Bicycle;
import com.example.unibike_version_4.model.Ride;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        User.loadFromFile(); // ✅ Load users on app start
        Bicycle.loadFromFile(); // Load bicycles
        Ride.loadFromFile();

        primaryStage = stage;
        changeScene("login.fxml");
        stage.setTitle("UniBike");
        stage.show();
    }
    @Override
    public void stop() throws Exception {
        super.stop();
        User.saveAllUsers();
        Bicycle.saveAllBicycles(); // Save bicycles
        Ride.saveAllToFile();    // Save rides
    }
    public static void changeScene(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/unibike_version_4/" + fxmlFile));
        if (loader.getLocation() == null) {
            throw new IOException("FXML file not found: " + fxmlFile);
        }
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}
