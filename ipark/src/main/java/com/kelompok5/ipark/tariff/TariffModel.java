package com.kelompok5.ipark.tariff;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TariffModel {
    private final int id;
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty car_tariff, motorCycle_tariff, bicycleTariff;

        public TariffModel(int id, String name, int car_tariff, int motorCycle_tariff, int bicycleTariff) {
            this.id = id;
            this.name = new SimpleStringProperty(name);
            this.car_tariff = new SimpleIntegerProperty(car_tariff);
            this.motorCycle_tariff = new SimpleIntegerProperty(motorCycle_tariff);
            this.bicycleTariff = new SimpleIntegerProperty(bicycleTariff);

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

        public int getCarTariff() {
            return car_tariff.get();
        }

        public SimpleIntegerProperty carTariffProperty() {
            return car_tariff;
        }

        public int getMotorCycleTariff() {
            return motorCycle_tariff.get();
        }

        public SimpleIntegerProperty motorCycleTariffProperty() {
            return motorCycle_tariff;
        }

        public int getBicycleTariff() {
            return bicycleTariff.get();
        }

        public SimpleIntegerProperty bicycleTariffProperty() {
            return bicycleTariff;
        }
}
