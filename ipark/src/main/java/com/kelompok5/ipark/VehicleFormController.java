package com.kelompok5.ipark;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class VehicleFormController {

    @FXML
    public void closeForm(ActionEvent event) {
        // This gets the Stage from any node in the scene, like a button
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
}
