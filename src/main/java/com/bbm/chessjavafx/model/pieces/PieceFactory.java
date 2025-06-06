package com.bbm.chessjavafx.model.pieces;

public abstract class PieceFactory {
    public abstract Piece createPiece(String type, boolean isWhite, Position position);
}