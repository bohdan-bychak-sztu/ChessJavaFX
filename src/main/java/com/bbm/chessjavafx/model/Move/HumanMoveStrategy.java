package com.bbm.chessjavafx.model.Move;

import com.bbm.chessjavafx.model.game.Board;

public class HumanMoveStrategy implements MoveStrategy {
    private Move nextMove;

    public void setNextMove(Move move) {
        this.nextMove = move;
    }

    @Override
    public Move decideMove(Board board, boolean isWhiteTurn) {
        return nextMove;
    }
}
