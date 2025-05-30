package com.kelompok5.ipark;

import java.io.IOException;

import com.kelompok5.ipark.utils.MemoryHelper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class TariffController implements MemoryHelper{
    @FXML
    private void backToMain() throws IOException {
        App.setRoot("main_scene");
    }

    @Override
    public void initializeDB() {
        throw new UnsupportedOperationException("Unimplemented method 'initializeDB'");
    }

    @Override
    public void saveToDB(ActionEvent event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveToDB'");
    }

    @Override
    public void loadData() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadData'");
    }
}
