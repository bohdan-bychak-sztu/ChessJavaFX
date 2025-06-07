package com.bbm.chessjavafx.model.pieces;

import com.bbm.chessjavafx.model.game.Board;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece implements Cloneable {
    public Knight(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {2, 1, -1, -2, -2, -1, 1, 2};

        for (int i = 0; i < dx.length; i++) {
            Position newPos = new Position(position.getX() + dx[i], position.getY() + dy[i]);
            if (newPos.isValid()) {
                Piece target = board.getPiece(newPos);
                if (target == null || target.isWhite() != isWhite) {
                    moves.add(newPos);
                }
            }
        }
        return moves;
    }

    @Override
    public List<Position> getPseudoLegalMoves(Board board) {
        return getValidMoves(board);
    }

    @Override
    public Piece clone() {
        return new Knight(isWhite, new Position(position.getX(), position.getY()));
    }

    @Override
    public String getFENSymbol() {
        return isWhite ? "N" : "n";
    }

}
