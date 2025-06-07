package com.bbm.chessjavafx.model.game;

import com.bbm.chessjavafx.model.pieces.*;
import com.bbm.chessjavafx.util.FENConverter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    public static final int SIZE = 8;
    private final Piece[][] board;
    private BooleanProperty whiteTurn = new SimpleBooleanProperty(true);

    public Board() {
        this(true);
    }

    public Board(boolean initialize) {
        board = new Piece[SIZE][SIZE];

        if (initialize) {
            initializeDefaultPieces();
        }
    }




    private void initializeDefaultPieces() {
        setPiece(new Position(0, 0), new Rook(false, new Position(0, 0)));
        setPiece(new Position(0, 1), new Knight(false, new Position(0, 1)));
        setPiece(new Position(0, 2), new Bishop(false, new Position(0, 2)));
        setPiece(new Position(0, 3), new Queen(false, new Position(0, 3)));
        setPiece(new Position(0, 4), new King(false, new Position(0, 4)));
        setPiece(new Position(0, 5), new Bishop(false, new Position(0, 5)));
        setPiece(new Position(0, 6), new Knight(false, new Position(0, 6)));
        setPiece(new Position(0, 7), new Rook(false, new Position(0, 7)));

        for (int col = 0; col < 8; col++) {
            setPiece(new Position(1, col), new Pawn(false, new Position(1, col)));
        }

        for (int col = 0; col < 8; col++) {
            setPiece(new Position(6, col), new Pawn(true, new Position(6, col)));
        }

        setPiece(new Position(7, 0), new Rook(true, new Position(7, 0)));
        setPiece(new Position(7, 1), new Knight(true, new Position(7, 1)));
        setPiece(new Position(7, 2), new Bishop(true, new Position(7, 2)));
        setPiece(new Position(7, 3), new Queen(true, new Position(7, 3)));
        setPiece(new Position(7, 4), new King(true, new Position(7, 4)));
        setPiece(new Position(7, 5), new Bishop(true, new Position(7, 5)));
        setPiece(new Position(7, 6), new Knight(true, new Position(7, 6)));
        setPiece(new Position(7, 7), new Rook(true, new Position(7, 7)));
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

    public boolean movePiece(Piece piece, Position to) {
        return movePiece(piece, to, false);
    }

    public boolean movePiece(Piece piece, Position to, boolean simulate) {
        if (piece == null || !to.isValid()) return false;
        if (simulate) {
            Position from = piece.getPosition();
            setPiece(from, null);
            setPiece(to, piece);
            return true;
        }
        if (!isValidMove(piece, to)) return false;


        Position from;
        try {
            from = piece.getPosition().clone();
        } catch (Exception ignored) {
            from = piece.getPosition();
        }

        if (piece instanceof King king) {
            king.setHasMoved(true);

            int row = king.isWhite() ? 7 : 0;
            if (to.equals(new Position(row, 6))) {
                Piece rook = getPiece(new Position(row, 7));
                setPiece(new Position(row, 7), null);
                setPiece(new Position(row, 5), rook);
            } else if (to.equals(new Position(row, 2))) {
                Piece rook = getPiece(new Position(row, 0));
                setPiece(new Position(row, 0), null);
                setPiece(new Position(row, 3), rook);
            }
        }

        if (piece instanceof Rook rook) {
            rook.setHasMoved(true);
        }

        setPiece(from, null);
        setPiece(to, piece);

        toggleTurn();

        return true;
    }

    public boolean isValidMove(Piece piece, Position to) {
        if (piece == null || !to.isValid()) return false;
        if (piece.isWhite() != whiteTurn.get()) return false;
        return getLegalMoves(piece).contains(to);
    }

    public boolean isWhiteTurn() {
        return whiteTurn.get();
    }

    public void toggleTurn() {
        whiteTurn.set(!whiteTurn.get());
    }

    public boolean isGameOver() {
        return isCheckmate(true) || isCheckmate(false) || isStalemate(true) || isStalemate(false);
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

    public List<Position> getLegalMoves(Piece piece) {
        List<Position> legalMoves = new ArrayList<>();
        for (Position move : piece.getValidMoves(this)) {
            Board clone = this.copy();

            Piece copyPiece = clone.getPiece(piece.getPosition());
            clone.movePiece(copyPiece, move, true);
            if (!clone.isKingInCheck(copyPiece.isWhite())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }


    public boolean hasAnyValidMove(boolean isWhite) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Position> validMoves = piece.getValidMoves(this);
                    for (Position to : validMoves) {
                        Board clone = this.copy();
                        Piece clonePiece = clone.getPiece(piece.getPosition());
                        clone.movePiece(clonePiece, to, true);
                        if (!clone.isKingInCheck(isWhite)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    private boolean hasKing(boolean isWhite) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece piece = board[x][y];
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isKingInCheck(boolean isWhite) {
        Position kingPos = findKingPosition(isWhite);
        if (kingPos == null) return false;

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.isWhite() != isWhite) {
                    if (piece.canAttack(kingPos, this)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Position findKingPosition(boolean isWhite) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece piece = board[x][y];
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return piece.getPosition();
                }
            }
        }
        return null;
    }

    public Piece[][] getBoardSnapshot() {
        Piece[][] copy = new Piece[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            System.arraycopy(board[y], 0, copy[y], 0, SIZE);
        }
        return copy;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public Board copy() {
        Board copy = new Board(false);

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece piece = board[x][y];
                if (piece != null) {
                    copy.setPiece(new Position(x, y), piece.clone());
                }
            }
        }
        copy.whiteTurn = this.whiteTurn;
        return copy;
    }

    public boolean movePiece(String move) {
        String from = move.substring(0, 2);
        String to = move.substring(2, 4);
        Position fromPosition = new Position(from);
        Position toPosition = new Position(to);

        movePiece(getPiece(fromPosition), toPosition);

        return true;
    }

    public BooleanProperty isWhiteTurnProperty() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean w) {
        whiteTurn.set(w);
    }
}
