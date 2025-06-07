package com.bbm.chessjavafx.model.game;

import com.bbm.chessjavafx.model.Move.HumanMoveStrategy;
import com.bbm.chessjavafx.model.Move.Move;
import com.bbm.chessjavafx.model.Move.MoveCommand;
import com.bbm.chessjavafx.model.Move.MoveStrategy;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;
import com.bbm.chessjavafx.util.PGNConverter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {
    private final Board board;
    private final ObservableList<String> moveLog = FXCollections.observableArrayList();
    private MoveStrategy whitePlayer;
    private MoveStrategy blackPlayer;
    private Runnable onBoardUpdated;
    private int fullMoveNumber = 1;
    private PGNConverter pgnConverter = new PGNConverter();

    public ChessGame(MoveStrategy whitePlayer, MoveStrategy blackPlayer) {
        this.board = new Board();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public ChessGame(String boardType, MoveStrategy whitePlayer, MoveStrategy blackPlayer) {
        this.board = new Board();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public ChessGame(String boardType) {
        if (boardType.equalsIgnoreCase("default"))
            this.board = new Board(true);
        else
            this.board = new Board(false);
    }

    public ObservableList<String> getMoveLog() {
        return FXCollections.observableArrayList(getPGN().replaceAll("\\d+\\.\\s", "").trim().split("\\s+"));
    }

    public String getPGN() {
        return pgnConverter.getPGN();
    }

    public void setPGN(String pgn) {
        pgnConverter = new PGNConverter(pgn);
    }

    public HumanMoveStrategy getHumanMoveStrategy() {
        if (whitePlayer instanceof HumanMoveStrategy) {
            return (HumanMoveStrategy) whitePlayer;
        }
        if (blackPlayer instanceof HumanMoveStrategy) {
            return (HumanMoveStrategy) blackPlayer;
        }
        return null;
    }

    public void setWhitePlayer(MoveStrategy whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public void setBlackPlayer(MoveStrategy blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public void startAsync() {

        if (board.isGameOver()) {
            System.out.println("Гра завершена!");
            return;
        }

        boolean isWhite = board.isWhiteTurn();
        MoveStrategy currentPlayer = isWhite ? whitePlayer : blackPlayer;

        System.out.println("Зараз хід: " + (isWhite ? "Білих" : "Чорних"));
        System.out.println("Гравець: " + currentPlayer.getClass().getSimpleName());

        if (currentPlayer instanceof HumanMoveStrategy) {
            waitForHumanMove(isWhite);
        } else {
            Move move = currentPlayer.decideMove(board, isWhite);
            processMove(move, isWhite);
            startAsync();
        }
    }

    private void waitForHumanMove(boolean isWhite) {
        HumanMoveStrategy humanStrategy = (HumanMoveStrategy) (isWhite ? whitePlayer : blackPlayer);
        humanStrategy.setOnMoveMade(move -> {
            processMove(move, isWhite);
            startAsync();
        });
    }

    private void processMove(Move move, boolean isWhite) {
        System.out.println(move.toString());
        if (!isValidMoveForPlayer(move, isWhite)) {
            System.out.println("Невалідний хід");
            return;
        }

        Position from;
        from = move.getPiece().getPosition().clone();
        Board clonedBoard = board.copy();
        MoveCommand moveCommand = new MoveCommand(board, move.getPiece(), move.getTo());
        moveCommand.execute();

        pgnConverter.addMove(clonedBoard, from.clone(), move.clone());

        if (!isWhite) {
            fullMoveNumber++;
        }

        if (onBoardUpdated != null) {
            Platform.runLater(onBoardUpdated);
        }

    }

    private boolean isValidMoveForPlayer(Move move, boolean isWhite) {
        if (move == null) return false;

        Piece piece = move.getPiece();
        if (piece == null) return false;

        if (piece.isWhite() != board.isWhiteTurn()) return false;

        return board.isValidMove(piece, move.getTo());
    }

    public void setOnBoardUpdated(Runnable onBoardUpdated) {
        this.onBoardUpdated = onBoardUpdated;
    }

    public Piece[][] getPieces() {
        return board.getBoard();
    }

    public Board getBoard() {
        return board;
    }
}
