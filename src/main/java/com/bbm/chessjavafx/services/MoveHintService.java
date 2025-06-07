package com.bbm.chessjavafx.services;

import com.bbm.chessjavafx.model.pieces.Position;
import com.bbm.chessjavafx.util.ImageLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.List;

public class MoveHintService {

    public void showHints(List<Position> moves, StackPane[][] cells) {
        for (Position pos : moves) {
            ImageView hint = new ImageView(ImageLoader.loadImage("images/pieces/hint.png"));
            hint.setUserData("hint");
            cells[pos.getX()][pos.getY()].getChildren().add(hint);
        }
    }

    public void clearHints(StackPane[][] cells) {
        for (StackPane[] row : cells) {
            for (StackPane cell : row) {
                cell.getChildren().removeIf(node -> node instanceof ImageView && "hint".equals(node.getUserData()));
            }
        }
    }

    public boolean isHintCell(Object node) {
        if (!(node instanceof StackPane cell)) return false;
        return cell.getChildren().stream().anyMatch(child -> "hint".equals(child.getUserData()));
    }
}
