package com.bbm.chessjavafx.controller;

import com.bbm.chessjavafx.model.ChessGameModel;
import com.bbm.chessjavafx.model.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MenuController {
    public Button savedGamesButton;
    public VBox savedGamesContainer;
    public ScrollPane savedGamesScrollPane;
    @FXML
    private Button startButton;
    private final DatabaseManager db = new DatabaseManager();

    @FXML
    public void initialize() {
        startButton.setOnAction(event -> openBoard());
    }

    @FXML
    private void openBoard() {
        if (db.getGameByName("LastGame") != null) {
            loadGame(db.getGameByName("LastGame"));
        }
        else
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

    @FXML
    private void showSavedGames() {
        savedGamesContainer.getChildren().clear();

        List<ChessGameModel> savedGames = db.loadAllGames();

        for (ChessGameModel game : savedGames) {
            Label nameLabel = new Label(game.getName());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label dateLabel = new Label(game.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

            HBox gameBox = getHBox(game, nameLabel, dateLabel);

            savedGamesContainer.getChildren().add(gameBox);
        }
    }

    private HBox getHBox(ChessGameModel game, Label nameLabel, Label dateLabel) {
        VBox textBox = new VBox(nameLabel, dateLabel);
        textBox.setSpacing(2);

        Button loadButton = new Button("✅");
        loadButton.setOnAction(e -> loadGame(game));
        loadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(e -> {
            db.deleteGameById(game.getId());
            showSavedGames();
        });
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox gameBox = new HBox(textBox, loadButton, deleteButton);
        gameBox.setSpacing(20);
        gameBox.setPadding(new Insets(5));
        gameBox.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        return gameBox;
    }

    private void loadGame(ChessGameModel game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbm/chessjavafx/board.fxml"));
            Parent root = loader.load();

            GameController gameController = loader.getController();

            gameController.loadGame(game);

            Stage stage = (Stage) savedGamesContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
