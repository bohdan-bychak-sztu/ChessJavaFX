package com.bbm.chessjavafx.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:derby:chessdb;create=true";

    public DatabaseManager() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String sql = """
                CREATE TABLE ChessGames (
                    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    name VARCHAR(100) UNIQUE NOT NULL,
                    pgn CLOB,
                    fen VARCHAR(100),
                    dateTime TIMESTAMP
                )
            """;
            stmt.execute(sql);

        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                e.printStackTrace();
            }
        }
    }

    public void saveGame(ChessGameModel game) {
        String sql = "INSERT INTO ChessGames (name, pgn, fen, dateTime) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, game.getName());
            pstmt.setString(2, game.getPgn());
            pstmt.setString(3, game.getFen());
            pstmt.setTimestamp(4, Timestamp.valueOf(game.getDateTime()));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ChessGameModel> loadAllGames() {
        List<ChessGameModel> games = new ArrayList<>();
        String sql = "SELECT * FROM ChessGames ORDER BY dateTime DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String pgn = rs.getString("pgn");
                String fen = rs.getString("fen");
                LocalDateTime dt = rs.getTimestamp("dateTime").toLocalDateTime();
                games.add(new ChessGameModel(id, name, pgn, fen, dt));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    public ChessGameModel getGameById(int id) {
        String sql = "SELECT * FROM ChessGames WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String pgn = rs.getString("pgn");
                String fen = rs.getString("fen");
                LocalDateTime dt = rs.getTimestamp("dateTime").toLocalDateTime();
                return new ChessGameModel(id, name, pgn, fen, dt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ChessGameModel getGameByName(String name) {
        ChessGameModel game = null;
        String sql = "SELECT * FROM ChessGames WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String pgn = rs.getString("pgn");
                String fen = rs.getString("fen");
                LocalDateTime dt = rs.getTimestamp("dateTime").toLocalDateTime();
                game = new ChessGameModel(id, name, pgn, fen, dt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return game;
    }

    public void deleteGameById(int id) {
        String sql = "DELETE FROM ChessGames WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateGame(ChessGameModel game) {
        String sql = "UPDATE ChessGames SET name = ?, pgn = ?, fen = ?, dateTime = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, game.getName());
            pstmt.setString(2, game.getPgn());
            pstmt.setString(3, game.getFen());
            pstmt.setTimestamp(4, Timestamp.valueOf(game.getDateTime()));
            pstmt.setInt(5, game.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
