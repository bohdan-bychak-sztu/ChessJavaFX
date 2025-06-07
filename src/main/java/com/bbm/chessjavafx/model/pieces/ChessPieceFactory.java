package com.bbm.chessjavafx.model.pieces;

public class ChessPieceFactory implements PieceFactory {
    @Override
    public Piece createPiece(char pieceChar, boolean isWhite, Position position) {
        return switch (pieceChar) {
            case 'p' -> new Pawn(isWhite, position);
            case 'r' -> new Rook(isWhite, position);
            case 'n' -> new Knight(isWhite, position);
            case 'b' -> new Bishop(isWhite, position);
            case 'q' -> new Queen(isWhite, position);
            case 'k' -> new King(isWhite, position);
            default -> throw new IllegalArgumentException("Unknown FEN piece: " + pieceChar);
        };
    }
}
