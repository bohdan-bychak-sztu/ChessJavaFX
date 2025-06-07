package com.bbm.chessjavafx.services;

import com.bbm.chessjavafx.model.StockfishEngine;

public class StockfishService {

    private final StockfishEngine engine = new StockfishEngine();

    public void startEngine() {
        try {
            engine.startEngine("../stockfish/stockfish-windows-x86-64-avx2.exe");
        } catch (Exception e) {
            System.err.println("Не вдалося запустити Stockfish.");
        }
    }

    public void stopEngine() {
        engine.stop();
    }

    public void setSkillLevel(int level) {
        engine.setSkillLevel(level);
    }

    public StockfishEngine getEngine() {
        return engine;
    }
}
