package com.bbm.chessjavafx.model.game;

import com.bbm.chessjavafx.model.Move.*;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;
import com.bbm.chessjavafx.util.PGNConverter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChessGame {

    private Board board;
    private PGNConverter pgnConverter;
    private MoveStrategy whitePlayer;
    private MoveStrategy blackPlayer;
    private Runnable onBoardUpdated;
    private int fullMoveNumber = 1;

    public ChessGame(MoveStrategy whitePlayer, MoveStrategy blackPlayer, ChessPosition position) {
        this.board = new Board(position);
        this.pgnConverter = new PGNConverter();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public ChessGame(ChessPosition position) {
        this(null, null, position);
    }

    public ChessGame(String type) {
        this(type.equalsIgnoreCase("default") ? new HumanMoveStrategy() : null,
                type.equalsIgnoreCase("default") ? new HumanMoveStrategy() : null,
                ChessPosition.DEFAULT);
    }

    public void changeBoardType(ChessPosition position) {
        board = new Board(position);
    }

    public void startAsync() {
        if (board.isGameOver()) {
            System.out.println("Гра завершена!");
            return;
        }

        boolean isWhite = board.isWhiteTurn();
        MoveStrategy currentPlayer = isWhite ? whitePlayer : blackPlayer;

        System.out.printf("Зараз хід: %s (%s)%n",
                isWhite ? "Білих" : "Чорних", currentPlayer.getClass().getSimpleName());

        if (currentPlayer instanceof HumanMoveStrategy) {
            waitForHumanMove(isWhite);
        } else {
            Move move = currentPlayer.decideMove(board, isWhite);
            processMove(move, isWhite);
            startAsync();
        }
    }

    private void waitForHumanMove(boolean isWhite) {
        HumanMoveStrategy human = (HumanMoveStrategy) (isWhite ? whitePlayer : blackPlayer);
        human.setOnMoveMade(move -> {
            processMove(move, isWhite);
            startAsync();
        });
    }

    private void processMove(Move move, boolean isWhite) {
        if (!isValidMoveForPlayer(move, isWhite)) {
            System.out.println("Невалідний хід: " + move);
            return;
        }

        Position from = move.getPiece().getPosition().clone();
        Board snapshot = board.copy();

        new MoveCommand(board, move.getPiece(), move.getTo()).execute();
        pgnConverter.addMove(snapshot, from, move.clone());

        if (!isWhite) fullMoveNumber++;

        notifyUIUpdate();
    }

    private boolean isValidMoveForPlayer(Move move, boolean isWhite) {
        return move != null &&
                move.getPiece() != null &&
                move.getPiece().isWhite() == board.isWhiteTurn() &&
                board.getMoveValidator().isValidMove(move.getPiece(), move.getTo());
    }

    private void notifyUIUpdate() {
        if (onBoardUpdated != null) {
            Platform.runLater(onBoardUpdated);
        }
    }

    // === Геттери та сеттери ===

    public ObservableList<String> getMoveLog() {
        return FXCollections.observableArrayList(
                pgnConverter.getPGN().replaceAll("\\d+\\.\\s", "").trim().split("\\s+")
        );
    }

    public void setOnBoardUpdated(Runnable listener) {
        this.onBoardUpdated = listener;
    }

    public void setPGN(String pgn) {
        pgnConverter = new PGNConverter(pgn);
    }

    public String getPGN() {
        return pgnConverter.getPGN();
    }

    public void setWhitePlayer(MoveStrategy whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public void setBlackPlayer(MoveStrategy blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public HumanMoveStrategy getHumanMoveStrategy() {
        if (whitePlayer instanceof HumanMoveStrategy) return (HumanMoveStrategy) whitePlayer;
        if (blackPlayer instanceof HumanMoveStrategy) return (HumanMoveStrategy) blackPlayer;
        return null;
    }

    public Piece[][] getPieces() {
        return board.getBoard();
    }

    public Board getBoard() {
        return board;
    }
}
