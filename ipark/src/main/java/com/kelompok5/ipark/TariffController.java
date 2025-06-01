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
import com.kelompok5.ipark.utils.Connector;
import com.kelompok5.ipark.utils.MemoryHelper;
import com.kelompok5.ipark.utils.Statics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TariffController implements MemoryHelper, Initializable {

    Connector connector = new Connector();
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
        try {
            connector.checkTableIfNotExists(tableName,
                    "name TEXT NOT NULL, car_tariff INTEGER NOT NULL, motorcycle_tariff INTEGER NOT NULL, bicycle_tariff INTEGER NOT NULL",
                    "name");
            connector.checkTableIfNotExists("vehicles", "name TEXT NOT NULL,type TEXT NOT NULL", "name");
            connector.checkTableIfNotExists(
                    "custom_tariff",
                    "vehicle_id INTEGER NOT NULL, tariff_id INTEGER NOT NULL, tariff INTEGER NOT NULL" +
                            "FOREIGN KEY(vehicle_id) REFERENCES vehicles(id), " +
                            "FOREIGN KEY(tariff_id) REFERENCES tariffs(id)",
                    "");

            tariff = new Tariff("Gratis", 0, 0, 0, tableName, String.join(", ", tableColumns));

            Object[][] rows = {
                    { tariff.getName(), tariff.getCarTariff(), tariff.getMotorCycleTariff(), tariff.getBicycleTariff() }
            };
            if (!connector.areRowsPresent(tableName, tableColumns, rows)) {
                connector.dropThenCreateTable(tableName,
                        "name TEXT NOT NULL, car_tariff INTEGER NOT NULL, motorcycle_tariff INTEGER NOT NULL, bicycle_tariff INTEGER NOT NULL",
                        "name");

                String structure = "name, car_tariff, motorcycle_tariff, bicycle_tariff";

                Object[] values = { tariff.getName(), tariff.getCarTariff(), tariff.getMotorCycleTariff(),
                        tariff.getBicycleTariff() };
                connector.insertToTable(tableName, structure, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    }

    boolean isEditMode = false;
    int selectedTariffId = -1;
    @FXML
    private TextField nameField, carTariffField, motorcycleTariffField, bicycleTariffField;

    public void setVehicleData(TariffModel tariffModel) {
        nameField.setText(tariffModel.getName());
        selectedTariffId = tariffModel.getId();
        isEditMode = true;
    }

    private void openEditForm(TariffModel tariffModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("window_edit_tariff.fxml"));
            Parent root = loader.load();

            TariffController controller = loader.getController();
            controller.setVehicleData(tariffModel);

            Stage stage = new Stage();
            stage.setTitle("Edit Tariff");
            stage.setScene(new Scene(root));
            stage.initOwner(tariffTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnHidden(event -> loadData());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void closeForm(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
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
                        openEditForm(selectedTariff);
                    }

                }
            });

            MenuItem deleteItem = new MenuItem("Hapus");
            deleteItem.setOnAction(event -> {
                TariffModel selectedTariff = row.getItem();
                if (selectedTariff != null) {
                    tariff.deleteTariff(selectedTariff.getId());
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
        tariffTable.getColumns().clear();

        List<String> vehicleNames = new ArrayList<>();
        Map<Integer, String> vehicleIdToName = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl)) {

            // === 1. Load vehicle names for dynamic columns ===
            ResultSet vehicleRS = conn.createStatement().executeQuery("SELECT id, name FROM vehicles");
            while (vehicleRS.next()) {
                int id = vehicleRS.getInt("id");
                String name = vehicleRS.getString("name");
                vehicleNames.add(name);
                vehicleIdToName.put(id, name);
            }

            // === 2. Load all tariffs ===
            Map<Integer, TariffModel> tariffMap = new LinkedHashMap<>();

            ResultSet tariffRS = conn.createStatement()
                    .executeQuery("SELECT id, name FROM tariffs");

            while (tariffRS.next()) {
                int id = tariffRS.getInt("id");
                String name = tariffRS.getString("name");
                TariffModel model = new TariffModel(id, name);

                // Init all vehicle tariffs to 0
                for (String vehicle : vehicleNames) {
                    model.setCustomTariff(vehicle, 0);
                }

                tariffMap.put(id, model);
            }

            // === 3. Load custom_tariff values ===
            ResultSet customRS = conn.createStatement()
                    .executeQuery("SELECT vehicle_id, tariff_id, tariff FROM custom_tariff");

            while (customRS.next()) {
                int vehicleId = customRS.getInt("vehicle_id");
                int tariffId = customRS.getInt("tariff_id");
                int tariff = customRS.getInt("tariff");

                TariffModel model = tariffMap.get(tariffId);
                if (model != null) {
                    String vehicleName = vehicleIdToName.get(vehicleId);
                    model.setCustomTariff(vehicleName, tariff);
                }
            }

            // Add all to observable list
            data.addAll(tariffMap.values());

            // === 4. Setup dynamic columns ===

            // Static name column
            TableColumn<TariffModel, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            tariffTable.getColumns().add(nameCol);

            // Dynamic columns
            for (String vehicleName : vehicleNames) {
                TableColumn<TariffModel, Number> dynamicCol = new TableColumn<>("Tariff " + vehicleName);
                dynamicCol.setCellValueFactory(cellData -> cellData.getValue().getCustomTariffProperty(vehicleName));
                tariffTable.getColumns().add(dynamicCol);
            }

            tariffTable.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
