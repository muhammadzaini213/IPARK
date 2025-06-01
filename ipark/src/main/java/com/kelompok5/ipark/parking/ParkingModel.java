package com.kelompok5.ipark.parking;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ParkingModel {
    private final int id;
    private final StringProperty name;
    private final IntegerProperty location_tariff;
    private final IntegerProperty tariff_id;
    private final BooleanProperty availability;
    private final StringProperty tariffName;

    private final Map<String, Integer> parkingCapacities = new HashMap<>();
    private final Map<String, Integer> parkingTariffs = new HashMap<>();

    public ParkingModel(int id, String name, int location_tariff, int tariff_id, boolean availability) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.location_tariff = new SimpleIntegerProperty(location_tariff);
        this.tariff_id = new SimpleIntegerProperty(tariff_id);
        this.availability = new SimpleBooleanProperty(availability);
        this.tariffName = new SimpleStringProperty("");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getLocation_tariff() {
        return location_tariff.get();
    }

    public IntegerProperty locationTariffProperty() {
        return location_tariff;
    }

    public int getTariffId() {
        return tariff_id.get();
    }

    public IntegerProperty tariffIdProperty() {
        return tariff_id;
    }

    public String getTariffName() {
        return tariffName.get();
    }

    public void setTariffName(String value) {
        tariffName.set(value);
    }

    public StringProperty tariffNameProperty() {
        return tariffName;
    }

    public boolean isAvailability() {
        return availability.get();
    }

    public void setAvailability(boolean value) {
        availability.set(value);
    }

    public BooleanProperty availabilityProperty() {
        return availability;
    }

    // ====== Capacity handling ======
    public void setParkingCapacity(String vehicleName, int capacity) {
        parkingCapacities.put(vehicleName, capacity);
    }

    public int getCustomParking(String vehicleName) {
        return parkingCapacities.getOrDefault(vehicleName, 0);
    }

    public IntegerProperty getCustomParkingProperty(String vehicleName) {
        return new SimpleIntegerProperty(getCustomParking(vehicleName));
    }

    // ====== Tariff handling (New) ======
    public void setParkingTariff(String vehicleName, int tariff) {
        parkingTariffs.put(vehicleName, tariff);
    }

    public int getCustomTariff(String vehicleName) {
        return parkingTariffs.getOrDefault(vehicleName, 0);
    }

    public IntegerProperty getCustomTariffProperty(String vehicleName) {
        return new SimpleIntegerProperty(getCustomTariff(vehicleName));
    }

    public BooleanProperty getAvailability() {
        return availability;
    }

    private final Map<String, Integer> usedCapacities = new HashMap<>();

    public void setUsedCapacity(String vehicleName, int used) {
        usedCapacities.put(vehicleName, used);
    }

    public int getUsedCapacity(String vehicleName) {
        return usedCapacities.getOrDefault(vehicleName, 0);
    }

    public IntegerProperty getUsedCapacityProperty(String vehicleName) {
        return new SimpleIntegerProperty(getUsedCapacity(vehicleName));
    }

}
