package com.kelompok5.ipark.tariff;

import com.kelompok5.ipark.utils.Connector;

public class Tariff {
    Connector connector = new Connector();
    String name;
    int car_tariff, motorCycle_tariff, bicycleTariff;
    String tableName, structure;
    
    public Tariff(String name, int car_tariff, int motorCycle_tariff, int bicycleTariff, String tableName, String structure) {
        this.name = name;
        this.car_tariff = car_tariff;
        this.motorCycle_tariff = motorCycle_tariff;
        this.bicycleTariff = bicycleTariff;
        this.tableName = tableName;
        this.structure = structure;
    }

    public String getName() {
        return name;
    }

    public int getCarTariff() {
        return car_tariff;
    }

    public int getMotorCycleTariff() {
        return motorCycle_tariff;
    }

    public int getBicycleTariff() {
        return bicycleTariff;
    }

    public void addTariff(String name, int car_tariff, int motorCycle_tariff, int bicycleTariff) {
        connector.insertToTable(tableName, structure, new Object[] { name, car_tariff, motorCycle_tariff, bicycleTariff });
    }

    public void editTariff(int id, String name, int car_tariff, int motorCycle_tariff, int bicycleTariff) {
        Object[] values = { name, car_tariff, motorCycle_tariff, bicycleTariff };
        connector.updateItem(tableName, structure.split(", "), values, id);
    }

    public void deleteTariff(int id) {
        connector.deleteItem(tableName, id);
    }
}
