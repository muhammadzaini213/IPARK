package com.kelompok5.ipark.vehicle;

public abstract class Vehicle {
        public abstract void addVehicle(String name);

        public abstract void editVehicle(int id, String[] values);

        public abstract void deleteVehicle(int id);

        public abstract void parkTo(int id);

        public abstract String getName();

        public abstract String getType();
    }