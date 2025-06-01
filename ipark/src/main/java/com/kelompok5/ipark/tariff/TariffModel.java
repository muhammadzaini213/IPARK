package com.kelompok5.ipark.tariff;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TariffModel {
    private final int id;
    private final SimpleStringProperty name;

    public TariffModel(int id, String name) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
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

    private Map<String, Integer> customTariffs = new HashMap<>();

    public void setCustomTariff(String vehicleName, int tariff) {
        customTariffs.put(vehicleName, tariff);
    }

    public Integer getCustomTariff(String vehicleName) {
        return customTariffs.getOrDefault(vehicleName, 0);
    }

    public IntegerProperty getCustomTariffProperty(String vehicleName) {
        return new SimpleIntegerProperty(getCustomTariff(vehicleName));
    }

}
