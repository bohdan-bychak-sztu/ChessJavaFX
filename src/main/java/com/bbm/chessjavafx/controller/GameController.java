package com.bbm.chessjavafx.controller;

import com.bbm.chessjavafx.model.ChessGameModel;
import com.bbm.chessjavafx.model.DatabaseManager;
import com.bbm.chessjavafx.model.Move.HumanMoveStrategy;
import com.bbm.chessjavafx.model.Move.Move;
import com.bbm.chessjavafx.model.Move.StockfishMoveStrategy;
import com.bbm.chessjavafx.model.StockfishEngine;
import com.bbm.chessjavafx.model.game.ChessGame;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;
import com.bbm.chessjavafx.util.FENConverter;
import com.bbm.chessjavafx.util.ImageLoader;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final StackPane[][] cells = new StackPane[8][8];
    private final List<Position> highlightedPositions = new ArrayList<>();
    public VBox game_setting;
    public VBox game_process;
    public Text turn_color;
    public Button newGame;
    public Slider difficulty;
    public Label difficultyLabel;
    public ListView moveLogListView;
    public TextField gameNameField;
    public Button saveButton;
    StockfishEngine engine = new StockfishEngine();
    DatabaseManager db = new DatabaseManager();
    @FXML
    private GridPane chessBoard;
    @FXML
    private ChoiceBox<String> modeChoice;
    private ChessGame game;
    private Piece selectedPiece;

    public void loadGame(ChessGameModel game) {
        System.out.println("Loading game " + game.getName());
        System.out.println("FEN: " + game.getFen());
        this.game = new ChessGame("saved");
        FENConverter.convertFromFEN(game.getFen(), this.game.getBoard());
        this.game.setPGN(game.getPgn());
        setPieces();
    }

    @FXML
    public void initialize() {
        saveButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Stage stage = (Stage) newWindow;
                        stage.setOnCloseRequest(event -> {
                            cleanup();
                        });
                    }
                });
            }
        });

        for (ChessGameModel g : db.loadAllGames()) {
            System.out.println("ID: " + g.getId());
            System.out.println("Name: " + g.getName());
            System.out.println("PGN: " + g.getPgn());
            System.out.println("FEN: " + g.getFen());
            System.out.println("Date: " + g.getDateTime());
            System.out.println("-----");
        }

        try {
            engine.startEngine("../stockfish/stockfish-windows-x86-64-avx2.exe");
        } catch (Exception ignored) {
        }

        modeChoice.setValue("Player vs Bot");
        difficulty.valueProperty().addListener((obs, oldVal, newVal) -> {
            difficultyLabel.setText("Значення: " + newVal.intValue());
        });
        chessBoard.addEventHandler(MouseEvent.MOUSE_CLICKED, this::HandleBoardClick);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(60, 60);
                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
                cell.setStyle("-fx-background-color: " + color + ";");
                cell.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleCellClick);
                chessBoard.add(cell, col, row);
                cells[row][col] = cell;
            }
        }
    }

    private void HandleBoardClick(MouseEvent mouseEvent) {
    }

    private void handleCellClick(MouseEvent event) {
        doMove((StackPane) event.getSource());
        clearHints();

        int col = GridPane.getColumnIndex((Node) event.getSource());
        int row = GridPane.getRowIndex((Node) event.getSource());
        Piece[][] pieces = game.getPieces();


        if (pieces[row][col] != null && pieces[row][col].isWhite() == game.getBoard().isWhiteTurn()) {
            System.out.println(game.getPieces()[row][col].getName());

            selectedPiece = pieces[row][col];

            List<Position> validMoves = game.getBoard().getLegalMoves(pieces[row][col]);
            for (Position move : validMoves) {
                ImageView hint = new ImageView(ImageLoader.loadImage("images/pieces/hint.png"));
                hint.setUserData("hint");
                cells[move.getX()][move.getY()].getChildren().add(hint);
                highlightedPositions.add(move);
            }
        }
    }

    private void doMove(StackPane cell) {
        boolean hasHintImage = cell.getChildren().stream().anyMatch(node -> node instanceof ImageView && "hint".equals(node.getUserData()));
        if (!hasHintImage) {
            return;
        }

        int col = GridPane.getColumnIndex(cell);
        int row = GridPane.getRowIndex(cell);
        Piece[][] pieces = game.getPieces();
        Move move = new Move(selectedPiece, new Position(row, col));
        game.getHumanMoveStrategy().notifyMoveMade(new Move(selectedPiece, new Position(row, col)));

        if (game.getBoard().isGameOver()) {
            System.out.println(game.getBoard().getGameResult());
        }
    }

    private void clearHints() {
        selectedPiece = null;
        for (Position pos : highlightedPositions) {
            StackPane cell = cells[pos.getX()][pos.getY()];
            cell.getChildren().removeIf(node -> node instanceof ImageView && "hint".equals(node.getUserData()));
        }
        highlightedPositions.clear();
    }


    @FXML
    private void goBackToMenu() {
        cleanup();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbm/chessjavafx/menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) chessBoard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanup() {
        if (game != null) {

            if (db.getGameByName("LastGame") != null) {
                ChessGameModel game = db.getGameByName("LastGame");
                game.setFen(FENConverter.convertToFEN(this.game.getBoard()));
                game.setPgn(this.game.getPGN());
                db.updateGame(game);
            }
            else {
                ChessGameModel game = new ChessGameModel(
                        this.game.getPGN(),
                        "LastGame",
                        FENConverter.convertToFEN(this.game.getBoard()),
                        LocalDateTime.now()
                );
                db.saveGame(game);
            }
        }
        engine.stop();
        game = null;
    }

    @FXML
    private void startGame() {
        try {
            int difficultyValue = (int) difficulty.getValue();
            String mode = modeChoice.getValue();

            if (this.game == null)
                this.game = new ChessGame("default");

            switch (mode) {
                case "Player vs Bot":
                    this.game.setWhitePlayer(new HumanMoveStrategy());
                    this.game.setBlackPlayer((new StockfishMoveStrategy(engine)));
                    engine.setSkillLevel(difficultyValue);
                    break;
                case "Player vs Player":
                    this.game.setWhitePlayer(new HumanMoveStrategy());
                    this.game.setBlackPlayer((new HumanMoveStrategy()));
                    break;
                case "Bot vs Bot":
                    this.game.setWhitePlayer(new StockfishMoveStrategy(engine));
                    this.game.setBlackPlayer((new StockfishMoveStrategy(engine)));
                    engine.setSkillLevel(difficultyValue);
                    break;
                default:
                    this.game.setWhitePlayer(new HumanMoveStrategy());
                    this.game.setBlackPlayer((new StockfishMoveStrategy(engine)));
                    break;
            }

            setPieces();
            game_setting.visibleProperty().set(false);
            game_process.visibleProperty().set(true);

            turn_color.textProperty().bind(Bindings.when(game.getBoard().isWhiteTurnProperty()).then("Білих").otherwise("Чорних"));
            game.setOnBoardUpdated(this::setPieces);
            game.startAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPieces() {
        System.out.println("Ходять " + (game.getBoard().isWhiteTurn() ? "білі" : "чорні"));
        moveLogListView.setItems(game.getMoveLog());


        Piece[][] pieces = game.getPieces();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                cells[row][col].getChildren().clear();
                if (pieces[row][col] != null) {
                    cells[row][col].getChildren().clear();
                    cells[row][col].getChildren().add(new ImageView(ImageLoader.loadImage("images/pieces/" + (pieces[row][col].isWhite() ? "w" : "b") + (pieces[row][col].getFENSymbol()) + ".png")));
                }
            }
        }
    }

    @FXML
    private void copyFENToClipboard() {
        String fen = FENConverter.convertToFEN(game.getBoard());
        ClipboardContent content = new ClipboardContent();
        content.putString(fen);
        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML
    public void newGame(ActionEvent actionEvent) {
        try {
            cleanup();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbm/chessjavafx/board.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) newGame.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSaveClicked(ActionEvent event) {
        String gameName = gameNameField.getText().trim();

        if (gameName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Помилка", "Введіть назву партії.");
            return;
        }

        ChessGameModel game = new ChessGameModel(
                this.game.getPGN(),
                gameNameField.getText(),
                FENConverter.convertToFEN(this.game.getBoard()),
                LocalDateTime.now()
        );

        db.saveGame(game);

        showAlert(Alert.AlertType.INFORMATION, "Збережено", "Партію \"" + gameName + "\" збережено успішно.");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}