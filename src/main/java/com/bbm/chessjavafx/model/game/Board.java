package com.bbm.chessjavafx.model.game;

import com.bbm.chessjavafx.model.pieces.*;
import com.bbm.chessjavafx.services.BoardCloner;
import com.bbm.chessjavafx.services.GameStateChecker;
import com.bbm.chessjavafx.services.MoveValidator;
import com.bbm.chessjavafx.util.FENConverter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Board {
    public static final int SIZE = 8;
    private final Piece[][] board;
    private final BooleanProperty whiteTurn = new SimpleBooleanProperty(true);

    private final MoveValidator moveValidator;
    private final GameStateChecker gameStateChecker;
    private final BoardCloner boardCloner;

    public Board() {
        this(ChessPosition.DEFAULT);
    }

    public Board(boolean isInitialize) {
        board = new Piece[SIZE][SIZE];
        moveValidator = new MoveValidator(this);
        gameStateChecker = new GameStateChecker(this);
        boardCloner = new BoardCloner(this);

        if (isInitialize) {
            initializePieces(ChessPosition.DEFAULT.getFen());
        }
    }

    public Board(ChessPosition position) {
        board = new Piece[SIZE][SIZE];
        moveValidator = new MoveValidator(this);
        gameStateChecker = new GameStateChecker(this);
        boardCloner = new BoardCloner(this);

        initializePieces(position.getFen());
    }

    private void initializePieces(String fen) {
        FENConverter.convertFromFEN(fen, this);
    }

    public boolean movePiece(Piece piece, Position to) {
        return movePiece(piece, to, false);
    }

    public boolean movePiece(Piece piece, Position to, boolean simulate) {
        if (piece == null || !to.isValid()) return false;
        if (simulate) {
            simulateMove(piece, to);
            return true;
        }
        if (!moveValidator.isValidMove(piece, to)) return false;

        Position from = piece.getPosition().clone();

        if (piece instanceof King king) {
            king.setHasMoved(true);
            handleCastling(king, to);
        }

        if (piece instanceof Rook rook) {
            rook.setHasMoved(true);
        }

        setPiece(from, null);
        setPiece(to, piece);
        toggleTurn();
        return true;
    }

    private void simulateMove(Piece piece, Position to) {
        Position from = piece.getPosition();
        setPiece(from, null);
        setPiece(to, piece);
    }

    private void handleCastling(King king, Position to) {
        int row = king.isWhite() ? 7 : 0;
        if (to.equals(new Position(row, 6))) {
            moveRook(new Position(row, 7), new Position(row, 5));
        } else if (to.equals(new Position(row, 2))) {
            moveRook(new Position(row, 0), new Position(row, 3));
        }
    }

    private void moveRook(Position from, Position to) {
        Piece rook = getPiece(from);
        setPiece(from, null);
        setPiece(to, rook);
    }

    public void setPiece(Position position, Piece piece) {
        board[position.getX()][position.getY()] = piece;
        if (piece != null) {
            piece.setPosition(position);
        }
    }

    public Piece getPiece(Position position) {
        if (!position.isValid()) return null;
        return board[position.getX()][position.getY()];
    }

    public boolean isWhiteTurn() {
        return whiteTurn.get();
    }

    public void toggleTurn() {
        whiteTurn.set(!whiteTurn.get());
    }

    public boolean isGameOver() {
        return gameStateChecker.isGameOver();
    }

    public String getGameResult() {
        return gameStateChecker.getGameResult();
    }

    public BooleanProperty isWhiteTurnProperty() {
        return whiteTurn;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public Piece[][] getBoardSnapshot() {
        Piece[][] copy = new Piece[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            System.arraycopy(board[y], 0, copy[y], 0, SIZE);
        }
        return copy;
    }

    public Board copy() {
        return boardCloner.copyBoard();
    }

    public boolean movePiece(String move) {
        String from = move.substring(0, 2);
        String to = move.substring(2, 4);
        return movePiece(getPiece(new Position(from)), new Position(to));
    }

    public void setWhiteTurn(boolean w) {
        whiteTurn.set(w);
    }

    public MoveValidator getMoveValidator() {
        return moveValidator;
    }

    public GameStateChecker getGameStateChecker() {
        return gameStateChecker;
    }
}
