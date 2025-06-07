package com.bbm.chessjavafx.services;

import com.bbm.chessjavafx.model.game.Board;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;

public class BoardCloner {
    private final Board source;

    public BoardCloner(Board source) {
        this.source = source;
    }

    public Board copyBoard() {
        Board copy = new Board(false);
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                Piece piece = source.getBoard()[x][y];
                if (piece != null) {
                    copy.setPiece(new Position(x, y), piece.clone());
                }
            }
        }
        copy.setWhiteTurn(source.isWhiteTurn());
        return copy;
    }
}
