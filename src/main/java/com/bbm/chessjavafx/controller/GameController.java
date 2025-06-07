package com.bbm.chessjavafx.controller;

import com.bbm.chessjavafx.model.ChessGameModel;
import com.bbm.chessjavafx.model.DatabaseManager;
import com.bbm.chessjavafx.model.Move.HumanMoveStrategy;
import com.bbm.chessjavafx.model.Move.Move;
import com.bbm.chessjavafx.model.Move.StockfishMoveStrategy;
import com.bbm.chessjavafx.model.game.ChessGame;
import com.bbm.chessjavafx.model.game.ChessPosition;
import com.bbm.chessjavafx.model.pieces.Piece;
import com.bbm.chessjavafx.model.pieces.Position;
import com.bbm.chessjavafx.services.ChessBoardRenderer;
import com.bbm.chessjavafx.services.MoveHintService;
import com.bbm.chessjavafx.services.StockfishService;
import com.bbm.chessjavafx.util.FENConverter;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class GameController {

    private final DatabaseManager db = new DatabaseManager();
    private final StockfishService stockfishService = new StockfishService();
    private final ChessBoardRenderer boardRenderer = new ChessBoardRenderer();
    private final MoveHintService hintService = new MoveHintService();
    @FXML
    public ChoiceBox<String> chessBoardType;
    @FXML
    private GridPane chessBoard;
    @FXML
    private ChoiceBox<String> modeChoice;
    @FXML
    private VBox game_setting;
    @FXML
    private VBox game_process;
    @FXML
    private Text turn_color;
    @FXML
    private Button newGame;
    @FXML
    private Slider difficulty;
    @FXML
    private Label difficultyLabel;
    @FXML
    private ListView<String> moveLogListView;
    @FXML
    private TextField gameNameField;
    @FXML
    private Button saveButton;
    private ChessGame game;
    private Piece selectedPiece;

    @FXML
    public void initialize() {
        initUIBindings();
        initChessBoard();
        loadPreviousGames();
        stockfishService.startEngine();

        modeChoice.getItems().addAll("Player vs Bot", "Player vs Player", "Bot vs Bot");
        modeChoice.setValue("Player vs Bot");
        for (ChessPosition position : ChessPosition.values()) {
            chessBoardType.getItems().add(position.name());
        }
        chessBoardType.setValue("DEFAULT");
    }

    private void initUIBindings() {
        difficulty.valueProperty().addListener((obs, oldVal, newVal) ->
                difficultyLabel.setText("Значення: " + newVal.intValue())
        );

        saveButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.setOnCloseRequest(event -> cleanup());
                    }
                });
            }
        });
    }

    private void initChessBoard() {
        boardRenderer.initializeBoard(chessBoard, this::handleCellClick);
    }

    private void loadPreviousGames() {
        db.loadAllGames().forEach(g -> {
            System.out.printf("Loaded: %s (%s)%n", g.getName(), g.getDateTime());
        });
    }

    private void cleanup() {
        if (game != null) {
            String fen = FENConverter.convertToFEN(game.getBoard());
            ChessGameModel lastGame = db.getGameByName("LastGame");
            if (lastGame != null) {
                lastGame.setFen(fen);
                lastGame.setPgn(game.getPGN());
                db.updateGame(lastGame);
            } else {
                db.saveGame(new ChessGameModel(game.getPGN(), "LastGame", fen, LocalDateTime.now()));
            }
        }
        stockfishService.stopEngine();
        game = null;
    }

    private void handleCellClick(MouseEvent event) {
        Node source = (Node) event.getSource();
        int row = GridPane.getRowIndex(source);
        int col = GridPane.getColumnIndex(source);

        if (hintService.isHintCell(source)) {
            game.getHumanMoveStrategy().notifyMoveMade(new Move(selectedPiece, new Position(row, col)));
            if (game.getBoard().isGameOver()) {
                System.out.println(game.getBoard().getGameResult());
            }
            clearSelection();
            renderBoard();
        } else {
            clearSelection();
            Piece piece = game.getPieces()[row][col];
            if (piece != null && piece.isWhite() == game.getBoard().isWhiteTurn()) {
                selectedPiece = piece;
                List<Position> validMoves = game.getBoard().getMoveValidator().getLegalMoves(piece);
                hintService.showHints(validMoves, boardRenderer.getCells());
            }
        }
    }

    private void clearSelection() {
        selectedPiece = null;
        hintService.clearHints(boardRenderer.getCells());
    }

    private void renderBoard() {
        boardRenderer.render(game.getPieces());
        moveLogListView.setItems(game.getMoveLog());
    }

    public void loadGame(ChessGameModel gameModel) {
        if (gameModel != null) {
            this.game = new ChessGame(new HumanMoveStrategy(), new StockfishMoveStrategy(stockfishService.getEngine()), ChessPosition.EMPTY);
            FENConverter.convertFromFEN(gameModel.getFen(), game.getBoard());
            game.setPGN(gameModel.getPgn());
            renderBoard();
        }
    }

    @FXML
    private void startGame() {
        if (game == null)
            game = new ChessGame(ChessPosition.EMPTY);
        String mode = modeChoice.getValue();
        int level = (int) difficulty.getValue();

        switch (mode) {
            case "Player vs Bot" -> {
                game.setWhitePlayer(new HumanMoveStrategy());
                game.setBlackPlayer(new StockfishMoveStrategy(stockfishService.getEngine()));
                stockfishService.setSkillLevel(level);
            }
            case "Player vs Player" -> {
                game.setWhitePlayer(new HumanMoveStrategy());
                game.setBlackPlayer(new HumanMoveStrategy());
            }
            case "Bot vs Bot" -> {
                game.setWhitePlayer(new StockfishMoveStrategy(stockfishService.getEngine()));
                game.setBlackPlayer(new StockfishMoveStrategy(stockfishService.getEngine()));
                stockfishService.setSkillLevel(level);
            }
        }

        game.changeBoardType(ChessPosition.valueOf(chessBoardType.getValue()));

        renderBoard();
        game_setting.setVisible(false);
        game_process.setVisible(true);
        turn_color.textProperty().bind(Bindings.when(game.getBoard().isWhiteTurnProperty()).then("Білих").otherwise("Чорних"));
        game.setOnBoardUpdated(this::renderBoard);
        game.startAsync();
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
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void newGame(ActionEvent event) {
        cleanup();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bbm/chessjavafx/board.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) newGame.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    public void onSaveClicked(ActionEvent event) {
        String name = gameNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Помилка", "Введіть назву партії.");
            return;
        }
        db.saveGame(new ChessGameModel(game.getPGN(), name, FENConverter.convertToFEN(game.getBoard()), LocalDateTime.now()));
        showAlert(Alert.AlertType.INFORMATION, "Збережено", "Партію \"" + name + "\" збережено успішно.");
    }

    @FXML
    public void copyFENToClipboard() {
        String fen = FENConverter.convertToFEN(game.getBoard());
        ClipboardContent content = new ClipboardContent();
        content.putString(fen);
        Clipboard.getSystemClipboard().setContent(content);
    }
}
