package com.bbm.chessjavafx.model.Move;

import com.bbm.chessjavafx.model.game.Board;

import java.util.function.Consumer;

public class HumanMoveStrategy implements MoveStrategy {
    private Consumer<Move> onMoveMade;

    public void setOnMoveMade(Consumer<Move> callback) {
        this.onMoveMade = callback;
    }

    public void notifyMoveMade(Move move) {
        System.out.println("HumanMoveStrategy notifyMoveMade: " + move);
        if (onMoveMade != null) {
            onMoveMade.accept(move);
        }
    }

    @Override
    public Move decideMove(Board board, boolean isWhiteTurn) {
        return null;
    }
}
