package com.bbm.chessjavafx.model.Move;

import com.bbm.chessjavafx.model.StockfishEngine;
import com.bbm.chessjavafx.model.game.Board;
import com.bbm.chessjavafx.model.pieces.Position;
import com.bbm.chessjavafx.util.FENConverter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockfishMoveStrategy implements MoveStrategy {
    private final StockfishEngine engine;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public StockfishMoveStrategy(StockfishEngine engine) {
        this.engine = engine;
    }

    public CompletableFuture<Move> decideMoveAsync(Board board, boolean isWhite) {
        return CompletableFuture.supplyAsync(() -> {
            String fen = FENConverter.convertToFEN(board);
            String bestMoveString = engine.getBestMove(fen, 1000);

            if (bestMoveString == null || bestMoveString.length() < 4) {
                return null;
            }

            Position from = new Position(
                    8 - Character.getNumericValue(bestMoveString.charAt(1)),
                    bestMoveString.charAt(0) - 'a'
            );
            Position to = new Position(
                    8 - Character.getNumericValue(bestMoveString.charAt(3)),
                    bestMoveString.charAt(2) - 'a'
            );


            return new Move(board.getPiece(from), to);
        }, executor);
    }

    @Override
    public Move decideMove(Board board, boolean isWhite) {
        try {
            System.out.println("Deciding move");
            return decideMoveAsync(board, isWhite).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
