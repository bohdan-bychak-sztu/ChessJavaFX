package com.bbm.chessjavafx.model.Move;

import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;

public class Move {
    private final Piece piece;
    private final Position to;

    public Move(Piece piece, Position to) {
        this.piece = piece;
        this.to = to;
    }

    public Piece getPiece() {
        return piece;
    }

    public Position getTo() {
        return to;
    }

    @Override
    public Move clone() {
        return new Move(piece.clone(), to.clone());
    }

    @Override
    public String toString() {
        return "Move[" + "piece=" + piece + ", to=" + to + ']';
    }
}
