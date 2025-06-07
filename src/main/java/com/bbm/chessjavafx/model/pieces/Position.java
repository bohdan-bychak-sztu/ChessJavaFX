package com.bbm.chessjavafx.model.pieces;

import com.bbm.chessjavafx.model.game.Board;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(String position) {
        this.x = 8 - Character.getNumericValue(position.charAt(1));
        this.y = position.charAt(0) - 97;
    }

    public boolean isValid() {
        return x >= 0 && x < Board.SIZE && y >= 0 && y < Board.SIZE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position p)) return false;
        return x == p.x && y == p.y;
    }

    @Override
    public Position clone() {
        try {
            Position position = (Position) super.clone();
        } catch (CloneNotSupportedException ignored) {
        }
        return new Position(this.x, this.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
