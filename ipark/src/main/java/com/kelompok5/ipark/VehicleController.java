package com.kelompok5.ipark;

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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import com.kelompok5.ipark.vehicleScene.Bicycle;
import com.kelompok5.ipark.vehicleScene.Car;
import com.kelompok5.ipark.vehicleScene.CustomVehicle;
import com.kelompok5.ipark.vehicleScene.MotorCycle;
import com.kelompok5.ipark.vehicleScene.Vehicle;
import com.kelompok5.ipark.vehicleScene.VehicleModel;

public class VehicleController implements MemoryHelper, Initializable {

    private Car car;
    private MotorCycle motorCycle;
    private Bicycle bicycle;

    @FXML
    private TextField nameField, typeField;

    @FXML
    private TableView vehiclesTable;

    @FXML
    private Button addBtn;

    @FXML
    private void backToMain() throws IOException {
        App.setRoot("main_scene");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDB();
        setupTableColumns();
        setupRowContextMenu();
        loadData();
    }

    @Override
    public void initializeDB() {
        try (Connection connection = DriverManager.getConnection(Statics.jdbcUrl)) {

            String pragmaQuery = "PRAGMA table_info(vehicles)";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(pragmaQuery);

            car = new Car("Mobil sandar", "Mobil");
            motorCycle = new MotorCycle("Motor Standar", "Motor");
            bicycle = new Bicycle("Sepeda Standar", "Sepeda");
            Vehicle[] vehicle = { car, motorCycle, bicycle };

            boolean hasName = false;
            boolean hasType = false;
            boolean hasAllItem = false;

            for (Vehicle v : vehicle) {
                String sql = "SELECT 1 FROM vehicles WHERE name = ? AND type = ? LIMIT 1";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, v.getName());
                ps.setString(2, v.getType());
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    hasAllItem = false;
                    break;
                } else {
                    hasAllItem = true;
                }

                rs.close();
                ps.close();
            }

            while (resultSet.next()) {
                String columnName = resultSet.getString("name");
                if ("name".equalsIgnoreCase(columnName))
                    hasName = true;
                if ("type".equalsIgnoreCase(columnName))
                    hasType = true;
            }

            resultSet.close();

            if (!hasName || !hasType || !hasAllItem) {
                statement.executeUpdate("DROP TABLE IF EXISTS vehicles");
                statement.executeUpdate(
                        "CREATE TABLE vehicles ( id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,type TEXT NOT NULL, UNIQUE(name))");
                statement.executeUpdate(
                        "INSERT INTO vehicles (name, type) VALUES ('" + car.getName() + "', '" + car.getType() + "')");
                statement.executeUpdate("INSERT INTO vehicles (name, type) VALUES ('" + motorCycle.getName() + "', '"
                        + motorCycle.getType() + "')");
                statement.executeUpdate("INSERT INTO vehicles (name, type) VALUES ('" + bicycle.getName() + "', '"
                        + bicycle.getType() + "')");
            }

            ResultSet vehicles = statement.executeQuery("SELECT * FROM vehicles");
            while (vehicles.next()) {
                String name = vehicles.getString("name");
                String type = vehicles.getString("type");
                System.out.println("Name: " + name + ", Type: " + type);
            }

            vehicles.close();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add_vehicle.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Tambah Kendaraan");
        stage.setScene(new Scene(root));
        stage.initOwner(((Node) (addBtn)).getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);

        // Add listener to refresh data after the form closes
        stage.setOnHidden(event -> {
            initializeDB();
            setupTableColumns();
            setupRowContextMenu(); // Add this
            loadData();
        });

        stage.show();
    }

    private void openEditForm(VehicleModel vehicle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit_vehicle.fxml"));
            Parent root = loader.load();

            // Get the controller for the edit form
            VehicleController controller = loader.getController();

            // Inject vehicle data into text fields in edit form
            controller.setVehicleData(vehicle);

            Stage stage = new Stage();
            stage.setTitle("Edit Vehicle");
            stage.setScene(new Scene(root));
            stage.initOwner(vehicleTable.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            // Refresh table after closing edit window
            stage.setOnHidden(event -> loadData());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadData() {
        ObservableList<VehicleModel> data = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, name, type FROM vehicles")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                data.add(new VehicleModel(id, name, type));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        vehicleTable.setItems(data);
    }

    @Override
    public void showData() {
        throw new UnsupportedOperationException("Unimplemented method 'showData'");
    }

    public void setVehicleData(VehicleModel vehicle) {
        nameField.setText(vehicle.getName());
        typeField.setText(vehicle.getType());
        editingVehicleId = vehicle.getId();
        isEditMode = true;
    }

    @FXML
    public void closeForm(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    @Override
    public void saveToDB(ActionEvent event) {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();

        if (name.isEmpty() || type.isEmpty()) {
        }

        if (type.equals(car.getType())) {
            try {
                if (isEditMode) {
                    car.editVehicle(editingVehicleId, name, type);
                } else {
                    car.addVehicle(name);
                }
            } catch (Exception e) {
            }
        }

        else if (type.equals(motorCycle.getType())) {
            try {
                if (isEditMode) {
                    motorCycle.editVehicle(editingVehicleId, name, type);
                } else {
                    motorCycle.addVehicle(name);
                }
            } catch (Exception e) {
            }
        }

        else if (type.equals(bicycle.getType())) {
            try {
                if (isEditMode) {
                    bicycle.editVehicle(editingVehicleId, name, type);
                } else {
                    bicycle.addVehicle(name);
                }
            } catch (Exception e) {
            }
        }

        else {
            try {
                if (isEditMode) {
                    new CustomVehicle().editVehicle(editingVehicleId, name, type);
                } else {
                    new CustomVehicle().addVehicle(name, type);
                }
            } catch (Exception e) {
            }
        }

        closeForm(event);
    }

    @FXML
    private TableView<VehicleModel> vehicleTable;

    @FXML
    private TableColumn<VehicleModel, String> colName;

    @FXML
    private TableColumn<VehicleModel, String> colType;

    private void setupTableColumns() {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    }

    private boolean isEditMode = false;
    private int editingVehicleId = -1;

    private void setupRowContextMenu() {
        vehicleTable.setRowFactory(tv -> {
            TableRow<VehicleModel> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> {
                VehicleModel selectedVehicle = row.getItem();
                if (selectedVehicle != null) {
                    if (!selectedVehicle.getName().equals(car.getName()) &&
                            !selectedVehicle.getName().equals(motorCycle.getName()) &&
                            !selectedVehicle.getName().equals(bicycle.getName())) {
                        openEditForm(selectedVehicle);
                    }

                }
            });

            MenuItem deleteItem = new MenuItem("Hapus");
            deleteItem.setOnAction(event -> {
                VehicleModel selectedVehicle = row.getItem();
                if (selectedVehicle != null) {
                    if (selectedVehicle.getType().equals(car.getType())) {
                        car.deleteVehicle(selectedVehicle);
                    } else if (selectedVehicle.getType().equals(motorCycle.getType())) {
                        motorCycle.deleteVehicle(selectedVehicle);
                    } else if (selectedVehicle.getType().equals(bicycle.getType())) {
                        bicycle.deleteVehicle(selectedVehicle);
                    } else {
                        new CustomVehicle().deleteVehicle(selectedVehicle);
                    }
                }
            });

            contextMenu.getItems().addAll(editItem, deleteItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu));

            return row;
        });
    }
}