package com.kelompok5.ipark;

import java.io.IOException;

import javafx.fxml.FXML;

public class TariffsController {
    @FXML
    private void backToMain() throws IOException {
        App.setRoot("main_scene");
    }
}
