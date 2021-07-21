package main.hr.kcosic.project.controllers;

import main.hr.kcosic.project.models.DataWrapper;
import main.hr.kcosic.project.models.Player;
import main.hr.kcosic.project.models.enums.DataType;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ResourceBundle;

public class JoinViewController extends MyController implements Initializable {
    @FXML
    public Button btnBack;
    @FXML
    public Button btnConnect;
    @FXML
    public TextField tfName;
    @FXML
    public TextField tfAddress;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnConnect.setDisable(true);
    }

    @FXML
    public void back() throws IOException {
        SceneUtils.createAndReplaceStage(ViewEnum.NETWORK_GAME_OPTIONS, "Network game", settings);
    }

    @FXML
    public void connect() throws IOException {
        NetworkUtils.connectToServer(tfAddress.getText().trim(), GameServer.PORT_NUM);
        var player = new DataWrapper(DataType.PLAYER, new Player(0, tfName.getText(), "#ffffff" ));
        NetworkUtils.sendData(player);
        btnConnect.setDisable(true);
        tfName.setDisable(true);
        tfAddress.setDisable(true);

        var newThread = new Thread(() -> {
            LogUtils.logInfo("Client Waiting for data");
            var exit = false;
            while(!exit){
                try {
                    var data = (DataWrapper)(new ObjectInputStream(NetworkUtils.getSocket().getInputStream())).readObject();
                    if(data.getType() == DataType.START_GAME){
                        settings.put(SettingsEnum.CURRENT_GAME_PLAYER, SerializationUtils.serialize(data.getData()));
                        settings.put(SettingsEnum.IS_CURRENT_PLAYER_HOST, String.valueOf(false));
                        FileUtils.saveSettings(settings);
                        exit = true;
                        Platform.runLater(()->{
                            try {
                                SceneUtils.createAndReplaceStage(ViewEnum.BOARD_VIEW, "Game", settings);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }
        });
        newThread.start();
    }

    @FXML
    public void checkValidity() {
        btnConnect.setDisable(tfAddress.getText().trim().isEmpty() || tfName.getText().trim().isEmpty());
    }
}
