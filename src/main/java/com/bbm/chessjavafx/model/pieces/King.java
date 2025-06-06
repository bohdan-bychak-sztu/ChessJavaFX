package com.bbm.chessjavafx.model.pieces;

import com.bbm.chessjavafx.model.game.Board;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private boolean hasMoved = false;

    public King(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> moves = getPseudoLegalMoves(board);

        List<Position> validMoves = new ArrayList<>();
        for (Position move : moves) {
            Board copyBoard = board.copy();
            copyBoard.movePiece(this.clone(), move, true);
            if (!copyBoard.isKingInCheck(isWhite)) {
                validMoves.add(move);
            }
        }

        if (!hasMoved && !board.isKingInCheck(isWhite)) {
            int row = isWhite ? 7 : 0;

            if (canCastle(board, row, 7, new int[]{5, 6})) {
                validMoves.add(new Position(row, 6));
            }

            if (canCastle(board, row, 0, new int[]{1, 2, 3})) {
                validMoves.add(new Position(row, 2));
            }
        }

        return validMoves;
    }

    private boolean canCastle(Board board, int row, int rookCol, int[] emptyCols) {
        Position rookPos = new Position(row, rookCol);
        Piece rook = board.getPiece(rookPos);
        if (!(rook instanceof Rook) || ((Rook) rook).hasMoved() || rook.isWhite() != isWhite) {
            return false;
        }

        for (int col : emptyCols) {
            if (board.getPiece(new Position(row, col)) != null) {
                return false;
            }
        }

        for (int col : new int[]{4, emptyCols[emptyCols.length - 1]}) {
            Board clone = board.copy();
            Position from = new Position(row, 4);
            Position to = new Position(row, col);
            Piece copyKing = clone.getPiece(from);
            clone.setPiece(from, null);
            clone.setPiece(to, copyKing);
            if (clone.isKingInCheck(isWhite)) {
                return false;
            }
        }

        return true;
    }
    @Override
    public List<Position> getPseudoLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int[] dx = {1, 1, 1, 0, -1, -1, -1, 0};
        int[] dy = {1, 0, -1, -1, 1, 0, -1, 1};

        for (int i = 0; i < dx.length; i++) {
            Position newPos = new Position(position.getX() + dx[i], position.getY() + dy[i]);
            if (newPos.isValid()) {
                Piece target = board.getPiece(newPos);
                if (target == null || target.isWhite() != isWhite) {
                    moves.add(newPos);
                }
            }
        }

        return moves;
    }

    @Override
    public Piece clone() {
        King cloned = new King(isWhite, new Position(position.getX(), position.getY()));
        cloned.setHasMoved(this.hasMoved);
        return cloned;
    }

    @Override
    public String getFENSymbol() {
        return isWhite ? "K" : "k";
    }
    public boolean hasMoved() {
        return hasMoved;
    }
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
