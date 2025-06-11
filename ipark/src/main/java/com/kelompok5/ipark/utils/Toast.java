package main.java.com.kelompok5.ipark.utils;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Toast {

    public static void showToast(Stage owner, String message) {
        Stage toastStage = new Stage();
        toastStage.initOwner(owner);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Label label = new Label(message);
        label.setStyle("-fx-background-color: rgba(220, 53, 69, 0.9); -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 5px;");

        VBox root = new VBox(label);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(root);
        scene.setFill(null);
        toastStage.setScene(scene);
        toastStage.setWidth(300);
        toastStage.setHeight(100);
        toastStage.setY(owner.getY() + 100); // vertical offset
        toastStage.setX(owner.getX() + owner.getWidth()/2 - 150); // center horizontally

        toastStage.show();

        new Thread(() -> {
            try {
                Thread.sleep(2500); // toast duration
            } catch (InterruptedException ignored) {}
            Platform.runLater(toastStage::close);
        }).start();
    }
}

