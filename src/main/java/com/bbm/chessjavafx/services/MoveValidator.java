package com.bbm.chessjavafx.services;

import com.bbm.chessjavafx.model.game.Board;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;

import java.util.List;

public class MoveValidator {
    private final Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    public boolean isValidMove(Piece piece, Position to) {
        if (piece == null || !to.isValid()) return false;
        if (piece.isWhite() != board.isWhiteTurn()) return false;
        return getLegalMoves(piece).contains(to);
    }

    public List<Position> getLegalMoves(Piece piece) {
        return piece.getValidMoves(board).stream()
                .filter(move -> {
                    Board clone = board.copy();
                    Piece copyPiece = clone.getPiece(piece.getPosition());
                    clone.movePiece(copyPiece, move, true);
                    return !clone.getGameStateChecker().isKingInCheck(piece.isWhite());
                })
                .toList();
    }
}
