package com.bbm.chessjavafx.model.pieces;

public interface PieceFactory {
    Piece createPiece(char pieceChar, boolean isWhite, Position position);
}