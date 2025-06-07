package com.bbm.chessjavafx.model.pieces;

import com.bbm.chessjavafx.model.game.Board;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece implements Cloneable {
    private boolean hasMoved = false;

    public Rook(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        for (int dir = 0; dir < 4; dir++) {
            for (int step = 1; step < Board.SIZE; step++) {
                int newX = position.getX() + dx[dir] * step;
                int newY = position.getY() + dy[dir] * step;
                Position newPos = new Position(newX, newY);
                if (!newPos.isValid()) break;

                Piece target = board.getPiece(newPos);
                if (target == null) {
                    moves.add(newPos);
                } else {
                    if (target.isWhite() != isWhite) moves.add(newPos);
                    break;
                }
            }
        }
        return moves;
    }

    @Override
    public Piece clone() {
        return new Rook(isWhite, new Position(position.getX(), position.getY()));
    }

    @Override
    public String getFENSymbol() {
        return isWhite ? "R" : "r";
    }

    @Override
    public List<Position> getPseudoLegalMoves(Board board) {
        return getValidMoves(board);
    }
}
