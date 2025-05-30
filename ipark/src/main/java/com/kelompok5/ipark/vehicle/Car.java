package com.kelompok5.ipark.vehicle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.kelompok5.ipark.utils.Statics;

public class Car extends Vehicle {
    private String name, type;

    public Car(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void addVehicle(String name) {
        try {
            Connection connection = DriverManager.getConnection(Statics.jdbcUrl);
            String sql = "INSERT INTO vehicles (name, type) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, getType());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {

        }
    }

    @Override
    public void editVehicle(int id, String name, String type) {
        try {
            Connection connection = DriverManager.getConnection(Statics.jdbcUrl);
            String sql = "UPDATE vehicles SET name = ?, type = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setInt(3, id);
            ps.executeUpdate();
            ps.close();

        } catch (Exception e) {

        }
    }

    @Override
    public void deleteVehicle(VehicleModel vehicle) {
        if (!vehicle.getName().equals(getName())) {
            try (Connection conn = DriverManager.getConnection(Statics.jdbcUrl)) {
                String sql = "DELETE FROM vehicles WHERE name = ? AND type = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, vehicle.getName());
                ps.setString(2, vehicle.getType());
                ps.executeUpdate();
                ps.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void parkTo(int id) {
        // Implementation for parking a car
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }
}
