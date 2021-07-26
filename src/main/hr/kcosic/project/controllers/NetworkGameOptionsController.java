package main.hr.kcosic.project.controllers;


import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.LogUtils;
import main.hr.kcosic.project.utils.SceneUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import main.hr.kcosic.project.utils.SerializationUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NetworkGameOptionsController extends MyController {

    @FXML
    public Button btnBack;
    @FXML
    public Button btnJoin;
    @FXML
    public Button btnHost;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComponents();
    }

    private void initializeComponents() {}

    @FXML
    public void back() throws IOException {
        settings.remove(SettingsEnum.IS_CURRENT_PLAYER_HOST);
        goToNextStage(ViewEnum.NEW_GAME_VIEW, "New game");
    }

    @FXML
    public void host() throws IOException {
        settings.put(SettingsEnum.IS_CURRENT_PLAYER_HOST, String.valueOf(true));
        goToNextStage(ViewEnum.HOST_VIEW, "Host game");
    }

    @FXML
    public void join() throws IOException {
        settings.put(SettingsEnum.IS_CURRENT_PLAYER_HOST, String.valueOf(false));
        goToNextStage(ViewEnum.JOIN_VIEW, "Join game");
    }
}
