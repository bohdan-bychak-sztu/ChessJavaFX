package com.bbm.chessjavafx.model.pieces;

public class StandardPieceFactory extends PieceFactory {
    @Override
    public Piece createPiece(String type, boolean isWhite, Position position) {
        return switch (type.toLowerCase()) {
            case "rook" -> new Rook(isWhite, position);
            default -> throw new IllegalArgumentException("Unknown piece type: " + type);
        };
    }
}
