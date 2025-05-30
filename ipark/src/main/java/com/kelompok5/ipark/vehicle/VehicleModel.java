package com.kelompok5.ipark.vehicle;

import javafx.beans.property.SimpleStringProperty;

public class VehicleModel {
        private final int id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty type;

        public VehicleModel(int id, String name, String type) {
            this.id = id;
            this.name = new SimpleStringProperty(name);
            this.type = new SimpleStringProperty(type);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name.get();
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public String getType() {
            return type.get();
        }

        public SimpleStringProperty typeProperty() {
            return type;
        }
    }