package com.bbm.chessjavafx.util;

import com.bbm.chessjavafx.model.game.Board;
import com.bbm.chessjavafx.model.pieces.Piece;

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

        // White king side
        if (isCastlingPossible(b, 7, 4, 7)) {
            result.append('K');
        }

        // White queen side
        if (isCastlingPossible(b, 7, 4, 0)) {
            result.append('Q');
        }

        // Black king side
        if (isCastlingPossible(b, 0, 4, 7)) {
            result.append('k');
        }

        // Black queen side
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
}
