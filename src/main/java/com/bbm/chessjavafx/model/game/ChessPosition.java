package com.bbm.chessjavafx.model.game;

public enum ChessPosition {
    DEFAULT("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"),
    EMPTY("8/8/8/8/8/8/8/8 w - - 0 1"),
    WITHOUTPAWNS("rnbqkbnr/8/8/8/8/8/8/RNBQKBNR w KQkq - 0 1");

    private final String fen;

    ChessPosition(String fen) {
        this.fen = fen;
    }

    public String getFen() {
        return fen;
    }
}
