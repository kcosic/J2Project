package main.hr.kcosic.project.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import main.hr.kcosic.project.models.*;
import main.hr.kcosic.project.models.enums.DataType;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.models.exceptions.BoardException;
import main.hr.kcosic.project.models.exceptions.EndOfBoardException;
import main.hr.kcosic.project.models.exceptions.SettingsException;
import main.hr.kcosic.project.utils.*;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import javafx.scene.canvas.*;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.List;

public class BoardController extends MyController implements Initializable {

    @FXML
    public GridPane gpBoard;
    @FXML
    public ListView<String> lvChatLog;
    @FXML
    public Button btnRoll;
    @FXML
    public TextField tfChatInput;
    @FXML
    public Label lblDiceResult;
    @FXML
    public Label lblCurrentPlayerName;
    @FXML
    public StackPane spMainStack;
    @FXML
    public Canvas cBoard;

    private final ObservableList<String> chat = FXCollections.observableArrayList();
    public MenuItem miAbortGame;

    protected ListProperty<String> listProperty = new SimpleListProperty<>();

    private Player currentPlayer;
    private List<Tile> tiles = new ArrayList<>();
    private int dice;

    private final Color borderColor = Color.SADDLEBROWN;
    private final Color boardBackgroundColor = Color.PAPAYAWHIP;
    private Thread readThread;
    private Socket clientSocket;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initializeComponents();
            initializeListeners();
            areSettingsEmpty();
            createBoard();
            if(isHotseatOrHost()){
                addSnakesAndLadders();
            }
            redraw();
        } catch (SettingsException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isHotseatOrHost() {
        LogUtils.logInfo(((getIsOverNetworkSetting() && getIsCurrentPlayerHostSetting()) || !getIsOverNetworkSetting())+"");
        return (getIsOverNetworkSetting() && getIsCurrentPlayerHostSetting()) || !getIsOverNetworkSetting();
    }

    private void initializeListeners() {

        gpBoard.widthProperty().addListener((obs, oldVal, newVal) -> cBoard.setWidth(newVal.doubleValue()));
        gpBoard.heightProperty().addListener((obs, oldVal, newVal) -> cBoard.setHeight(newVal.doubleValue()));

        cBoard.widthProperty().addListener((obs, oldVal, newVal) -> {
            cBoard.getGraphicsContext2D().clearRect(0, 0, oldVal.doubleValue(), cBoard.getHeight());
            cBoard.setWidth(newVal.doubleValue());
            redraw();
        });

        cBoard.heightProperty().addListener((obs, oldVal, newVal) -> {
            cBoard.getGraphicsContext2D().clearRect(0, 0, cBoard.getWidth(), oldVal.doubleValue());
            cBoard.setHeight(newVal.doubleValue());

            redraw();
        });

        if(getIsOverNetworkSetting()){
            clientSocket = NetworkUtils.getSocket();
            readThread = new Thread(()->{
                var exit = false;
                while(!exit) {
                    try {
                        var data = (DataWrapper)(new ObjectInputStream(clientSocket.getInputStream())).readObject();
                        switch (data.getType()){
                            case MESSAGE -> Platform.runLater(()->crunchMessage((String)data.getData()));
                            case GAME_STATE -> Platform.runLater(()->crunchNewGameState((GameState)data.getData()));
                            case BOARD -> Platform.runLater(()-> copyBoard((Board)data.getData()));
                            default -> throw new InvalidObjectException("Wrong data");
                        }



                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });
            readThread.start();
        }
    }

    private void copyBoard(Board data) {
        gpBoard = data.getGpBoard();
        tiles = data.getTiles();
    }

    private void crunchNewGameState(GameState data) {
        LogUtils.logInfo(new Gson().toJson(data));
    }

    private void crunchMessage(String data) {
        LogUtils.logInfo(data);
    }

    private void initializeComponents() {
        lvChatLog.itemsProperty().bind(listProperty);
        listProperty.set(chat);
        lvChatLog.setCellFactory(list -> {
            ListCell cell = new ListCell() {
                @Override
                public void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setWrapText(true);
                        setPrefWidth(10);
                        setText(item.toString());
                    }
                }

            };

            return cell;
        });
        clearTextField();

    }

    public void redraw() {
            Platform.runLater(() -> {
                drawSnakes();
                drawLadders();
            });
    }


    /**
     * Catches to see if user has pressed enter to submit a message.
     *
     * @param keyEvent keyboard event.
     */
    @FXML
    public void sendMessage(KeyEvent keyEvent) {
        if (isEnter(keyEvent)) {
            sendMessage();
            clearTextField();
        }
    }

    /**
     * Generates snakes and ladders.
     */
    private void addSnakesAndLadders() {
        generateSnakes();
        generateLadders();
    }

    /**
     * Generates ladders depending on number of ladders that are set up in settings object.
     */
    private void generateLadders() {
        for (int numberOfLaddersGenerated = 0; numberOfLaddersGenerated < getNumberOfLaddersSetting(); ) {
            int startTileId = (int) ((Math.random() * Math.random()) * 1000000 % getNumberOfTilesSetting());
            int endTileId = (int) (Math.random() * 1000000 % getNumberOfTilesSetting());

            if (startTileId < getNumberOfTilesSetting() - 10 && endTileId > startTileId && notInSameOrNextRow(startTileId, endTileId) && notOnEdgeOfMap(startTileId) && notOnEdgeOfMap(endTileId)) {

                Tile startTile = tiles.get(startTileId);
                Tile endTile = tiles.get(endTileId);

                if (startTile.hasNoShortcuts() && endTile.hasNoShortcuts()) {
                    startTile.setLadderStartId(numberOfLaddersGenerated);
                    endTile.setLadderEndId(numberOfLaddersGenerated);
                    numberOfLaddersGenerated++;
                }
            }
        }


        for (Tile tile : tiles) {
            if (tile.getLadderEndId() != null) {
                Label ladderLabel = new Label();

                ladderLabel.setText(tile.getLadderEndId() + "");
                ladderLabel.setStyle("-fx-text-fill: #774b03;");
                GridPane tileGridPane = (GridPane) getNodeFromGridPane(gpBoard, tile.getColumnIndex(), tile.getRowIndex());
                if(tileGridPane != null){
                    tileGridPane.add(ladderLabel, Ladder.getLadderEndColumn(), Ladder.getLadderEndRow());
                }
            } else if (tile.getLadderStartId() != null) {
                Label ladderLabel = new Label();
                ladderLabel.setText(tile.getLadderStartId() + "");
                ladderLabel.setStyle("-fx-text-fill: #774b03;");
                GridPane tileGridPane = (GridPane) getNodeFromGridPane(gpBoard, tile.getColumnIndex(), tile.getRowIndex());
                if(tileGridPane != null){
                    tileGridPane.add(ladderLabel, Ladder.getLadderStartColumn(), Ladder.getLadderStartRow());
                }
            }
        }
    }

    /**
     * Generates snakes depending on number of snakes that are set up in settings object.
     */
    private void generateSnakes() {
        for (int numberOfSnakesGenerated = 0; numberOfSnakesGenerated < getNumberOfSnakesSetting(); ) {

            int startTileId = (int) ((Math.random() * Math.random()) * 1000000 % getNumberOfTilesSetting());
            int endTileId = (int) (Math.random() * 1000000 % getNumberOfTilesSetting());

            if (startTileId >= 10 && endTileId < startTileId && notInSameOrNextRow(startTileId, endTileId) && notOnEdgeOfMap(startTileId) && notOnEdgeOfMap(endTileId)) {
                Tile startTile = tiles.get(startTileId);
                Tile endTile = tiles.get(endTileId);

                if (startTile.hasNoShortcuts() && endTile.hasNoShortcuts()) {
                    startTile.setSnakeStartId(numberOfSnakesGenerated);
                    endTile.setSnakeEndId(numberOfSnakesGenerated);
                    numberOfSnakesGenerated++;
                }
            }
        }

        for (Tile tile : tiles) {
            if (tile.getSnakeEndId() != null) {
                Label snakeLabel = new Label();
                snakeLabel.setText("");
                snakeLabel.setText(tile.getSnakeEndId()+"");
                snakeLabel.setStyle("-fx-text-fill: #9500c6;");
                GridPane tileGridPane = (GridPane) getNodeFromGridPane(gpBoard, tile.getColumnIndex(), tile.getRowIndex());
                if(tileGridPane != null){
                    tileGridPane.add(snakeLabel, Snake.getSnakeEndColumn(), Snake.getSnakeEndRow());
                }
            } else if (tile.getSnakeStartId() != null) {
                Label snakeLabel = new Label();
                snakeLabel.setText("");
                snakeLabel.setText(tile.getSnakeStartId()+"");
                snakeLabel.setStyle("-fx-text-fill: #9500c6;");
                GridPane tileGridPane = (GridPane) getNodeFromGridPane(gpBoard, tile.getColumnIndex(), tile.getRowIndex());
                if(tileGridPane != null){
                    tileGridPane.add(snakeLabel, Snake.getSnakeStartColumn(), Snake.getSnakeStartRow());
                }
            }
        }

    }

    /**
     * Checks if two tile IDs are in the same row.
     *
     * @param tile1 first tile
     * @param tile2 second tile
     * @return boolean. True if not in the same row, false if they are.
     */
    private boolean notInSameOrNextRow(int tile1, int tile2) {
        return tile1 / 10 != tile2 / 10
                && (tile1 / 10) + 1 != (tile2 / 10)
                && (tile1 / 10) - 1 != (tile2 / 10)
                && (tile2 / 10) + 1 != (tile1 / 10)
                && (tile2 / 10) - 1 != (tile1 / 10);
    }

    /**
     * Checks if two tile IDs are in the same row.
     *
     * @param tile tile
     * @return boolean. True if not on the edge of the board.
     */
    private boolean notOnEdgeOfMap(int tile) {
        return tile % 10 != 0 &&
                tile%10 != 1;
    }

    /**
     * Checks to see if settings object is null or empty.
     *
     * @throws SettingsException if settings are null or empty.
     */
    private void areSettingsEmpty() throws SettingsException {
        if (this.settings == null || this.settings.isEmpty()) {
            throw new SettingsException("Settings parameter is empty.");
        }
    }

    @FXML
    public void rollDice() throws BoardException {
        dice = (((int) (Math.random() * 100)) % 6) + 1;
        lblDiceResult.setText(dice + "");

        try {
            movePlayerNTiles();
        } catch (EndOfBoardException e) {
            // if(!getIsHardGameSetting()){
            finishGame();
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }

        switchPlayers();
    }

    private void finishGame() {
        MessageUtils.showMessage("Game finished", currentPlayer.getName().concat(" won the game!"), "Congratulations!", Alert.AlertType.INFORMATION);
    }

    /**
     * Logic for switching players after moving. It iterates through players by their ID. When it detects that last player is current player, it switches back to the first one.
     */
    private void switchPlayers() {
        int nextPlayerId = getNextPLayerId();

        if (getIsOverNetworkSetting()) {
            //network logic

        } else {
            //hot seat logic
            boolean breakLoop = false;
            for (Tile tile : tiles) {
                if (!tile.getPlayersOnTile().isEmpty()) {
                    for (Player player : tile.getPlayersOnTile()) {
                        if (player.getId() == nextPlayerId) {
                            setPlayerAsCurrent(player);
                            breakLoop = true;
                            break;
                        }
                    }
                }
                if (breakLoop) {
                    break;
                }
            }

        }

    }

    /**
     * Retrieves next player ID from a tile array.
     *
     * @return ID of next player.
     */
    private int getNextPLayerId() {
        if (currentPlayer.getId() == getNumberOfPlayersSetting() - 1) {
            return 0;
        } else {

            for (Tile tile : tiles) {
                if (!tile.getPlayersOnTile().isEmpty()) {
                    for (Player player : tile.getPlayersOnTile()) {
                        if (player.getId() == currentPlayer.getId() + 1) {
                            return player.getId();
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Player wasn't found");
    }

    /**
     * Moves player forward by a number that is rolled on a dice. If snake is encountered, player is lowered to snake end. If ladder is encountered, player is risen to ladder end.
     *
     * @throws BoardException      is thrown when invalid state has been detected that is connected to the board state.
     * @throws EndOfBoardException is thrown when player is trying to move out of bounds of the board.
     */
    private void movePlayerNTiles() throws BoardException, EndOfBoardException, IOException {
        //TODO original player position is not removed, move player to another Tile object and remove him from currentPlayer variable
        Tile currentTile = null;
        for (Tile tile : tiles) {
            if (!tile.getPlayersOnTile().isEmpty() && tile.getPlayersOnTile().contains(currentPlayer)) {
                currentTile = tile;
                break;
            }
        }

        if (currentTile == null) {
            throw new BoardException("Can't find current tile");
        }

        GridPane currentTileGridPane = (GridPane) getNodeFromGridPane(gpBoard, currentTile.getColumnIndex(), currentTile.getRowIndex());

        if (currentTileGridPane == null) {
            throw new BoardException("Can't find grid pane on current tile");
        }
        var currentPlayerPosition = ((VBox) getNodeFromGridPane(currentTileGridPane, currentPlayer.getPlayerColumn(), currentPlayer.getPlayerRow()));

        if(currentPlayerPosition != null){
            currentPlayerPosition.getChildren().clear();
        }

        currentTile.removePlayerFromTile(currentPlayer);

        Tile nextTile = null;
        for (Tile tile : tiles) {
            if (tile.getId() == currentTile.getId() + dice) {
                nextTile = tile;
                break;
            }
        }

        if (nextTile == null) {
            if (currentTile.getId() + dice > getNumberOfTilesSetting()) {
                throw new EndOfBoardException("Player ".concat(currentPlayer.getName()).concat(" can't progress because dice number goes over total board tiles."));
            }
            throw new BoardException("Can't find next tile");
        }

        if (nextTile.getLadderStartId() != null) {
            for (Tile tile : tiles) {
                if (Objects.equals(tile.getLadderEndId(), nextTile.getLadderStartId())) {
                    nextTile = tile;
                    break;
                }
            }
        } else if (nextTile.getSnakeStartId() != null) {
            for (Tile tile : tiles) {
                if (Objects.equals(tile.getSnakeStartId(), nextTile.getSnakeEndId())) {
                    nextTile = tile;
                    break;
                }
            }
        }
        GridPane nextTileGridPane = (GridPane) getNodeFromGridPane(gpBoard, nextTile.getColumnIndex(), nextTile.getRowIndex());

        if (nextTileGridPane == null) {
            throw new BoardException("Can't find grid pane on next tile");
        }
        VBox nextTilePlayer = ((VBox) getNodeFromGridPane(nextTileGridPane, currentPlayer.getPlayerColumn(), currentPlayer.getPlayerRow()));

        var playerIcon = GlyphsDude.createIcon(FontAwesomeIcons.USER);
        playerIcon.setStyle("-fx-fill: " + currentPlayer.getColor() + ";");
        playerIcon.setStyle("-fx-font-family: FontAwesome" );


        if (nextTilePlayer != null) {
            nextTilePlayer.getChildren().add(playerIcon);
        }

        var nextTilePlayerLabel = new Label(currentPlayer.getName());
        nextTilePlayerLabel.setWrapText(true);
        nextTilePlayerLabel.setStyle("-fx-text-fill: " + currentPlayer.getColor() + ";");
        if (nextTilePlayer != null) {
            nextTilePlayer.getChildren().add(nextTilePlayerLabel);
        }
        if (nextTilePlayer != null) {
            nextTilePlayer.setAlignment(Pos.CENTER);
        }

        nextTile.addPlayerToTile(currentPlayer);

        if (nextTile.getId() == getNumberOfTilesSetting() - 1) {
            finishGame();
        }

        addToChat(currentPlayer.getName() + " R: " + dice + ", " + (currentTile.getId() + 1) + " -> " + (nextTile.getId() + 1));
    }

    private void addToChat(String text) throws IOException {
        chat.add(text);
        if(getIsOverNetworkSetting()){
            NetworkUtils.sendData(new DataWrapper(DataType.MESSAGE, text));
        }

    }

    /**
     * Generates initial board state with initial player positions.
     *
     * @throws SettingsException is thrown when any of the crucial settings are missing.
     */
    private void createBoard() throws SettingsException, IOException {

            if (!settings.containsKey(SettingsEnum.NUMBER_OF_TILES)) {
                throw new SettingsException("Number of tiles parameter is missing.");
            }
            int numberOfTiles = getNumberOfTilesSetting();
            int numberOfPlayers = getNumberOfPlayersSetting();

            for (int i = 0; i < numberOfTiles; i++) {
                Tile tile = new Tile(i);
                int row = calculateRow(numberOfTiles, i);
                int column = calculateColumn(numberOfTiles, i);

                GridPane tileGridPane = new GridPane();

                Label label = new Label();
                label.setText((i + 1) + "");

                tileGridPane.add(label, 1, 1);
                if (i == 0) {
                    tile.setPlayersOnTile(setPlayersOnATile(numberOfPlayers));

                    for (int playerIndex = 0; playerIndex < numberOfPlayers; playerIndex++) {
                        Player player = tile.getPlayersOnTile().get(playerIndex);
                        switch (player.getId()) {
                            case 0 -> {
                                var playerIcon = GlyphsDude.createIcon(FontAwesomeIcons.USER);

                                Label player1Label = new Label();
                                player1Label.setText(player.getName());
                                player1Label.setWrapText(true);
                                player1Label.setStyle("-fx-text-fill: " + player.getColor() + ";");

                                playerIcon.setStyle("-fx-fill: " + player.getColor() + ";");
                                playerIcon.setStyle("-fx-font-family: FontAwesome" );

                                VBox player1 = new VBox();
                                player1.getChildren().add(playerIcon);
                                player1.getChildren().add(player1Label);

                                player1.setAlignment(Pos.CENTER);

                                tileGridPane.add(player1, 0, 1);
                                player.setPlayerColumn(0);
                                player.setPlayerRow(1);
                                setPlayerAsCurrent(player);
                            }
                            case 1 -> {
                                var playerIcon = GlyphsDude.createIcon(FontAwesomeIcons.USER);

                                Label player2Label = new Label();
                                player2Label.setText(player.getName());
                                player2Label.setWrapText(true);
                                player2Label.setStyle("-fx-text-fill: " + player.getColor() + ";");

                                playerIcon.setStyle("-fx-fill: " + player.getColor() + ";");
                                playerIcon.setStyle("-fx-font-family: FontAwesome" );

                                VBox player2 = new VBox();
                                player2.getChildren().add(playerIcon);
                                player2.getChildren().add(player2Label);

                                player2.setAlignment(Pos.CENTER);

                                tileGridPane.add(player2, 2, 1);
                                player.setPlayerColumn(2);
                                player.setPlayerRow(1);
                            }
                            case 2 -> {
                                var playerIcon = GlyphsDude.createIcon(FontAwesomeIcons.USER);

                                Label player3Label = new Label();
                                player3Label.setText(player.getName());
                                player3Label.setWrapText(true);
                                player3Label.setStyle("-fx-text-fill: " + player.getColor() + ";");

                                playerIcon.setStyle("-fx-fill: " + player.getColor() + ";");
                                playerIcon.setStyle("-fx-font-family: FontAwesome" );

                                VBox player3 = new VBox();
                                player3.getChildren().add(playerIcon);
                                player3.getChildren().add(player3Label);

                                player3.setAlignment(Pos.CENTER);

                                tileGridPane.add(player3, 1, 0);
                                player.setPlayerColumn(1);
                                player.setPlayerRow(0);
                            }
                            case 3 -> {
                                var playerIcon = GlyphsDude.createIcon(FontAwesomeIcons.USER);

                                Label player4Label = new Label();
                                player4Label.setText(player.getName());
                                player4Label.setWrapText(true);
                                player4Label.setStyle("-fx-text-fill: " + player.getColor() + ";");

                                playerIcon.setStyle("-fx-fill: " + player.getColor() + ";");
                                playerIcon.setStyle("-fx-font-family: FontAwesome" );

                                VBox player4 = new VBox();
                                player4.getChildren().add(playerIcon);
                                player4.getChildren().add(player4Label);

                                player4.setAlignment(Pos.CENTER);

                                tileGridPane.add(player4, 1, 2);
                                player.setPlayerColumn(1);
                                player.setPlayerRow(2);
                            }
                        }
                    }

                } else {
                    tileGridPane.add(new VBox(), 0, 1);
                    tileGridPane.add(new VBox(), 2, 1);
                    tileGridPane.add(new VBox(), 1, 0);
                    tileGridPane.add(new VBox(), 1, 2);
                }
                for (int tilePanelFormatter = 0; tilePanelFormatter < 3; tilePanelFormatter++) {
                    RowConstraints rc = new RowConstraints();
                    rc.setVgrow(Priority.SOMETIMES);
                    rc.setValignment(VPos.CENTER);
                    tileGridPane.getRowConstraints().add(rc);

                    ColumnConstraints cc = new ColumnConstraints();
                    cc.setHgrow(Priority.SOMETIMES);
                    cc.setHalignment(HPos.CENTER);
                    tileGridPane.getColumnConstraints().add(cc);
                }

                tile.setRowIndex(row);
                tile.setColumnIndex(column);

                tileGridPane = addBackground(tileGridPane, boardBackgroundColor);
                tileGridPane = calculateBorders(tileGridPane, column, row, tile.getId(), numberOfTiles);

                tiles.add(tile);
                gpBoard.add(tileGridPane, column, row);
            }

            for (int i = 0; i < numberOfTiles / 10; i++) {
                RowConstraints rc = new RowConstraints();
                rc.setVgrow(Priority.SOMETIMES);
                rc.setPercentHeight(100);
                gpBoard.getRowConstraints().add(rc);
            }

            for (int i = 0; i < 10; i++) {
                ColumnConstraints cc = new ColumnConstraints();
                cc.setHgrow(Priority.SOMETIMES);
                cc.setPercentWidth(100);
                gpBoard.getColumnConstraints().add(cc);
            }


            if(getIsOverNetworkSetting() && getIsCurrentPlayerHostSetting()){
                NetworkUtils.sendData(new DataWrapper(
                        DataType.BOARD,
                        new Board(
                                tiles,
                                null,
                                gpBoard
                        )
                ));
            }


    }

    /**
     * Calculates and assigns borders to passed tile.
     *
     * @param pane GridPane object that needs a border
     * @param column Column id of the passed GridPane object
     * @param row Row ID of the passed GridPane object
     * @param id ID of the passed GridPane object
     * @param maxTiles Maximum number of tiles on the board
     * @return GridPane object with assigned borders
     */
    private GridPane calculateBorders(GridPane pane, int column, int row, int id, int maxTiles) {
        if (id == 0) {
            pane = addSolidBorder(pane, borderColor, borderColor, null, borderColor, 0);
        }
        else if ((maxTiles / 10) % 2 == 0) {
            //last tile
            if (id == maxTiles - 1) {
                pane = addSolidBorder(pane, borderColor, borderColor, null, borderColor, 0);
            }
            //right column
            else if (column == 9) {
                //even rows
                if (row % 2 == 0) {
                    pane = addSolidBorder(pane, borderColor, null, borderColor, null, 0);
                }
                //odd rows
                else {
                    pane = addSolidBorder(pane, null, borderColor, borderColor, null, 0);
                }
            }
            //left column
            else if (column == 0) {
                //even rows
                if (row % 2 == 0) {
                    pane = addSolidBorder(pane, null, borderColor, null, borderColor, 0);
                }
                //odd rows
                else {
                    pane = addSolidBorder(pane, borderColor, null, null, borderColor, 0);

                }
            } else {
                pane = addSolidBorder(pane, borderColor, borderColor, null, null, 0);
            }
        }
        else {
            //last tile
            if (id == maxTiles - 1) {
                pane = addSolidBorder(pane, borderColor, borderColor, borderColor, null, 0);
            }
            //right column
            else if (column == 9) {

                //even rows
                if (row % 2 == 0) {
                    pane = addSolidBorder(pane, null, borderColor, borderColor, null, 0);
                }
                //odd rows
                else {
                    pane = addSolidBorder(pane, borderColor, null, borderColor, null, 0);
                }
            }
            //left column
            else if (column == 0) {
                //even rows
                if (row % 2 == 0) {
                    pane = addSolidBorder(pane, borderColor, null, null, borderColor, 0);
                }
                //odd rows
                else {
                    pane = addSolidBorder(pane, null, borderColor, null, borderColor, 0);
                }

            }
            //in between outermost columns
            else {
                pane = addSolidBorder(pane, borderColor, borderColor, null, null, 0);
            }

        }


        return pane;
    }

    /**
     * Creates player object dependant on number of players that is chosen.
     *
     * @param numberOfPlayers number of players that want to play. Minimum 2, maximum 4.
     * @return ArrayList of Players.
     */
    private ArrayList<Player> setPlayersOnATile(int numberOfPlayers) {
        if(getIsOverNetworkSetting() && getIsCurrentPlayerHostSetting()){
            return new ArrayList<>(getCurrentGamePlayersSetting());
        }
        else{
            if (numberOfPlayers == 2) {
                return new ArrayList<>() {
                    {
                        add(new Player(0, "P 1", "blue"));
                        add(new Player(1, "P 2", "red"));
                    }
                };

            } else if (numberOfPlayers == 3) {
                return new ArrayList<>() {
                    {
                        add(new Player(0, "P 1", "blue"));
                        add(new Player(1, "P 2", "red"));
                        add(new Player(2, "P 3", "green"));
                    }
                };
            } else if (numberOfPlayers == 4) {
                return new ArrayList<>() {
                    {
                        add(new Player(0, "P 1", "blue"));
                        add(new Player(1, "P 2", "red"));
                        add(new Player(2, "P 3", "green"));
                        add(new Player(3, "P 4", "#b7b219"));
                    }
                };
            } else {
                throw new RuntimeException("Invalid number of players");
            }
        }

    }

    private List<Player> getCurrentGamePlayersSetting() {
        Type listType = new TypeToken<List<Player>>() {}.getType();
        LogUtils.logInfo(settings.get(SettingsEnum.NUMBER_OF_PLAYERS).toString());
        return new Gson().fromJson(settings.get(SettingsEnum.NUMBER_OF_PLAYERS).toString(),listType);
    }

    /**
     * Retrieves a cell from a GridPane object.
     *
     * @param gridPane a GridPane that is searched.
     * @param col      column index of the cell that you want to retrieve.
     * @param row      row index of the cell that you want to retrieve.
     * @return Node object that is in that cell.
     */
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null && GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    /**
     * Calculates row number dependant on the current tile index.
     *
     * @param numberOfTiles overall number of tiles on the board.
     * @param currentTile   index of the current tile.
     * @return row index of that tile.
     * @throws SettingsException is thrown when overall number of tiles is not divisible by 10.
     */
    private int calculateRow(int numberOfTiles, int currentTile) throws SettingsException {
        if (numberOfTiles % 10 != 0) {
            throw new SettingsException("Invalid number of tiles. It must be divisible by 10.");
        }
        currentTile++;
        int rowCount = (numberOfTiles / 10) - 1;
        int lowest = 1;
        int highest = 11;
        int currentRow = rowCount;
        while (lowest != rowCount) {
            if (currentTile >= lowest && currentTile < highest) {
                return currentRow;
            } else {
                lowest += 10;
                highest += 10;
                currentRow--;
            }
        }
        throw new RuntimeException("Error");
    }

    /**
     * Calculates column number dependant on the current tile index.
     *
     * @param numberOfTiles overall number of tiles on the board.
     * @param currentTile   index of the current tile.
     * @return column index of that tile.
     * @throws SettingsException is thrown when overall number of tiles is not divisible by 10.
     */
    private int calculateColumn(int numberOfTiles, int currentTile) throws SettingsException {
        if (numberOfTiles % 10 != 0) {
            throw new SettingsException("Invalid number of tiles. It must be divisible by 10.");
        }

        if (((currentTile / 10) % 2) == 1) {
            //right to left
            return (((
                    //find tile position
                    (currentTile) % 10
                    //switch it to be an opposite side
            ) - 10) * -1) - 1;
        } else {
            //left to right
            return (currentTile) % 10;
        }
    }

    /**
     * Retrieves numberOfPlayers setting from settings.
     *
     * @return number of players.
     */
    private int getNumberOfPlayersSetting() {
        return Integer.parseInt(settings.get(SettingsEnum.NUMBER_OF_PLAYERS).toString());
    }

    /**
     * Retrieves numberOfTiles setting from settings.
     *
     * @return number of tiles.
     */
    private int getNumberOfTilesSetting() {
        return Integer.parseInt(settings.get(SettingsEnum.NUMBER_OF_TILES).toString());
    }

    /**
     * Retrieves numberOfSnakes setting from settings.
     *
     * @return number of snakes.
     */
    private int getNumberOfSnakesSetting() {
        return Integer.parseInt(settings.get(SettingsEnum.NUMBER_OF_SNAKES).toString());
    }

    /**
     * Retrieves numberOfLadders setting from settings.
     *
     * @return number of ladders.
     */
    private int getNumberOfLaddersSetting() {
        return Integer.parseInt(settings.get(SettingsEnum.NUMBER_OF_LADDERS).toString());
    }

    /**
     * Retrieves isOverNetwork setting from settings.
     *
     * @return boolean.
     */
    private boolean getIsOverNetworkSetting() {
        return Boolean.parseBoolean(settings.get(SettingsEnum.IS_OVER_NETWORK).toString());
    }

    /**
     * Retrieves isOverNetwork setting from settings.
     *
     * @return boolean.
     */
    private boolean getIsCurrentPlayerHostSetting() {
        return Boolean.parseBoolean(settings.get(SettingsEnum.IS_CURRENT_PLAYER_HOST).toString());
    }

    /**
     * Retrieves isOverNetwork setting from settings.
     *
     * @return boolean.
     */
    private boolean getIsHardGameSetting() {
        return Boolean.parseBoolean(settings.get(SettingsEnum.IS_HARD_GAME).toString());
    }

    /**
     * Sets player as current player and changes player label to show that players name.
     *
     * @param player player that is being set as current player.
     */
    private void setPlayerAsCurrent(Player player) {
        currentPlayer = player;
        lblCurrentPlayerName.setText(player.getName());
    }

    /**
     * Clears chat text field
     */
    private void clearTextField() {
        tfChatInput.clear();
    }

    /**
     * Takes input from text field and pushes it to chat message array
     */
    private void sendMessage() {
        chat.add(tfChatInput.getText());
    }

    /**
     * Checks to see if key event on text field is enter or not.
     * @param keyEvent passed key event
     * @return True if enter is pressed, otherwise false.
     */
    private boolean isEnter(KeyEvent keyEvent) {
        return keyEvent.getCode() == KeyCode.ENTER;
    }

    /**
     * Draws ladders on the canvas
     */
    private void drawLadders() {
        for (var pane :
                gpBoard.getChildren()) {
            pane.getBoundsInParent();
        }

        List<Pair<Tile, Tile>> ladders = new ArrayList<>();

        for (Tile tile :
                tiles) {
            if (tile.getLadderStartId() != null) {
                Tile end = null;
                for (Tile tile1 :
                        tiles) {
                    if (tile1.getLadderEndId() != null && tile1.getLadderEndId().equals(tile.getLadderStartId())) {
                        end = tile1;
                        break;
                    }
                }
                if (end != null) {
                    ladders.add(new Pair<>(tile, end));
                }
            }
        }

        int counter = 0;
        for (var pair :
                ladders) {
            var startTile = (GridPane) getNodeFromGridPane(gpBoard, pair.getKey().getColumnIndex(), pair.getKey().getRowIndex());
            var endTile = (GridPane) getNodeFromGridPane(gpBoard, pair.getValue().getColumnIndex(), pair.getValue().getRowIndex());

            Node startSpot = null;
            if (startTile != null) {
                startSpot = getNodeFromGridPane(startTile, Ladder.getLadderStartColumn(), Ladder.getLadderStartRow());
            }
            Node endSpot = null;
            if (endTile != null) {
                endSpot = getNodeFromGridPane(endTile, Ladder.getLadderEndColumn(), Ladder.getLadderEndRow());
            }

            if (startSpot != null && endSpot != null) {
                var startSpotBounds = startSpot.localToScene(startSpot.getBoundsInLocal());
                var endSpotBounds = endSpot.localToScene(endSpot.getBoundsInLocal());

                DrawingUtils.drawLadder(
                        cBoard.getGraphicsContext2D(),
                        startSpotBounds.getCenterX(),
                        startSpotBounds.getCenterY(),
                        endSpotBounds.getCenterX(),
                        endSpotBounds.getCenterY(),
                        DrawingUtils.ladderColors.get(counter));

            }
            counter++;
        }
    }

    /**
     * Draws snakes on the canvas
     */
    private void drawSnakes() {
        for (var pane :
                gpBoard.getChildren()) {
            pane.getBoundsInParent();
        }

        List<Pair<Tile, Tile>> snakes = new ArrayList<>();

        for (Tile tile :
                tiles) {
            if (tile.getSnakeStartId() != null) {
                Tile end = null;
                for (Tile tile1 :
                        tiles) {
                    if (tile1.getSnakeEndId() != null && tile1.getSnakeEndId().equals(tile.getSnakeStartId())) {
                        end = tile1;
                        break;
                    }
                }
                if (end != null) {
                    snakes.add(new Pair<>(tile, end));
                }
            }
        }
        int counter = 0;
        for (var pair :
                snakes) {
            var startTile = (GridPane) getNodeFromGridPane(gpBoard, pair.getKey().getColumnIndex(), pair.getKey().getRowIndex());
            var endTile = (GridPane) getNodeFromGridPane(gpBoard, pair.getValue().getColumnIndex(), pair.getValue().getRowIndex());

            Node startSpot = null;
            if (startTile != null) {
                startSpot = getNodeFromGridPane(startTile, Snake.getSnakeStartColumn(), Snake.getSnakeStartRow());
            }
            Node endSpot = null;
            if (endTile != null) {
                endSpot = getNodeFromGridPane(endTile, Snake.getSnakeEndColumn(), Snake.getSnakeEndRow());
            }

            if (startSpot != null && endSpot != null) {
                var startSpotBounds = startSpot.localToScene(startSpot.getBoundsInLocal());
                var endSpotBounds = endSpot.localToScene(endSpot.getBoundsInLocal());
                DrawingUtils.drawSnake(
                        cBoard.getGraphicsContext2D(),
                        startSpotBounds.getCenterX(),
                        startSpotBounds.getCenterY(),
                        endSpotBounds.getCenterX(),
                        endSpotBounds.getCenterY(),
                        DrawingUtils.snakeColors.get(counter));


            }
            counter ++;
        }
    }

    /**
     * Adds background to given GridPane
     * @param pane GridPane that needs background
     * @param paneBackgroundColor Color of the background.
     * @return GridPane with background of the wanted color.
     */
    private GridPane addBackground(GridPane pane, Color paneBackgroundColor) {

        Background background = new Background(new BackgroundFill(paneBackgroundColor, null, null));
        pane.setBackground(background);

        return pane;
    }

    /**
     * Adds border to given GridPane
     * @param pane Passed GridPane object that needs border.
     * @param topBorderColor Color of the top border
     * @param topBorderStyle Style of the top  border.
     * @param bottomBorderColor Color of the bottom border
     * @param bottomBorderStyle Style of the bottom  border.
     * @param rightBorderColor Color of the right border
     * @param rightBorderStyle Style of the right  border.
     * @param leftBorderColor Color of the left border
     * @param leftBorderStyle Style of the left  border.
     * @param borderRadius Radius of the border
     * @return GridPane with assigned borders
     */
    private GridPane addBorder(GridPane pane, Color topBorderColor, BorderStrokeStyle topBorderStyle, Color bottomBorderColor, BorderStrokeStyle bottomBorderStyle, Color rightBorderColor, BorderStrokeStyle rightBorderStyle, Color leftBorderColor, BorderStrokeStyle leftBorderStyle, double borderRadius) {

        Border border = new Border(
                new BorderStroke(
                        topBorderColor,
                        rightBorderColor,
                        bottomBorderColor,
                        leftBorderColor,
                        topBorderStyle,
                        rightBorderStyle,
                        bottomBorderStyle,
                        leftBorderStyle,
                        new CornerRadii(borderRadius),
                        BorderWidths.DEFAULT,
                        Insets.EMPTY)
        );
        pane.setBorder(border);

        return pane;
    }

    /**
     * Adds solid borders to the sides of passed GridPane that have been assigned color. If color wasn't assigned, then border won't be set.
     * @param pane GridPane object that needs borders.
     * @param topBorderColor Color of the top border
     * @param bottomBorderColor Color of the bottom border
     * @param rightBorderColor Color of the right border
     * @param leftBorderColor Color of the left border
     * @param borderRadius Radius of the border
     * @return GridPane with assigned borders
     */
    private GridPane addSolidBorder(GridPane pane, Color topBorderColor, Color bottomBorderColor, Color rightBorderColor, Color leftBorderColor, double borderRadius) {

        return addBorder(pane,
                topBorderColor,
                topBorderColor != null ? BorderStrokeStyle.SOLID : BorderStrokeStyle.NONE,
                bottomBorderColor,
                bottomBorderColor != null ? BorderStrokeStyle.SOLID : BorderStrokeStyle.NONE,
                rightBorderColor,
                rightBorderColor != null ? BorderStrokeStyle.SOLID : BorderStrokeStyle.NONE,
                leftBorderColor,
                leftBorderColor != null ? BorderStrokeStyle.SOLID : BorderStrokeStyle.NONE,
                borderRadius);
    }

    /**
     * Adds dashed borders to the sides of passed GridPane that have been assigned color. If color wasn't assigned, then border won't be set.
     * @param pane GridPane object that needs borders.
     * @param topBorderColor Color of the top border
     * @param bottomBorderColor Color of the bottom border
     * @param rightBorderColor Color of the right border
     * @param leftBorderColor Color of the left border
     * @param borderRadius Radius of the border
     * @return GridPane with assigned borders
     */
    private GridPane addDashedBorder(GridPane pane, Color topBorderColor, Color bottomBorderColor, Color rightBorderColor, Color leftBorderColor, double borderRadius) {

        return addBorder(pane,
                topBorderColor,
                topBorderColor != null ? BorderStrokeStyle.DASHED : BorderStrokeStyle.NONE,
                bottomBorderColor,
                bottomBorderColor != null ? BorderStrokeStyle.DASHED : BorderStrokeStyle.NONE,
                rightBorderColor,
                rightBorderColor != null ? BorderStrokeStyle.DASHED : BorderStrokeStyle.NONE,
                leftBorderColor,
                leftBorderColor != null ? BorderStrokeStyle.DASHED : BorderStrokeStyle.NONE,
                borderRadius);
    }


    public void onAbortGame() {
          var button = MessageUtils.showMessage(":(", "Abort game", "Are you sure?", Alert.AlertType.CONFIRMATION).get();

          if(button.equals(ButtonType.OK)){
              try {
                  SceneUtils.createAndReplaceStage(ViewEnum.HOTSEAT_GAME_OPTIONS, "Game options", settings);
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }


    }


}