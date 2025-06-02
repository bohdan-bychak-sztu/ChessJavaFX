package com.bbm.chessjavafx.model.pieces;

import java.lang.reflect.Array;

public class Rook implements Piece {
    Color color;

    public Rook(Color color){
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }
}
