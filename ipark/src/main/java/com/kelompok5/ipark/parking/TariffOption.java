package com.kelompok5.ipark.parking;

public class TariffOption {
    private final int id;
    private final String name;

    public TariffOption(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    @Override
    public String toString() {
        return name; // agar yang tampil di ComboBox adalah nama
    }
}