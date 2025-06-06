package com.bbm.chessjavafx.util;

import com.bbm.chessjavafx.model.Move.Move;
import com.bbm.chessjavafx.model.Move.MoveCommand;
import com.bbm.chessjavafx.model.game.Board;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;

public class PGNConverter {
    public static String toAlgebraicNotation(Board board, Position fromPos, Move move, boolean isWhite, int moveNumber) {
        Piece piece = move.getPiece();
        String from = String.valueOf((char)(fromPos.getY() + 97)) + (fromPos.getX());
        String to = String.valueOf((char)((move.getTo().getY() + 97))) + (move.getTo().getX());

        String figureLetter = switch (piece.getClass().getSimpleName()) {
            case "King" -> "K";
            case "Queen" -> "Q";
            case "Rook" -> "R";
            case "Bishop" -> "B";
            case "Knight" -> "N";
            case "Pawn" -> "";
            default -> "";
        };

        if (piece.getClass().getSimpleName().equals("King")) {
            int dx = move.getTo().getY() - fromPos.getY();
            if (dx == 2) return (isWhite ? moveNumber + ". " : "") + "O-O";
            if (dx == -2) return (isWhite ? moveNumber + ". " : "") + "O-O-O";
        }

        boolean isCapture = board.getPiece(move.getTo()) == null;
        String captureSymbol = isCapture ? "x" : "";

        if (figureLetter.isEmpty() && isCapture) {
            figureLetter = from.substring(0, 1);
        }

        String checkOrMate = "";
        Board clonedBoard = board.copy();
        new MoveCommand(clonedBoard, piece, move.getTo()).execute();
        if (clonedBoard.isCheckmate(move.getPiece().isWhite())) {
            checkOrMate = "#";
        } else if (clonedBoard.isKingInCheck(!isWhite)) {
            checkOrMate = "+";
        }

        String notation = figureLetter + captureSymbol + to + checkOrMate;
        if (isWhite) {
            return moveNumber + ". " + notation;
        } else {
            return notation;
        }
    }
}
