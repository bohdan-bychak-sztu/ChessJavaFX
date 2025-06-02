package com.bbm.chessjavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {
    @FXML
    private Button startButton;

    @FXML
    public void initialize() {
        startButton.setOnAction(event -> openBoard());
    }

    @FXML
    private void openBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbm/chessjavafx/board.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitGame() {
        System.exit(0);
    }
}
