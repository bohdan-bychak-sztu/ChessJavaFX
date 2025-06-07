package com.bbm.chessjavafx.services;

import com.bbm.chessjavafx.model.game.Board;
import com.bbm.chessjavafx.model.pieces.King;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;

public class GameStateChecker {
    private final Board board;

    public GameStateChecker(Board board) {
        this.board = board;
    }

    public boolean isGameOver() {
        return isCheckmate(true) || isCheckmate(false)
                || isStalemate(true) || isStalemate(false);
    }

    public String getGameResult() {
        if (isCheckmate(true)) return "Чорні виграли (мат білим)";
        if (isCheckmate(false)) return "Білі виграли (мат чорним)";
        if (isStalemate(true) || isStalemate(false)) return "Нічия (пат)";
        return "Гра триває";
    }

    public boolean isCheckmate(boolean isWhite) {
        return isKingInCheck(isWhite) && !hasAnyValidMove(isWhite);
    }

    public boolean isStalemate(boolean isWhite) {
        return !isKingInCheck(isWhite) && !hasAnyValidMove(isWhite);
    }

    public boolean isKingInCheck(boolean isWhite) {
        Position kingPos = findKingPosition(isWhite);
        if (kingPos == null) return false;

        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                Piece piece = board.getBoard()[x][y];
                if (piece != null && piece.isWhite() != isWhite) {
                    if (piece.canAttack(kingPos, board)) return true;
                }
            }
        }
        return false;
    }

    private Position findKingPosition(boolean isWhite) {
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                Piece piece = board.getBoard()[x][y];
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return piece.getPosition();
                }
            }
        }
        return null;
    }

    public boolean hasAnyValidMove(boolean isWhite) {
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                Piece piece = board.getBoard()[x][y];
                if (piece != null && piece.isWhite() == isWhite) {
                    for (Position move : piece.getValidMoves(board)) {
                        Board clone = board.copy();
                        Piece copyPiece = clone.getPiece(piece.getPosition());
                        clone.movePiece(copyPiece, move, true);
                        if (!clone.getGameStateChecker().isKingInCheck(isWhite)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
