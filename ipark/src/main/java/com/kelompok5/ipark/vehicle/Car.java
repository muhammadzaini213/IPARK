package com.kelompok5.ipark.vehicle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.kelompok5.ipark.utils.Connector;
import com.kelompok5.ipark.utils.Statics;

public class Car extends Vehicle {
    Connector connector = new Connector();

    private String name, type, tableName;
    private String structure;

    public Car(String name, String type, String tableName, String structure) {
        this.name = name;
        this.type = type;
        this.tableName = tableName;
        this.structure = structure;
    }

    @Override
    public void addVehicle(String name) {
        connector.insertToTable(tableName, structure, new String[] { name, getType() });
    }

    @Override
    public void editVehicle(int id, String[] values) {
        connector.updateItem(tableName, structure.split(", "), values, id);
    }

    @Override
    public void deleteVehicle(int id) {
        connector.deleteItem(tableName, id);
    }

    @Override
    public void parkTo(int id) {
        // Implementation for parking a car
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }
}
