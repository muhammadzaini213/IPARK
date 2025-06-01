package com.kelompok5.ipark.parking;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ParkingModel {
    private final int id;
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty location_tariff, tariff_id;
    private final SimpleBooleanProperty availability;
    private final SimpleStringProperty tariffName;  // add this

    private Map<String, Integer> parkingCapacities = new HashMap<>();

    public ParkingModel(int id, String name, int location_tariff, int tariff_id, boolean availability) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.location_tariff = new SimpleIntegerProperty(location_tariff);
        this.tariff_id = new SimpleIntegerProperty(tariff_id);
        this.availability = new SimpleBooleanProperty(availability);
        this.tariffName = new SimpleStringProperty("");  // initialize empty
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

    public SimpleIntegerProperty locationTariffProperty() {
        return location_tariff;
    }

    public int getTariffId() {
        return tariff_id.get();
    }

    public SimpleIntegerProperty tariffIdProperty() {
        return tariff_id;
    }

    public String getTariffName() {
        return tariffName.get();
    }

    public void setTariffName(String value) {
        tariffName.set(value);
    }

    public SimpleStringProperty tariffNameProperty() {
        return tariffName;
    }

    public boolean isAvailability() {
        return availability.get();
    }

    public void setAvailability(boolean value) {
        availability.set(value);
    }

    public SimpleBooleanProperty availabilityProperty() {
        return availability;
    }

    public void setParkingCapacity(String vehicleName, int capacity) {
        parkingCapacities.put(vehicleName, capacity);
    }

    public int getCustomParking(String vehicleName) {
        return parkingCapacities.getOrDefault(vehicleName, 0);
    }

    public IntegerProperty getCustomParkingProperty(String vehicleName) {
        return new SimpleIntegerProperty(getCustomParking(vehicleName));
    }

    public int getLocation_tariff() {
        return location_tariff.get();
    }
}
