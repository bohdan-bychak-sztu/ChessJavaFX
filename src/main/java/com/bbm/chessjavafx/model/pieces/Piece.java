package com.bbm.chessjavafx.model.pieces;

import com.bbm.chessjavafx.model.game.Board;

import java.util.List;

public abstract class Piece {
    protected final boolean isWhite;
    protected Position position;

    public Piece(boolean isWhite, Position position) {
        this.isWhite = isWhite;
        this.position = position;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public abstract List<Position> getValidMoves(Board board);

    public boolean canAttack(Position target, Board board) {
        return getPseudoLegalMoves(board).contains(target);
    }

    public List<Position> getPseudoLegalMoves(Board board) {
        throw new UnsupportedOperationException("Must be implemented in subclasses");    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public abstract Piece clone();

    public abstract String getFENSymbol();

    @Override
    public String toString() {
        return getName();
    }
}
