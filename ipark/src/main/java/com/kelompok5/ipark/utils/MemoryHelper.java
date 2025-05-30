package com.kelompok5.ipark.utils;

import javafx.event.ActionEvent;

public interface MemoryHelper {
    public void initializeDB();
    public void saveToDB(ActionEvent event);
    public void loadData();
}
