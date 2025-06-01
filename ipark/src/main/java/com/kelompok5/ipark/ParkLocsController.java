package com.kelompok5.ipark;

import java.io.IOException;

import javafx.fxml.FXML;

public class ParkLocsController {
    @FXML
    private void backToMain() throws IOException {
        App.setRoot("scene_parklocs_location");
    }
}
