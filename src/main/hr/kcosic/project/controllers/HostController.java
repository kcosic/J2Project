package main.hr.kcosic.project.controllers;

import javafx.scene.paint.Color;
import main.hr.kcosic.project.models.DataWrapper;
import main.hr.kcosic.project.models.Player;
import main.hr.kcosic.project.models.enums.DataType;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.LogUtils;
import main.hr.kcosic.project.utils.NetworkUtils;
import main.hr.kcosic.project.utils.SerializationUtils;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.converter.IntegerStringConverter;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.UnaryOperator;

public class HostController extends MyController {
    @FXML
    public Button btnSetName;
    @FXML
    public TextField tfName;
    @FXML
    public ListView<String> lvPlayers;
    @FXML
    public Button btnBack;
    @FXML
    public Button btnStart;
    @FXML
    public CheckBox chkHardGame;
    @FXML
    public Slider slLadders;
    @FXML
    public StackPane spMain;
    @FXML
    public TextField tfTiles;
    @FXML
    public Slider slTiles;
    @FXML
    public TextField tfSnakes;
    @FXML
    public Slider slSnakes;
    @FXML
    public TextField tfLadders;


    protected ListProperty<String> listProperty = new SimpleListProperty<>();
    private final ObservableList<String> playerNames = FXCollections.observableArrayList();
    private final List<Player> players = new ArrayList<>();
    Socket clientSocket;
    private Thread receiveThread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComponents();
        receiveThread = new Thread(() -> {
            LogUtils.logInfo("Client Waiting for data");
            var exit = false;
            while(!exit){

                try {
                    if(receiveThread.isInterrupted()){
                        exit = true;
                    }
                    else {
                        var data = (DataWrapper)(new ObjectInputStream(clientSocket.getInputStream())).readObject();
                        if (data.getType() == DataType.PLAYER) {
                            Platform.runLater(()-> addPlayerToList((Player) data.getData()));
                        }
                        else if (data.getType() == DataType.DISCONNECT){
                            Platform.runLater(()-> removePlayerFromList((String) data.getData()));
                        }
                    }


                } catch (EOFException e) {
                    exit = true;

                }
                catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private void removePlayerFromList(String playerName) {
        players.removeIf(player -> player.getName().equals(playerName));
        listProperty.removeIf(name -> name.equals(playerName));
    }


    private void initializeComponents() {
        lvPlayers.itemsProperty().bind(listProperty);
        listProperty.set(playerNames);
        lvPlayers.setCellFactory(list -> new ListCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setWrapText(true);
                    setPrefWidth(10);
                    setText(item);
                }
            }

        });
        addIntegerMask(tfLadders);
        addIntegerMask(tfSnakes);
        addIntegerMask(tfTiles);
        bindBidirectional(tfLadders, slLadders);
        bindBidirectional(tfTiles, slTiles);
        bindBidirectional(tfSnakes, slSnakes);
        tfLadders.setText("5");
        tfSnakes.setText("5");
        tfTiles.setText("100");

        btnStart.setDisable(true);
    }

    private void addIntegerMask(TextField tf) {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("\\d*")) {
                return change;
            }
            return null;
        };
        tf.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
    }

    private void bindBidirectional(TextField tf, Slider sl) {
        tf.textProperty().bindBidirectional(sl.valueProperty(), NumberFormat.getIntegerInstance());
    }

    private void addPlayerToList(Player data) {
        LogUtils.logInfo(SerializationUtils.serializeToJson(data));
        players.add(data);
        playerNames.add(data.getName());
        btnStart.setDisable(playerNames.size() < 2);
    }

    private void runServer() {
        try {
            GameServer.start();
            clientSocket = NetworkUtils.connectToServer("0.0.0.0", GameServer.PORT_NUM);
            LogUtils.logInfo("Connected to host");
            int i = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void setName() throws IOException {
        runServer();
        receiveThread.start();

        var me = new Player(0,tfName.getText(), null, null);
        var data = new DataWrapper(DataType.PLAYER, me );
        NetworkUtils.sendData(data);
        settings.put(SettingsEnum.CURRENT_GAME_PLAYER, SerializationUtils.serializeToJson(me));

        tfName.setDisable(true);
    }
    @FXML
    public void start() throws IOException {

        var data = new DataWrapper(DataType.START_GAME, true );
        NetworkUtils.sendData(data);
        settings.put(SettingsEnum.PLAYERS, SerializationUtils.serializeToJson(players));
        settings.put(SettingsEnum.NUMBER_OF_PLAYERS, String.valueOf(players.size()));
        settings.put(SettingsEnum.NUMBER_OF_TILES, tfTiles.getText());
        settings.put(SettingsEnum.NUMBER_OF_SNAKES, tfSnakes.getText());
        settings.put(SettingsEnum.NUMBER_OF_LADDERS, tfLadders.getText());
        settings.put(SettingsEnum.IS_HARD_GAME, String.valueOf(chkHardGame.isSelected()));
        receiveThread.interrupt();
        goToNextStage(ViewEnum.BOARD_VIEW, "Game");
    }
    @FXML
    public void back() throws IOException {
        GameServer.stop();
        receiveThread.interrupt();
        settings.remove(SettingsEnum.CURRENT_GAME_PLAYER);
        goToNextStage(ViewEnum.NETWORK_VIEW, "Network game");
    }


    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    private String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }
}

