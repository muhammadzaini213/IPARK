package com.kelompok5.ipark.VehicleScene;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.kelompok5.ipark.Statics;

public class CustomVehicle {

        public void addVehicle(String name, String type) {
            try {
            Connection connection = DriverManager.getConnection(Statics.jdbcUrl);
            String sql = "INSERT INTO vehicles (name, type) VALUES (?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, type);
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {

            }
        }

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

        public void deleteVehicle(VehicleModel vehicle) {
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

        public void parkTo(int id) {
            throw new UnsupportedOperationException("Unimplemented method 'parkTo'");
        }

    }