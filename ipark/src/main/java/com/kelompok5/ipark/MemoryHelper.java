package com.kelompok5.ipark;

import javafx.event.ActionEvent;

public interface MemoryHelper {
    public void initializeDB();
    public void saveToDB(ActionEvent event);
    public void loadData();
    public void showData();
}
