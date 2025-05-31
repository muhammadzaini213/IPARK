package com.kelompok5.ipark.vehicle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.kelompok5.ipark.utils.Connector;
import com.kelompok5.ipark.utils.Statics;

public class Bicycle extends Vehicle {
    Connector connector = new Connector();

    private String name, type, tableName;
    private String[] structure;

    public Bicycle(String name, String type, String tableName, String[] structure) {
        this.name = name;
        this.type = type;
        this.tableName = tableName; 
        this.structure = structure;
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
    public void editVehicle(int id, String[] values) {
        connector.updateItem(tableName, structure, values, id);
    }

    @Override
    public void deleteVehicle(int id) {
        connector.deleteItem(tableName, id);
    }

    @Override
    public void parkTo(int id) {
        throw new UnsupportedOperationException("Unimplemented method 'parkTo'");
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
