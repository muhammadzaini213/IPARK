package com.kelompok5.ipark;

import java.io.IOException;
import javafx.fxml.FXML;

public class MainController {
    @FXML
    private void exit() {
        System.exit(0);
    }

    @FXML
    private void allTableOption() throws IOException {
        App.setRoot("alltable_scene");
    }

    @FXML
    private void parkingOption() throws IOException {
        App.setRoot("parking_scene");
    }

    @FXML
    private void parklocsOption() throws IOException {
        App.setRoot("parklocs_scene");
    }

    @FXML
    private void parklocsLocationOption() throws IOException {
        App.setRoot("parklocs_location_scene");
    }

    @FXML
    private void tariffsOption() throws IOException {
        App.setRoot("tariff_scene");
    }
    
    @FXML
    private void vehiclesOption() throws IOException {
        App.setRoot("vehicle_scene");
    }
}
