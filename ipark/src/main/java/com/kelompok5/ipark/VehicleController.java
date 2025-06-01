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
import java.sql.ResultSet;
import java.util.ResourceBundle;

import com.kelompok5.ipark.utils.Connector;
import com.kelompok5.ipark.utils.MemoryHelper;
import com.kelompok5.ipark.vehicle.Bicycle;
import com.kelompok5.ipark.vehicle.Car;
import com.kelompok5.ipark.vehicle.CustomVehicle;
import com.kelompok5.ipark.vehicle.MotorCycle;
import com.kelompok5.ipark.vehicle.Vehicle;
import com.kelompok5.ipark.vehicle.VehicleModel;

public class VehicleController implements MemoryHelper, Initializable {

    String tableName = "vehicles";
    String[] tableColumns = { "name", "type" };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDB();
        setupTableColumns();
        setupRowContextMenu();
        loadData();
    }

    private Car car;
    private MotorCycle motorCycle;
    private Bicycle bicycle;

    @FXML
    private TableView vehiclesTable;

    @FXML
    private TableView<VehicleModel> vehicleTable;

    @FXML
    private TableColumn<VehicleModel, String> colName;

    @FXML
    private TableColumn<VehicleModel, String> colType;

    @FXML
    private Button addBtn;

    Connector connector = new Connector();

    @Override
    public void initializeDB() {
        try {
            connector.checkTableIfNotExists(tableName, "name TEXT NOT NULL,type TEXT NOT NULL", "name");

            car = new Car("Mobil Standar", "Mobil", tableName, String.join(", ", tableColumns));
            motorCycle = new MotorCycle("Motor Standar", "Motor", tableName, String.join(", ", tableColumns));
            bicycle = new Bicycle("Sepeda Standar", "Sepeda", tableName, String.join(", ", tableColumns));
            Vehicle[] vehicles = {car, motorCycle, bicycle};

            String[][] rows = {
                    { car.getName(), car.getType() },
                    { motorCycle.getName(), motorCycle.getType() },
                    { bicycle.getName(), bicycle.getType() }
            };

            if (!connector.areRowsPresent(tableName, tableColumns, rows)) {
                connector.dropThenCreateTable(tableName, "name TEXT NOT NULL, type TEXT NOT NULL", "name");

                String structure = "name, type";

                for (Vehicle v : vehicles) {
                    String[] values = { v.getName(), v.getType() };
                    connector.insertToTable(tableName, structure, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    }

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
                        car.deleteVehicle(selectedVehicleId);
                    } else if (selectedVehicle.getType().equals(motorCycle.getType())) {
                        motorCycle.deleteVehicle(selectedVehicleId);
                    } else if (selectedVehicle.getType().equals(bicycle.getType())) {
                        bicycle.deleteVehicle(selectedVehicleId);
                    } else {
                        new CustomVehicle().deleteVehicle(selectedVehicle);
                    }
                    loadData();
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

    @Override
    public void loadData() {
        ObservableList<VehicleModel> data = FXCollections.observableArrayList();

        try (ResultSet rs = connector.loadItem(tableName, new String[] { "id", "name", "type" })) {

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

    private boolean isEditMode = false;
    private int selectedVehicleId = -1;

    public void setVehicleData(VehicleModel vehicle) {
        nameField.setText(vehicle.getName());
        typeField.setText(vehicle.getType());
        selectedVehicleId = vehicle.getId();
        isEditMode = true;
    }

    @FXML
    private TextField nameField, typeField;

    @FXML
    public void addForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("window_add_vehicle.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("Tambah Kendaraan");
        stage.setScene(new Scene(root));
        stage.initOwner(((Node) (addBtn)).getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);

        stage.setOnHidden(event -> {
            initializeDB();
            setupTableColumns();
            setupRowContextMenu();
            loadData();
        });

        stage.show();
    }

    private void openEditForm(VehicleModel vehicle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("window_edit_vehicle.fxml"));
            Parent root = loader.load();

            VehicleController controller = loader.getController();
            controller.setVehicleData(vehicle);

            Stage stage = new Stage();
            stage.setTitle("Edit Kendaraan");
            stage.setScene(new Scene(root));
            stage.initOwner(vehicleTable.getScene().getWindow());
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
                    car.editVehicle(selectedVehicleId, new String[] { name, type });
                } else {
                    car.addVehicle(name);
                }
            } catch (Exception e) {
            }
        }

        else if (type.equals(motorCycle.getType())) {
            try {
                if (isEditMode) {
                    motorCycle.editVehicle(selectedVehicleId, new String[] { name, type });
                } else {
                    motorCycle.addVehicle(name);
                }
            } catch (Exception e) {
            }
        }

        else if (type.equals(bicycle.getType())) {
            try {
                if (isEditMode) {
                    bicycle.editVehicle(selectedVehicleId, new String[] { name, type });
                } else {
                    bicycle.addVehicle(name);
                }
            } catch (Exception e) {
            }
        }

        else {
            try {
                if (isEditMode) {
                    new CustomVehicle().editVehicle(selectedVehicleId, name, type);
                } else {
                    new CustomVehicle().addVehicle(name, type);
                }
            } catch (Exception e) {
            }
        }

        closeForm(event);
    }

    @FXML
    private void backToMain() throws IOException {
        App.setRoot("scene_main");
    }
}