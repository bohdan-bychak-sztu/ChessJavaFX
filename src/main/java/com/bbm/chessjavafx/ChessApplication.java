package com.bbm.chessjavafx;

import com.bbm.chessjavafx.model.StockfishEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ChessApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        StockfishEngine engine = new StockfishEngine();
        if (engine.startEngine("../stockfish/stockfish-windows-x86-64-avx2.exe")) {
            String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            String bestMove = engine.getBestMove(fen);
            System.out.println("Stockfish радить: " + bestMove);
            engine.stop();
        } else {
            System.out.println("Не вдалося запустити движок.");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbm/chessjavafx/menu.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Шахи");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
