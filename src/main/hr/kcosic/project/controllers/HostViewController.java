package main.hr.kcosic.project.controllers;

import com.google.gson.GsonBuilder;
import main.hr.kcosic.project.models.DataWrapper;
import main.hr.kcosic.project.models.Player;
import main.hr.kcosic.project.models.enums.DataType;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.LogUtils;
import main.hr.kcosic.project.utils.NetworkUtils;
import main.hr.kcosic.project.utils.SceneUtils;
import main.hr.kcosic.project.utils.SerializationUtils;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.converter.IntegerStringConverter;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.UnaryOperator;

public class HostViewController extends MyController implements Initializable {
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
                    }


                } catch (EOFException e) {
                    exit = true;

                }
                catch(InterruptedIOException e){
                    LogUtils.logSevere("GOT FCKING INTERRUPTED... RUDE!");
                }
                catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });

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
        tfLadders.setText(settings.get(SettingsEnum.NUMBER_OF_LADDERS).toString());
        tfSnakes.setText(settings.get(SettingsEnum.NUMBER_OF_SNAKES).toString());
        tfTiles.setText(settings.get(SettingsEnum.NUMBER_OF_TILES).toString());

        btnStart.setDisable(true);
    }

    @Override
    public void saveSettings(Properties settings) {
        settings.replace(SettingsEnum.NUMBER_OF_TILES, tfTiles.getText());
        settings.replace(SettingsEnum.NUMBER_OF_SNAKES, tfSnakes.getText());
        settings.replace(SettingsEnum.NUMBER_OF_LADDERS, tfLadders.getText());
        settings.replace(SettingsEnum.IS_HARD_GAME, String.valueOf(chkHardGame.isSelected()));
        super.saveSettings(settings);
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
        LogUtils.logInfo(SerializationUtils.serialize(data));
        players.add(data);
        playerNames.add(data.getName());
        if(playerNames.size() < 1){
            btnStart.setDisable(true);
        }
        else if(playerNames.size() >= 2) {
            btnStart.setDisable(false);
        }
    }

    private void runServer() {
        try {
            GameServer.start();
            LogUtils.logInfo("Connecting host");
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

        var me = new Player(0,tfName.getText(), "#ffffff");
        var data = new DataWrapper(DataType.PLAYER, me );
        NetworkUtils.sendData(data);
        tfName.setDisable(true);
        btnStart.setDisable(false);
    }
    @FXML
    public void start() throws IOException {

        var data = new DataWrapper(DataType.START_GAME, true );
        receiveThread.interrupt();
        NetworkUtils.sendData(data);
        settings.put(SettingsEnum.IS_CURRENT_PLAYER_HOST, String.valueOf(true));
        settings.put(SettingsEnum.PLAYERS, SerializationUtils.serialize(players));
        settings.put(SettingsEnum.NUMBER_OF_PLAYERS, String.valueOf(players.size()));
        LogUtils.logInfo("Players that are passed to the game:");
        LogUtils.logInfo(new GsonBuilder().create().toJson(players));
        saveSettings(settings);
        SceneUtils.createAndReplaceStage(ViewEnum.BOARD_VIEW, "Game", settings);

    }
    @FXML
    public void back() throws IOException {
        GameServer.stop();
        SceneUtils.createAndReplaceStage(ViewEnum.NETWORK_GAME_OPTIONS, "Network game", settings);
    }
}

