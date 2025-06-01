package com.kelompok5.ipark;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.kelompok5.ipark.utils.Connector;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class ParkLocsLocationController implements Initializable {

    Connector connector = new Connector();

    @FXML
    private void backToMain() throws IOException {
        App.setRoot("scene_main");
    }

    @FXML
    private GridPane grid;

    private final int COLUMNS = 4;

    // Contoh data lokasi
    private List<String> locationNames = new ArrayList<>();

    private void loadData() throws SQLException {
        ResultSet resultSet = connector.loadItem("parkings", new String[] { "name" });
        locationNames.clear(); // in case it gets called more than once

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            locationNames.add(name);
        }
    }

    private void populateGrid() {
        for (int i = 0; i < locationNames.size(); i++) {
            int row = i / COLUMNS;
            int col = i % COLUMNS;

            StackPane card = createLocationCard(locationNames.get(i));
            grid.add(card, col, row);
        }
    }

    private StackPane createLocationCard(String name) {
        StackPane pane = new StackPane();
        pane.setPrefSize(200, 300);

        Button imageButton = new Button();
        imageButton.setPrefSize(200, 200);
        imageButton.setOpacity(0.8);
        imageButton.setId("locationImage");
        imageButton.setOnAction(e -> handleLocationClick(name));

        Label label = new Label(name);
        label.setPrefHeight(60);
        label.setTranslateY(-100); // moved up further
        label.setId("location_name");

        pane.getChildren().addAll(imageButton, label);
        return pane;
    }

    private void handleLocationClick(String locationName) {
        try {
        App.setRoot("scene_parklocs");

        } catch (IOException e) {
            e.printStackTrace(); // You can show an alert instead

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadData();
            populateGrid();
        } catch (SQLException e) {
            e.printStackTrace(); // You can show an alert instead
        }
    }
}
