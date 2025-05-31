package com.kelompok5.ipark.tariff;

public class Tariff {
    String name;
    int car_tariff, motorCycle_tariff, bicycleTariff;
    Object[] customTariffs;

    public Tariff(String name, int car_tariff, int motorCycle_tariff, int bicycleTariff) {
        this.name = name;
        this.car_tariff = car_tariff;
        this.motorCycle_tariff = motorCycle_tariff;
        this.bicycleTariff = bicycleTariff;
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

    public Object[] getCustomTariffs() {
        return customTariffs;
    }   

    public void addTariff(String name, int car_tariff, int motorCycle_tariff, int bicycleTariff) {

    }

    public void editTariff(int id, String name, int car_tariff, int motorCycle_tariff, int bicycleTariff) {

    }

    public void deleteTariff(TariffModel tariffModel) {

    }
}
