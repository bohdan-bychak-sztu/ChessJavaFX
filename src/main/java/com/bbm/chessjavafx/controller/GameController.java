package com.bbm.chessjavafx.controller;

import com.bbm.chessjavafx.model.Move.HumanMoveStrategy;
import com.bbm.chessjavafx.model.Move.Move;
import com.bbm.chessjavafx.model.Move.MoveStrategy;
import com.bbm.chessjavafx.model.game.ChessGame;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;
import com.bbm.chessjavafx.util.ImageLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final StackPane[][] cells = new StackPane[8][8];
    @FXML
    private GridPane chessBoard;
    @FXML
    private ChoiceBox<String> difficultyChoice;

    @FXML
    private ChoiceBox<String> modeChoice;

    private ChessGame game;
    private Piece selectedPiece;

    private final List<Position> highlightedPositions = new ArrayList<>();

    @FXML
    public void initialize() {
        difficultyChoice.setValue("Easy");
        modeChoice.setValue("Player vs Bot");
        chessBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, this::HandleBoardClick);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(60, 60);
                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
                cell.setStyle("-fx-background-color: " + color + ";");
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleCellClick);
                chessBoard.add(cell, col, row);
                cells[row][col] = cell;
            }
        }
    }

    private void HandleBoardClick(MouseEvent mouseEvent) {
    }

    private void doMove(StackPane cell) {
        boolean hasHintImage = cell.getChildren().stream()
                .anyMatch(node -> node instanceof ImageView && "hint".equals(node.getUserData()));
        if (!hasHintImage) {
            return;
        }

        int col = GridPane.getColumnIndex((Node) cell);
        int row = GridPane.getRowIndex((Node) cell);
        Piece[][] pieces = game.getPieces();
        boolean isSuccess = game.getBoard().movePiece(selectedPiece, new Position(row, col));
        setPieces();

        if (game.getBoard().isGameOver()) {
            System.out.println(game.getBoard().getGameResult());
        }
    }

    private void handleCellClick(MouseEvent event) {
        doMove((StackPane) event.getSource());
        clearHints();

        int col = GridPane.getColumnIndex((Node) event.getSource());
        int row = GridPane.getRowIndex((Node) event.getSource());
        Piece[][] pieces = game.getPieces();


        if (pieces[row][col] != null && pieces[row][col].isWhite() == game.getBoard().isWhiteTurn()) {
            System.out.println(game.getPieces()[row][col].getName());

            selectedPiece = pieces[row][col];

            List<Position> validMoves = game.getBoard().getLegalMoves(pieces[row][col]);
            for (Position move : validMoves) {
                ImageView hint = new ImageView(ImageLoader.loadImage("images/pieces/hint.png"));
                hint.setUserData("hint");
                cells[move.getX()][move.getY()].getChildren().add(hint);
                highlightedPositions.add(move);
            }
        }
    }

    private void clearHints() {
        selectedPiece = null;
        for (Position pos : highlightedPositions) {
            StackPane cell = cells[pos.getX()][pos.getY()];
            cell.getChildren().removeIf(node ->
                    node instanceof ImageView && "hint".equals(node.getUserData())
            );
        }
        highlightedPositions.clear();
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

    @FXML
    private void startGame() {
        try {
            String difficulty = difficultyChoice.getValue();
            String mode = modeChoice.getValue();
            MoveStrategy white;
            MoveStrategy black;
            switch (mode) {
                case "Player vs Bot":
                    white = new HumanMoveStrategy();
                    black = new HumanMoveStrategy();
                    break;
                case "Player vs Player":
                    white = new HumanMoveStrategy();
                    black = new HumanMoveStrategy();
                default:
                    white = new HumanMoveStrategy();
                    black = new HumanMoveStrategy();
            }
            this.game = new ChessGame(white, black);
            setPieces();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPieces() {
        System.out.println("Ходять " + (game.getBoard().isWhiteTurn() ? "білі" : "чорні"));
        Piece[][] pieces = game.getPieces();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cells[row][col].getChildren().clear();
                if (pieces[row][col] != null) {
                    cells[row][col].getChildren().clear();
                    cells[row][col].getChildren().add(new ImageView(ImageLoader.loadImage("images/pieces/" + (pieces[row][col].isWhite() ? "w" : "b") + (pieces[row][col].getFENSymbol()) + ".png")));
                }
            }
        }
    }
}