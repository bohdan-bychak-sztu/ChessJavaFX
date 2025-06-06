package com.bbm.chessjavafx.model.Move;

import com.bbm.chessjavafx.model.game.Board;

public interface MoveStrategy {
    Move decideMove(Board board, boolean isWhite);
}
