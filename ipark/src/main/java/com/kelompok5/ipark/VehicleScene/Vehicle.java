package com.kelompok5.ipark.VehicleScene;

public abstract class Vehicle {
        public abstract void addVehicle(String name);

        public abstract void editVehicle(int id, String name, String type);

        public abstract void deleteVehicle(VehicleModel vehicle);

        public abstract void parkTo(int id);

        public abstract String getName();

        public abstract String getType();
    }