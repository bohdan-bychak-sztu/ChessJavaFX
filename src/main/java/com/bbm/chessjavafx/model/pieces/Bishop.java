package com.bbm.chessjavafx.model.pieces;

import com.bbm.chessjavafx.model.game.Board;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[] dx = {1, -1, -1, 1};
        int[] dy = {1, -1, 1, -1};

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
    public List<Position> getPseudoLegalMoves(Board board) {
        return getValidMoves(board);
    }

    @Override
    public Piece clone() {
        return new Bishop(isWhite, new Position(position.getX(), position.getY()));
    }

    @Override
    public String getFENSymbol() {
        return isWhite ? "B" : "b";
    }
}