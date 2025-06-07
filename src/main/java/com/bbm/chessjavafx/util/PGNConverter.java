package com.bbm.chessjavafx.util;

import com.bbm.chessjavafx.model.Move.Move;
import com.bbm.chessjavafx.model.Move.MoveCommand;
import com.bbm.chessjavafx.model.game.Board;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;

public class PGNConverter {
    private final StringBuilder pgnBuilder = new StringBuilder();
    private int moveNumber = 1;
    private boolean isWhiteTurn = true;

    public PGNConverter() { }

    public PGNConverter(String pgn) {
        pgnBuilder.append(pgn);
    }

    public void addMove(Board board, Position fromPos, Move move) {
        String notation = toAlgebraicNotation(board, fromPos, move, isWhiteTurn, moveNumber);
        if (isWhiteTurn) {
            pgnBuilder.append(moveNumber).append(". ").append(notation).append(" ");
        } else {
            pgnBuilder.append(notation).append(" ");
            moveNumber++;
        }
        isWhiteTurn = !isWhiteTurn;
    }

    public static String toAlgebraicNotation(Board board, Position fromPos, Move move, boolean isWhite, int moveNumber) {
        Piece piece = move.getPiece();
        String from = String.valueOf((char) (fromPos.getY() + 97)) + (fromPos.getX());
        String to = String.valueOf((char) (move.getTo().getY() + 97)) + (move.getTo().getX());

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
            if (dx == 2) return "O-O";
            if (dx == -2) return "O-O-O";
        }

        boolean isCapture = board.getPiece(move.getTo()) != null;
        String captureSymbol = isCapture ? "x" : "";

        if (figureLetter.isEmpty() && isCapture) {
            figureLetter = from.substring(0, 1);
        }

        String checkOrMate = "";
        Board clonedBoard = board.copy();
        new MoveCommand(clonedBoard, piece, move.getTo()).execute();

        if (clonedBoard.isCheckmate(!isWhite)) {
            checkOrMate = "#";
        } else if (clonedBoard.isKingInCheck(!isWhite)) {
            checkOrMate = "+";
        }

        return figureLetter + captureSymbol + to + checkOrMate;
    }

    public String getPGN() {
        return pgnBuilder.toString().trim();
    }
}
