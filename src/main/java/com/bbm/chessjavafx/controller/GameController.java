package com.bbm.chessjavafx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameController {
    @FXML
    private GridPane chessBoard;

    @FXML
    private ChoiceBox<String> difficultyChoice;

    @FXML
    private ChoiceBox<String> modeChoice;

    @FXML
    public void initialize() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(60, 60);
                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
                cell.setStyle("-fx-background-color: " + color + ";");
                chessBoard.add(cell, col, row);
            }
        }
    }

    @FXML
    private void goBackToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbm/chessjavafx/menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) chessBoard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
