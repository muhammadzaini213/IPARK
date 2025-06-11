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
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.com.kelompok5.ipark.utils.Toast;

public class TariffController implements MemoryHelper, Initializable {

    Connector connector = new Connector();
    String tableName = "tariffs";
    String[] tableColumns = { "name" };

    boolean isForm = false;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initializeDB();
        setupTableColumns();
        setupRowContextMenu();
        loadData();
    }

    public void setIsForm(boolean isForm) {
        this.isForm = isForm;
        if (isForm) {
            loadVehicleInputs();
        }
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
                    "name TEXT NOT NULL",
                    "name");
            connector.checkTableIfNotExists("vehicles", "name TEXT NOT NULL,type TEXT NOT NULL", "name");
            connector.checkTableIfNotExists(
                    "custom_tariff",
                    "vehicle_id INTEGER NOT NULL, " +
                            "tariff_id INTEGER NOT NULL, " +
                            "tariff INTEGER NOT NULL, " + // <== comma added
                            "FOREIGN KEY(vehicle_id) REFERENCES vehicles(id), " +
                            "FOREIGN KEY(tariff_id) REFERENCES tariffs(id)",
                    "" // assuming no UNIQUE constraint, or replace with column name
            );

            tariff = new Tariff("Gratis", tableName, String.join(", ", tableColumns));

            Object[][] rows = {
                    { tariff.getName() }
            };
            if (!connector.areRowsPresent(tableName, tableColumns, rows)) {
                connector.dropThenCreateTable(tableName,
                        "name TEXT NOT NULL",
                        "name");

                String structure = "name";

                Object[] values = { tariff.getName() };
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

    public void setTariffData(TariffModel tariffModel) {
        nameField.setText(tariffModel.getName());
        selectedTariffId = tariffModel.getId();
        isEditMode = true;

        for (Map.Entry<String, TextField> entry : vehicleInputFields.entrySet()) {
            String vehicleName = entry.getKey();
            TextField textField = entry.getValue();

            Integer tariffValue = tariffModel.getCustomTariff(vehicleName);
            if (tariffValue != null) {
                textField.setText(String.valueOf(tariffValue));
            } else {
                textField.setText("0");
            }
        }
    }

    private void openEditForm(TariffModel tariffModel) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("window_edit_tariff.fxml"));
            Parent root = fxmlLoader.load();

            TariffController controller = fxmlLoader.getController();
            controller.setIsForm(true);
            controller.setTariffData(tariffModel);

            Stage stage = new Stage();
            stage.setTitle("Edit Tariff");
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
    private Button addBtn;

    @FXML
    public void addForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("window_add_tariff.fxml"));
        Parent root = fxmlLoader.load();

        TariffController controller = fxmlLoader.getController();
        controller.setIsForm(true);

        Stage stage = new Stage();
        stage.setTitle("Tambah Tariff");
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

    private void setupRowContextMenu() {
        tariffTable.setRowFactory(tv -> {
            TableRow<TariffModel> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> {
                TariffModel selectedTariff = tariffTable.getSelectionModel().getSelectedItem();
                if (selectedTariff != null && !selectedTariff.getName().equals(tariff.getName())) {
                    openEditForm(selectedTariff);
                }
            });

            MenuItem deleteItem = new MenuItem("Hapus");
            deleteItem.setOnAction(event -> {
                TariffModel selectedTariff = row.getItem();
                if (selectedTariff != null) {
                    if (!selectedTariff.getName().equals(tariff.getName())) {
                        tariff.deleteTariff(selectedTariff.getId());
                        loadData();
                        return;
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

    @Override
    public void loadData() {
        ObservableList<TariffModel> data = FXCollections.observableArrayList();
        tariffTable.getColumns().clear();

        List<String> vehicleNames = new ArrayList<>();
        Map<Integer, String> vehicleIdToName = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl)) {

            // === 1. Load vehicle names for dynamic columns ===
            ResultSet vehicleRS = conn.createStatement().executeQuery("SELECT id, name FROM vehicles ORDER BY id ASC");
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

            data.addAll(tariffMap.values());

            TableColumn<TariffModel, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            tariffTable.getColumns().add(nameCol);

            // Dynamic columns
            for (String vehicleName : vehicleNames) {
                TableColumn<TariffModel, Number> dynamicCol = new TableColumn<>("Tariff " + vehicleName);
                dynamicCol.setCellValueFactory(cellData -> cellData.getValue().getCustomTariffProperty(vehicleName));
                dynamicCol.setPrefWidth(200); // <== Tambahkan ini
                tariffTable.getColumns().add(dynamicCol);
            }

            tariffTable.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveToDB(ActionEvent event) {
        String tariffName = nameField.getText().trim();

        if (tariffName.isEmpty()) {
            Toast.showToast((Stage) nameField.getScene().getWindow(), "Nama tarif tidak boleh kosong!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl)) {
            conn.setAutoCommit(false);

            if (isEditMode && selectedTariffId != -1) {
                // UPDATE tarif name
                String updateTariffSQL = "UPDATE tariffs SET name = ? WHERE id = ?";
                PreparedStatement psUpdate = conn.prepareStatement(updateTariffSQL);
                psUpdate.setString(1, tariffName);
                psUpdate.setInt(2, selectedTariffId);
                psUpdate.executeUpdate();

                // Hapus semua custom tariff untuk tarif ini
                String deleteCustomSQL = "DELETE FROM custom_tariff WHERE tariff_id = ?";
                PreparedStatement psDelete = conn.prepareStatement(deleteCustomSQL);
                psDelete.setInt(1, selectedTariffId);
                psDelete.executeUpdate();

                // Insert ulang custom tariffs
                for (Map.Entry<String, TextField> entry : vehicleInputFields.entrySet()) {
                    String vehicleName = entry.getKey();
                    String tariffValueStr = entry.getValue().getText().trim();
                    int tariffValue = 0;
                    try {
                        tariffValue = Integer.parseInt(tariffValueStr);
                        if (tariffValue < 0) {
                            Toast.showToast((Stage) nameField.getScene().getWindow(), "Tarif tidak boleh negatif!");
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        tariffValue = 0;
                        Toast.showToast((Stage) nameField.getScene().getWindow(), "Tarif harus berupa angka!");
                        return;
                    }

                    PreparedStatement psVehicle = conn.prepareStatement("SELECT id FROM vehicles WHERE name = ?");
                    psVehicle.setString(1, vehicleName);
                    ResultSet rsVehicle = psVehicle.executeQuery();
                    if (rsVehicle.next()) {
                        int vehicleId = rsVehicle.getInt("id");

                        PreparedStatement psCustom = conn.prepareStatement(
                                "INSERT INTO custom_tariff(vehicle_id, tariff_id, tariff) VALUES (?, ?, ?)");
                        psCustom.setInt(1, vehicleId);
                        psCustom.setInt(2, selectedTariffId);
                        psCustom.setInt(3, tariffValue);
                        psCustom.executeUpdate();
                    }
                }
            } else {
                // Insert baru (kode lama)
                String insertTariffSQL = "INSERT INTO tariffs(name) VALUES(?)";
                PreparedStatement ps = conn.prepareStatement(insertTariffSQL, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, tariffName);
                ps.executeUpdate();

                ResultSet generatedKeys = ps.getGeneratedKeys();
                int newTariffId = -1;
                if (generatedKeys.next()) {
                    newTariffId = generatedKeys.getInt(1);
                } else {
                    throw new RuntimeException("Gagal mendapatkan ID tarif baru");
                }

                for (Map.Entry<String, TextField> entry : vehicleInputFields.entrySet()) {
                    String vehicleName = entry.getKey();
                    String tariffValueStr = entry.getValue().getText().trim();
                    int tariffValue = 0;
                    try {
                        tariffValue = Integer.parseInt(tariffValueStr);
                        if (tariffValue < 0) {
                            Toast.showToast((Stage) nameField.getScene().getWindow(), "Tarif tidak boleh negatif!");
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        tariffValue = 0;
                        Toast.showToast((Stage) nameField.getScene().getWindow(), "Tarif harus berupa angka!");
                        return;
                    }

                    PreparedStatement psVehicle = conn.prepareStatement("SELECT id FROM vehicles WHERE name = ?");
                    psVehicle.setString(1, vehicleName);
                    ResultSet rsVehicle = psVehicle.executeQuery();
                    if (rsVehicle.next()) {
                        int vehicleId = rsVehicle.getInt("id");

                        PreparedStatement psCustom = conn.prepareStatement(
                                "INSERT INTO custom_tariff(vehicle_id, tariff_id, tariff) VALUES (?, ?, ?)");
                        psCustom.setInt(1, vehicleId);
                        psCustom.setInt(2, newTariffId);
                        psCustom.setInt(3, tariffValue);
                        psCustom.executeUpdate();
                    }
                }
            }

            conn.commit();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToMain() throws IOException {
        App.setRoot("scene_main");
    }

    @FXML
    private VBox dynamicInputContainer;

    private Map<String, TextField> vehicleInputFields = new HashMap<>();

    private void loadVehicleInputs() {
        dynamicInputContainer.getChildren().clear();
        vehicleInputFields.clear();

        try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT id, name FROM vehicles ORDER BY id ASC");
            while (rs.next()) {
                String vehicleName = rs.getString("name");

                TextField textField = new TextField();
                textField.setPromptText("Masukkan tarif untuk " + vehicleName);
                textField.setPrefWidth(300);

                dynamicInputContainer.getChildren().addAll(textField);
                vehicleInputFields.put(vehicleName, textField);
                System.out.println("Memuat input kendaraan...");
                System.out.println("Jumlah kendaraan: " + vehicleInputFields.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
