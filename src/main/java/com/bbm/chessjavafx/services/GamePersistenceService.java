package com.bbm.chessjavafx.services;

import com.bbm.chessjavafx.model.ChessGameModel;
import com.bbm.chessjavafx.model.DatabaseManager;
import com.bbm.chessjavafx.model.game.ChessGame;
import com.bbm.chessjavafx.util.FENConverter;

import java.time.LocalDateTime;

public class GamePersistenceService {
    private final DatabaseManager db;

    public GamePersistenceService(DatabaseManager db) {
        this.db = db;
    }

    public void saveLastGame(ChessGame game) {
        ChessGameModel model = db.getGameByName("LastGame");
        String fen = FENConverter.convertToFEN(game.getBoard());
        String pgn = game.getPGN();

        if (model != null) {
            model.setFen(fen);
            model.setPgn(pgn);
            db.updateGame(model);
        } else {
            model = new ChessGameModel(pgn, "LastGame", fen, LocalDateTime.now());
            db.saveGame(model);
        }
    }
}
