package com.kelompok5.ipark.vehicle;

import com.kelompok5.ipark.utils.Connector;

public class MotorCycle extends Vehicle {
    Connector connector = new Connector();

    private String name, type, tableName;
    private String structure;

    public MotorCycle(String name, String type, String tableName, String structure) {
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
        // Implementation for parking a motorcycle
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
