package com.kelompok5.ipark.tariff;

import com.kelompok5.ipark.utils.Connector;

public class Tariff {
    Connector connector = new Connector();
    String name;
    String tableName, structure;
    
    public Tariff(String name, String tableName, String structure) {
        this.name = name;
        this.tableName = tableName;
        this.structure = structure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTariff(String name, int car_tariff, int motorCycle_tariff, int bicycleTariff) {
        connector.insertToTable(tableName, structure, new Object[] { name });
    }

    public void editTariff(int id, String name, int car_tariff, int motorCycle_tariff, int bicycleTariff) {
        Object[] values = { name };
        connector.updateItem(tableName, structure.split(", "), values, id);
    }

    public void deleteTariff(int id) {
        connector.deleteItem(tableName, id);
    }
}
