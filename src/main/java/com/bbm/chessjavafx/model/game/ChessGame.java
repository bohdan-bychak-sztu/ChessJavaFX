package com.bbm.chessjavafx.model.game;

import com.bbm.chessjavafx.model.Move.Move;
import com.bbm.chessjavafx.model.Move.MoveCommand;
import com.bbm.chessjavafx.model.Move.MoveStrategy;
import com.bbm.chessjavafx.model.pieces.Piece;

public class ChessGame {
    private final Board board;
    private final MoveStrategy whitePlayer;
    private final MoveStrategy blackPlayer;

    public ChessGame(MoveStrategy whitePlayer, MoveStrategy blackPlayer) {
        this.board = new Board();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public ChessGame(String boardType, MoveStrategy whitePlayer, MoveStrategy blackPlayer) {
        this(whitePlayer, blackPlayer);
        Board board = new Board();
    }

    public void start() {
        while (!board.isGameOver()) {
            boolean isWhite = board.isWhiteTurn();
            MoveStrategy currentPlayer = isWhite ? whitePlayer : blackPlayer;

            Move move = currentPlayer.decideMove(board, isWhite);
            if (move == null) continue;

            Piece piece = move.getPiece();
            if (piece == null || piece.isWhite() != isWhite) continue;

            if (board.isValidMove(piece, move.getTo())) {
                MoveCommand command = new MoveCommand(board, piece, move.getTo());
                command.execute();
                board.toggleTurn();
            }
        }

        System.out.println("Гра завершена!");
    }

    public Piece[][] getPieces() {
        return board.getBoard();
    }

    public Board getBoard() {
        return board;
    }
}
