package com.bbm.chessjavafx.services;

import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.util.ImageLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class ChessBoardRenderer {

    private final StackPane[][] cells = new StackPane[8][8];

    public void initializeBoard(GridPane board, javafx.event.EventHandler<MouseEvent> clickHandler) {
        board.getChildren().clear();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(60, 60);
                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
                cell.setStyle("-fx-background-color: " + color + ";");
                cell.setOnMouseClicked(clickHandler);
                board.add(cell, col, row);
                cells[row][col] = cell;
            }
        }
    }

    public void render(Piece[][] pieces) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane cell = cells[row][col];
                cell.getChildren().clear();
                Piece piece = pieces[row][col];
                if (piece != null) {
                    ImageView image = new ImageView(ImageLoader.loadImage(
                            "images/pieces/" + (piece.isWhite() ? "w" : "b") + piece.getFENSymbol() + ".png"));
                    cell.getChildren().add(image);
                }
            }
        }
    }

    public StackPane[][] getCells() {
        return cells;
    }
}
