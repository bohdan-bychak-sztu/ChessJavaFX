package com.bbm.chessjavafx.model.Move;

import com.bbm.chessjavafx.model.game.Board;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;

public class MoveCommand {
    private final Board board;
    private final Piece piece;
    private final Position from;
    private final Position to;
    private Piece captured;

    public MoveCommand(Board board, Piece piece, Position to) {
        this.board = board;
        this.piece = piece;
        this.from = piece.getPosition();
        this.to = to;
    }

    public void execute() {
        captured = board.getPiece(to);
        board.movePiece(piece, to);
    }

    public void undo() {
        board.movePiece(piece, from);
        board.setPiece(to, captured);
    }
}
