package com.bbm.chessjavafx.model.pieces;

import com.bbm.chessjavafx.model.game.Board;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece implements Cloneable {
    public Queen(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        moves.addAll(new Rook(isWhite, position).getValidMoves(board));
        moves.addAll(new Bishop(isWhite, position).getValidMoves(board));
        return moves;
    }

    @Override
    public Piece clone() {
        return new Queen(isWhite, new Position(position.getX(), position.getY()));
    }

    @Override
    public List<Position> getPseudoLegalMoves(Board board) {
        return getValidMoves(board);
    }

    @Override
    public String getFENSymbol() {
        return isWhite ? "Q" : "q";
    }
}
