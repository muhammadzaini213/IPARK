package com.kelompok5.ipark;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.*;
import java.lang.reflect.Type;

import com.google.gson.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONObject;

import com.kelompok5.ipark.tariff.Tariff;
import com.kelompok5.ipark.tariff.TariffModel;
import com.kelompok5.ipark.utils.MemoryHelper;
import com.kelompok5.ipark.utils.Statics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class TariffController implements MemoryHelper, Initializable {

    String tableName = "tariffs";
    String[] tableColumns = { "name", "car_tariff", "motorcycle_tariff", "bicycle_tariff" };
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initializeDB();
        setupTableColumns();
        setupRowContextMenu();
        loadData();
    }

    Tariff tariff;

    @FXML
    private TableView tariffsTable;

    @FXML
    private TableView<TariffModel> tariffTable;

    @FXML
    private TableColumn<TariffModel, String> colName;

    @FXML
    private TableColumn<TariffModel, Integer> colCarTariff, colMotorcycleTariff, colBicycleTariff;


    @Override
    public void initializeDB() {
        
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colCarTariff.setCellValueFactory(cellData -> cellData.getValue().carTariffProperty().asObject());
        colMotorcycleTariff.setCellValueFactory(cellData -> cellData.getValue().motorCycleTariffProperty().asObject());
        colBicycleTariff.setCellValueFactory(cellData -> cellData.getValue().bicycleTariffProperty().asObject());
    }

    private void setupRowContextMenu() {
        tariffTable.setRowFactory(tv -> {
            TableRow<TariffModel> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> {
                TariffModel selectedTariff = row.getItem();
                if (selectedTariff != null) {
                    if (!selectedTariff.getName().equals(tariff.getName())) {
                        // openEditForm(selectedTariff);
                    }

                }
            });

            MenuItem deleteItem = new MenuItem("Hapus");
            deleteItem.setOnAction(event -> {
                TariffModel selectedTariff = row.getItem();
                if (selectedTariff != null) {
                    tariff.deleteTariff(selectedTariff);
                }
                loadData();
            });

            contextMenu.getItems().addAll(editItem, deleteItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu));

            return row;
        });
    }

    @Override
    public void loadData() {
        ObservableList<TariffModel> data = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT id, name, car_tariff, motorcycle_tariff, bicycle_tariff FROM tariffs")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int car_tariff = rs.getInt("car_tariff");
                int motorcycle_tariff = rs.getInt("motorcycle_tariff");
                int bicycle_tariff = rs.getInt("bicycle_tariff");

                data.add(new TariffModel(id, name, car_tariff, motorcycle_tariff, bicycle_tariff));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tariffTable.setItems(data);
    }

    @Override
    public void saveToDB(ActionEvent event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveToDB'");
    }

    @FXML
    private void backToMain() throws IOException {
        App.setRoot("scene_main");
    }
}
