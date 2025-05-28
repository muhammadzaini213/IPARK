module com.kelompok5.ipark {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.kelompok5.ipark to javafx.fxml;
    exports com.kelompok5.ipark;
}
