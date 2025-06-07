package com.bbm.chessjavafx.util;

import com.bbm.chessjavafx.model.game.Board;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;

public class FENConverter {
    public static String convertToFEN(Board board) {
        StringBuilder fen = new StringBuilder();

        for (int row = 0; row < Board.SIZE; row++) {
            int emptyCount = 0;
            for (int col = 0; col < Board.SIZE; col++) {
                Piece piece = board.getPiece(new com.bbm.chessjavafx.model.pieces.Position(row, col));
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece.getFENSymbol());
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (row < Board.SIZE - 1) {
                fen.append('/');
            }
        }

        fen.append(' ');
        fen.append(board.isWhiteTurn() ? 'w' : 'b');

        fen.append(" ");
        String castling = getCastlingAvailability(board);
        fen.append(castling.isEmpty() ? "-" : castling);

        // TODO: En passant
        fen.append(" -");

        fen.append(" 0 1");

        return fen.toString();
    }

    private static String getCastlingAvailability(Board board) {
        StringBuilder result = new StringBuilder();

        Piece[][] b = board.getBoard();

        if (isCastlingPossible(b, 7, 4, 7)) {
            result.append('K');
        }

        if (isCastlingPossible(b, 7, 4, 0)) {
            result.append('Q');
        }

        if (isCastlingPossible(b, 0, 4, 7)) {
            result.append('k');
        }

        if (isCastlingPossible(b, 0, 4, 0)) {
            result.append('q');
        }

        return result.toString();
    }

    private static boolean isCastlingPossible(Piece[][] board, int row, int kingCol, int rookCol) {
        Piece king = board[row][kingCol];
        Piece rook = board[row][rookCol];

        if (!(king instanceof com.bbm.chessjavafx.model.pieces.King k) || k.hasMoved()) return false;
        if (!(rook instanceof com.bbm.chessjavafx.model.pieces.Rook r) || r.hasMoved()) return false;
        return king.isWhite() == rook.isWhite();
    }

    private static Piece createPieceFromFEN(char pieceChar, boolean isWhite, Position position) {
        return switch (pieceChar) {
            case 'p' -> new com.bbm.chessjavafx.model.pieces.Pawn(isWhite, position);
            case 'r' -> new com.bbm.chessjavafx.model.pieces.Rook(isWhite, position);
            case 'n' -> new com.bbm.chessjavafx.model.pieces.Knight(isWhite, position);
            case 'b' -> new com.bbm.chessjavafx.model.pieces.Bishop(isWhite, position);
            case 'q' -> new com.bbm.chessjavafx.model.pieces.Queen(isWhite, position);
            case 'k' -> new com.bbm.chessjavafx.model.pieces.King(isWhite, position);
            default -> throw new IllegalArgumentException("Unknown FEN piece: " + pieceChar);
        };
    }

    public static void convertFromFEN(String fen, Board board) {
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");

        for (int row = 0; row < Board.SIZE; row++) {
            int col = 0;
            for (char c : rows[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    col += c - '0';
                } else {
                    boolean isWhite = Character.isUpperCase(c);
                    char pieceChar = Character.toLowerCase(c);
                    Piece piece = createPieceFromFEN(pieceChar, isWhite, new Position(row, col));
                    board.setPiece(new Position(row, col), piece);
                    col++;
                }
            }
        }

        board.setWhiteTurn(parts[1].equals("w"));
    }

}
