package com.kelompok5.ipark.parking;

import com.kelompok5.ipark.utils.Connector;

public class Parking {
    Connector connector = new Connector();
    String name;
    String tableName, structure;
    int location_tariff, tariff_id;
    boolean availability;    

    public Parking(String name, int location_tariff, int tariff_id, boolean availability, String tableName, String structure) {
        this.name = name;
        this.location_tariff = location_tariff;
        this.tariff_id = tariff_id;
        this.availability = availability;
        this.tableName = tableName; 
        this.structure = structure;
    }

    public String getName() {
        return name;
    }

    public int getLocationTariff() {
        return location_tariff;
    }

    public int getTariffId() {
        return tariff_id;
    }

    public boolean isAvailable() {
        return availability;
    }

    public void addParking(String name, int location_tariff, int tariff_id, boolean availability) {
        Object[] values = { name, location_tariff, tariff_id, availability };
        connector.insertToTable(tableName, structure, values);
    }

    public void editParking(int id, String name, int location_tariff, int tariff_id, boolean availability) {
        Object[] values = { name, location_tariff, tariff_id, availability };
        connector.updateItem(tableName, structure.split(", "), values, id);
    }

    public void deleteParking(int id) {
        connector.deleteItem(tableName, id);
    }

    
}

