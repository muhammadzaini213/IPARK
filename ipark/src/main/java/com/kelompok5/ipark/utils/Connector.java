package com.kelompok5.ipark.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class Connector {

    public Connection connector() throws SQLException {
        Connection connection = DriverManager.getConnection(Statics.jdbcUrl);
        return connection;
    }

    public void checkTableIfNotExists(String tableName, String structure, String unique) throws SQLException {
        String checkSql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement stmt = connector().prepareStatement(checkSql)) {
            stmt.setString(1, tableName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    String createSql = "CREATE TABLE " + tableName + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + structure + ", UNIQUE(" + unique + "))";
                    if (unique == null || unique.isEmpty()) {
                        createSql = "CREATE TABLE " + tableName + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + structure + ")";
                    }
                    try (Statement createStmt = connector().createStatement()) {
                        createStmt.executeUpdate(createSql);
                    }
                }
            }
        }
    }

    public void dropThenCreateTable(String tableName, String structure, String unique) throws SQLException {
        try (Statement statement = connector().createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS " + tableName);

            String createSQL = String.format(
                    "CREATE TABLE %s (id INTEGER PRIMARY KEY AUTOINCREMENT, %s, UNIQUE(%s))",
                    tableName, structure, unique);

            statement.executeUpdate(createSQL);
        }
    }

    public void insertToTable(String tableName, String structure, Object[] values) {
        try {
            String placeholders = String.join(", ", java.util.Collections.nCopies(values.length, "?"));
            String sql = "INSERT INTO " + tableName + " (" + structure + ") VALUES (" + placeholders + ")";

            try (PreparedStatement statement = connector().prepareStatement(sql)) {
                for (int i = 0; i < values.length; i++) {
                    Object value = values[i];
                    if (value instanceof Integer) {
                        statement.setInt(i + 1, (Integer) value);
                    } else if (value instanceof String) {
                        statement.setString(i + 1, (String) value);
                    } else if (value instanceof Boolean) {
                        statement.setBoolean(i + 1, (Boolean) value);
                    } else if (value == null) {
                        statement.setNull(i + 1, java.sql.Types.NULL);
                    } else {
                        throw new SQLException("Unsupported data type at index " + i + ": " + value.getClass());
                    }
                }
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            // e.printStackTrace();
        }
    }

    public boolean areRowsPresent(String tableName, String[] columns, Object[][] rows) throws SQLException {
        if (rows.length == 0)
            return true;

        try (Connection connection = DriverManager.getConnection(Statics.jdbcUrl)) {
            StringJoiner whereClause = new StringJoiner(" OR ");
            for (Object[] row : rows) {
                StringJoiner andClause = new StringJoiner(" AND ", "(", ")");
                for (String col : columns) {
                    andClause.add(col + " = ?");
                }
                whereClause.add(andClause.toString());
            }

            String sql = "SELECT " + String.join(", ", columns) + " FROM " + tableName + " WHERE " + whereClause;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int paramIndex = 1;
                for (Object[] row : rows) {
                    for (Object value : row) {
                        if (value instanceof Integer) {
                            ps.setInt(paramIndex++, (Integer) value);
                        } else if (value instanceof Double) {
                            ps.setDouble(paramIndex++, (Double) value);
                        } else if (value instanceof Boolean) {
                            ps.setBoolean(paramIndex++, (Boolean) value);
                        } else {
                            ps.setString(paramIndex++, value.toString());
                        }
                    }
                }

                Set<String> existing = new HashSet<>();
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        StringBuilder key = new StringBuilder();
                        for (String col : columns) {
                            key.append(rs.getString(col)).append("|");
                        }
                        existing.add(key.toString());
                    }
                }

                for (Object[] row : rows) {
                    StringBuilder key = new StringBuilder();
                    for (Object value : row) {
                        key.append(value.toString()).append("|");
                    }
                    if (!existing.contains(key.toString()))
                        return false;
                }

                return true;
            }
        }
    }

    public ResultSet loadItem(String tableName, String[] columns) throws SQLException {
        String columnList = String.join(", ", columns);
        String sql = "SELECT " + columnList + " FROM " + tableName;

        Statement stmt = connector().createStatement();
        return stmt.executeQuery(sql);
    }

    public void updateItem(String tableName, String[] columns, Object[] values, int id) {
        if (columns.length != values.length || columns.length == 0) {
            throw new IllegalArgumentException("Columns and values must be same length and not empty");
        }

        StringBuilder setClause = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            setClause.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) {
                setClause.append(", ");
            }
        }

        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE id = ?";

        try (PreparedStatement ps = connector().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                Object value = values[i];
                if (value instanceof Integer) {
                    ps.setInt(i + 1, (Integer) value);
                } else if (value instanceof Double) {
                    ps.setDouble(i + 1, (Double) value);
                } else if (value instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) value);
                } else if (value == null) {
                    ps.setNull(i + 1, java.sql.Types.NULL);
                } else {
                    ps.setString(i + 1, value.toString());
                }
            }
            ps.setInt(values.length + 1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(String tableName, int id) {
        try {
            String sql = "DELETE FROM " + tableName + " WHERE id = ?";
            try (PreparedStatement ps = connector().prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
