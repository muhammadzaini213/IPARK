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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.kelompok5.ipark.parking.Parking;
import com.kelompok5.ipark.parking.ParkingModel;
import com.kelompok5.ipark.parking.TariffOption;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ParkingController implements MemoryHelper, Initializable {

    Connector connector = new Connector();
    String tableName = "parkings";
    String[] tableColumns = { "name", "location_tariff", "tariff_id", "availability" };
    Parking parking;

    @FXML
    private TableView<ParkingModel> parkingTable;
    @FXML
    private TableColumn<ParkingModel, String> colName;
    @FXML
    private TableColumn<ParkingModel, Integer> colCarParking, colMotorcycleParking, colBicycleParking;
    @FXML
    private TextField nameField, locationTariffField;
    @FXML
    private ComboBox<TariffOption> tariffComboBox;
    @FXML
    private ComboBox<String> availabilityCombobox;
    @FXML
    private VBox dynamicInputContainer;
    @FXML
    private Button addBtn;

    private Map<String, TextField> vehicleInputFields = new HashMap<>();
    private boolean isEditMode = false;
    private int selectedLocationId = -1;
    private boolean isForm = false;

    @FXML
    private void backToMain() throws IOException {
        App.setRoot("scene_main");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initializeDB();
        setupTableColumns();
        setupRowContextMenu();
        loadData();
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    }

    private void setupRowContextMenu() {
        parkingTable.setRowFactory(tv -> {
            TableRow<ParkingModel> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> {
                ParkingModel selectedParking = parkingTable.getSelectionModel().getSelectedItem();
                if (selectedParking != null && !selectedParking.getName().equals(parking.getName())) {
                    openEditForm(selectedParking);
                }
            });

            MenuItem deleteItem = new MenuItem("Hapus");
            deleteItem.setOnAction(event -> {
                ParkingModel selectedParking = row.getItem();
                if (selectedParking != null) {
                    if (!selectedParking.getName().equals(parking.getName())) {
                        parking.deleteParking(selectedParking.getId());
                    }
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

    private void openEditForm(ParkingModel parkingModel) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("window_add_location.fxml"));
            Parent root = fxmlLoader.load();

            ParkingController controller = fxmlLoader.getController();
            controller.setIsForm(true);
            controller.setEditMode(parkingModel);

            Stage stage = new Stage();
            stage.setTitle("Edit Lokasi");
            stage.setScene(new Scene(root));
            stage.initOwner(parkingTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setOnHidden(event -> loadData());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEditMode(ParkingModel parkingModel) {
        this.isEditMode = true;
        this.selectedLocationId = parkingModel.getId();

        nameField.setText(parkingModel.getName());
        locationTariffField.setText(String.valueOf(parkingModel.getLocation_tariff()));
        availabilityCombobox.setValue(parkingModel.isAvailability() ? "Ya" : "Tidak");

        for (TariffOption option : tariffComboBox.getItems()) {
            if (option.getId() == parkingModel.getTariffId()) {
                tariffComboBox.getSelectionModel().select(option);
                break;
            }
        }

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl)) {
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT v.name, pc.capacity FROM parking_capacity pc " +
                            "JOIN vehicles v ON pc.vehicle_id = v.id " +
                            "WHERE pc.parking_id = " + parkingModel.getId());

            while (rs.next()) {
                String vehicleName = rs.getString("name");
                int capacity = rs.getInt("capacity");
                TextField field = vehicleInputFields.get(vehicleName);
                if (field != null) {
                    field.setText(String.valueOf(capacity));
                }
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initializeDB() {
        try {
            connector.checkTableIfNotExists(tableName, String.join(", ",
                    "name TEXT NOT NULL, location_tariff INTEGER NOT NULL, tariff_id INTEGER NOT NULL, availability BOOLEAN NOT NULL"),
                    "name");
            connector.checkTableIfNotExists("vehicles", "name TEXT NOT NULL,type TEXT NOT NULL", "name");
            connector.checkTableIfNotExists(
                    "parking_capacity",
                    "parking_id INTEGER NOT NULL, " +
                            "vehicle_id INTEGER NOT NULL, " +
                            "capacity INTEGER NOT NULL, " +
                            "FOREIGN KEY(parking_id) REFERENCES parkings(id), " +
                            "FOREIGN KEY(vehicle_id) REFERENCES vehicles(id)",
                    "");

            parking = new Parking("Default", 0, 1, true, tableName, String.join(", ", tableColumns));

            // Manually check if row exists using raw SQL
            String checkSql = "SELECT 1 FROM " + tableName + " WHERE name = ?";
            try (PreparedStatement stmt = connector.connector().prepareStatement(checkSql)) {
                stmt.setString(1, parking.getName());
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) { // row does not exist
                    Object[] values = {
                            parking.getName(),
                            parking.getLocationTariff(),
                            parking.getTariffId(),
                            parking.isAvailable() ? 1 : 0 // convert boolean to integer for SQLite
                    };
                    connector.insertToTable(tableName, String.join(", ", tableColumns), values);
                    System.out.println("Inserted default parking.");
                } else {
                    System.out.println("Parking already exists. No insert.");
                }
            }

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    @Override
    public void saveToDB(ActionEvent event) {
        String locationName = nameField.getText().trim();
        int locationTariff = Integer.parseInt(locationTariffField.getText().trim());
        boolean isAvailable = "Ya".equals(availabilityCombobox.getValue());

        if (locationName.isEmpty()) {
            System.out.println("Nama lokasi tidak boleh kosong");
            return;
        }

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl)) {
            conn.setAutoCommit(false);

            if (isEditMode && selectedLocationId != -1) {
                // UPDATE parking
                String updateParkingSQL = "UPDATE parkings SET name = ?, location_tariff = ?, tariff_id = ?, availability = ? WHERE id = ?";
                PreparedStatement psUpdate = conn.prepareStatement(updateParkingSQL);
                psUpdate.setString(1, locationName);
                psUpdate.setInt(2, locationTariff);

                TariffOption selected = tariffComboBox.getSelectionModel().getSelectedItem();
                psUpdate.setInt(3, selected != null ? selected.getId() : 1);
                psUpdate.setBoolean(4, isAvailable);
                psUpdate.setInt(5, selectedLocationId);

                psUpdate.executeUpdate();

                // Delete existing capacities
                String deleteCapacitySQL = "DELETE FROM parking_capacity WHERE parking_id = ?";
                PreparedStatement psDelete = conn.prepareStatement(deleteCapacitySQL);
                psDelete.setInt(1, selectedLocationId);
                psDelete.executeUpdate();

                // Insert new capacities
                insertParkingCapacities(conn, selectedLocationId);

            } else {
                // INSERT new parking
                String insertParkingSQL = "INSERT INTO parkings (name, location_tariff, tariff_id, availability) VALUES(?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(insertParkingSQL, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, locationName);
                ps.setInt(2, locationTariff);

                TariffOption selected = tariffComboBox.getSelectionModel().getSelectedItem();
                ps.setInt(3, selected != null ? selected.getId() : 1);
                ps.setBoolean(4, isAvailable);

                ps.executeUpdate();

                // Get generated parking ID
                ResultSet generatedKeys = ps.getGeneratedKeys();
                int newParkingId = -1;
                if (generatedKeys.next()) {
                    newParkingId = generatedKeys.getInt(1);
                }

                // Insert capacities
                insertParkingCapacities(conn, newParkingId);
            }

            conn.commit();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertParkingCapacities(Connection conn, int parkingId) throws Exception {
        for (Map.Entry<String, TextField> entry : vehicleInputFields.entrySet()) {
            String vehicleName = entry.getKey();
            String capacityValueStr = entry.getValue().getText().trim();
            int capacityValue = 0;
            try {
                capacityValue = Integer.parseInt(capacityValueStr);
            } catch (NumberFormatException ex) {
                capacityValue = 0;
            }

            PreparedStatement psVehicle = conn.prepareStatement("SELECT id FROM vehicles WHERE name = ?");
            psVehicle.setString(1, vehicleName);
            ResultSet rsVehicle = psVehicle.executeQuery();
            if (rsVehicle.next()) {
                int vehicleId = rsVehicle.getInt("id");

                PreparedStatement psCustom = conn.prepareStatement(
                        "INSERT INTO parking_capacity(vehicle_id, parking_id, capacity) VALUES (?, ?, ?)");
                psCustom.setInt(1, vehicleId);
                psCustom.setInt(2, parkingId);
                psCustom.setInt(3, capacityValue);
                psCustom.executeUpdate();
            }
        }
    }

    @Override
    public void loadData() {
        ObservableList<ParkingModel> data = FXCollections.observableArrayList();
        parkingTable.getColumns().clear();

        List<String> vehicleNames = new ArrayList<>();
        Map<Integer, String> vehicleIdToName = new HashMap<>();
        Map<Integer, String> tariffIdToName = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl)) {
            // Load vehicles
            ResultSet vehicleRS = conn.createStatement().executeQuery("SELECT id, name FROM vehicles ORDER BY id ASC");
            while (vehicleRS.next()) {
                int id = vehicleRS.getInt("id");
                String name = vehicleRS.getString("name");
                vehicleNames.add(name);
                vehicleIdToName.put(id, name);
            }

            // Load tariffs
            ResultSet tariffRS = conn.createStatement().executeQuery("SELECT id, name FROM tariffs");
            while (tariffRS.next()) {
                tariffIdToName.put(tariffRS.getInt("id"), tariffRS.getString("name"));
            }

            Map<Integer, ParkingModel> parkingMap = new LinkedHashMap<>();

            // Load parkings
            ResultSet parkingRS = conn.createStatement()
                    .executeQuery("SELECT id, name, location_tariff, tariff_id, availability FROM parkings");

            while (parkingRS.next()) {
                int id = parkingRS.getInt("id");
                String name = parkingRS.getString("name");
                int location_tariff = parkingRS.getInt("location_tariff");
                int tariff_id = parkingRS.getInt("tariff_id");
                boolean availability = parkingRS.getBoolean("availability");

                String tariffName = tariffIdToName.getOrDefault(tariff_id, "Unknown");

                ParkingModel model = new ParkingModel(id, name, location_tariff, tariff_id, availability);
                model.setTariffName(tariffName);
                model.setAvailability(availability);

                // Init capacities to 0 for each vehicle
                for (String vehicle : vehicleNames) {
                    model.setParkingCapacity(vehicle, 0);
                }

                parkingMap.put(id, model);
            }

            // Load capacities
            ResultSet customRS = conn.createStatement()
                    .executeQuery("SELECT vehicle_id, parking_id, capacity FROM parking_capacity");

            while (customRS.next()) {
                int vehicleId = customRS.getInt("vehicle_id");
                int parkingId = customRS.getInt("parking_id");
                int capacity = customRS.getInt("capacity");

                ParkingModel model = parkingMap.get(parkingId);
                if (model != null) {
                    String vehicleName = vehicleIdToName.get(vehicleId);
                    model.setParkingCapacity(vehicleName, capacity);
                }
            }

            data.addAll(parkingMap.values());

            // Setup table columns
            TableColumn<ParkingModel, String> nameCol = new TableColumn<>("Nama Lokasi");
            nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            parkingTable.getColumns().add(nameCol);

            TableColumn<ParkingModel, Number> locationCapacityCol = new TableColumn<>("Tarif Lokasi");
            locationCapacityCol.setCellValueFactory(cellData -> cellData.getValue().locationTariffProperty());
            parkingTable.getColumns().add(locationCapacityCol);

            TableColumn<ParkingModel, String> capacityCol = new TableColumn<>("Nama Tarif");
            capacityCol.setCellValueFactory(cellData -> cellData.getValue().tariffNameProperty());
            parkingTable.getColumns().add(capacityCol);

            for (String vehicleName : vehicleNames) {
                TableColumn<ParkingModel, Number> dynamicCol = new TableColumn<>("Kapasitas " + vehicleName);
                dynamicCol.setCellValueFactory(cellData -> cellData.getValue().getCustomParkingProperty(vehicleName));
                dynamicCol.setPrefWidth(200);
                parkingTable.getColumns().add(dynamicCol);
            }

            TableColumn<ParkingModel, Boolean> availabilityCol = new TableColumn<>("Status");
            availabilityCol.setCellValueFactory(cellData -> cellData.getValue().availabilityProperty());
            parkingTable.getColumns().add(availabilityCol);

            parkingTable.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIsForm(boolean isForm) {
        this.isForm = isForm;
        if (isForm) {
            loadCapacityInputs();
            initializeTariffComboBox();
            initializeAvailabilityCombobox();
        }
    }

    private void initializeAvailabilityCombobox() {
        availabilityCombobox.getItems().addAll("Ya", "Tidak");
        availabilityCombobox.setValue("Ya"); // Default value
    }

    private void loadCapacityInputs() {
        dynamicInputContainer.getChildren().clear();
        vehicleInputFields.clear();

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT id, name FROM vehicles ORDER BY id ASC");
            while (rs.next()) {
                String vehicleName = rs.getString("name");

                TextField textField = new TextField();
                textField.setPromptText("Masukkan kapasitas untuk " + vehicleName);
                textField.setPrefWidth(300);

                dynamicInputContainer.getChildren().add(textField);
                vehicleInputFields.put(vehicleName, textField);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeTariffComboBox() {
        tariffComboBox.getItems().clear();
        try (ResultSet rs = connector.loadItem("tariffs", new String[] { "id", "name" })) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                tariffComboBox.getItems().add(new TariffOption(id, name));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("window_add_location.fxml"));
        Parent root = fxmlLoader.load();

        ParkingController controller = fxmlLoader.getController();
        controller.setIsForm(true);

        Stage stage = new Stage();
        stage.setTitle("Tambah Lokasi");
        stage.setScene(new Scene(root));
        stage.initOwner(addBtn.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);

        stage.setOnHidden(event -> {
            initializeDB();
            setupTableColumns();
            setupRowContextMenu();
            loadData();
        });

        stage.show();
    }

    @FXML
    public void closeForm(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }
}