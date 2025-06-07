package com.bbm.chessjavafx.model.pieces;

import com.bbm.chessjavafx.model.game.Board;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece implements Cloneable {
    public Pawn(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = isWhite ? -1 : 1;

        int x = position.getX();
        int y = position.getY();

        Position oneStep = new Position(x + direction, y);
        if (oneStep.isValid() && board.getPiece(oneStep) == null) {
            moves.add(oneStep);

            if ((isWhite && x == 6) || (!isWhite && x == 1)) {
                Position twoStep = new Position(x + 2 * direction, y);
                if (board.getPiece(twoStep) == null) {
                    moves.add(twoStep);
                }
            }
        }

        for (int dx = -1; dx <= 1; dx += 2) {
            Position diag = new Position(x + direction, y + dx);
            if (diag.isValid()) {
                Piece target = board.getPiece(diag);
                if (target != null && target.isWhite() != isWhite) {
                    moves.add(diag);
                }
            }
        }

        // TODO: реалізувати en passant

        return moves;
    }

    @Override
    public List<Position> getPseudoLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = isWhite ? -1 : 1;

        int x = position.getX();
        int y = position.getY();

        Position leftAttack = new Position(x + direction, y - 1);
        Position rightAttack = new Position(x + direction, y + 1);

        if (leftAttack.isValid()) moves.add(leftAttack);
        if (rightAttack.isValid()) moves.add(rightAttack);

        return moves;
    }

    @Override
    public Piece clone() {
        return new Pawn(isWhite, new Position(position.getX(), position.getY()));
    }

    @Override
    public String getFENSymbol() {
        return isWhite ? "P" : "p";
    }
}
