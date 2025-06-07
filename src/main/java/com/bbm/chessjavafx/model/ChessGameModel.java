package com.bbm.chessjavafx.model;

import java.time.LocalDateTime;

public class ChessGameModel {
    private int id;
    private String name;
    private String pgn;
    private String fen;
    private LocalDateTime dateTime;

    public ChessGameModel(int id, String name, String pgn, String fen, LocalDateTime dateTime) {
        this.id = id;
        this.name = name;
        this.pgn = pgn;
        this.fen = fen;
        this.dateTime = dateTime;
    }

    public ChessGameModel(String pgn, String name, String fen, LocalDateTime dateTime) {
        this(-1, name, pgn, fen, dateTime);
    }

    public int getId() { return id; }
    public String getPgn() { return pgn; }
    public String getName() { return name; }
    public String getFen() { return fen; }
    public LocalDateTime getDateTime() { return dateTime; }

    public void setId(int id) { this.id = id; }
    public void setPgn(String pgn) { this.pgn = pgn; }
    public void setName(String pgn) { this.name = name; }
    public void setFen(String fen) { this.fen = fen; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
}
