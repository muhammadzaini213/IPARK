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
        App.setRoot("scene_alltable");
    }

    @FXML
    private void parkingOption() throws IOException {
        App.setRoot("scene_parking");
    }

    @FXML
    private void parklocsOption() throws IOException {
        App.setRoot("scene_parklocs");
    }

    @FXML
    private void parklocsLocationOption() throws IOException {
        App.setRoot("scene_parklocs_location");
    }

    @FXML
    private void tariffsOption() throws IOException {
        App.setRoot("scene_tariff");
    }
    
    @FXML
    private void vehiclesOption() throws IOException {
        App.setRoot("scene_vehicle");
    }
}
