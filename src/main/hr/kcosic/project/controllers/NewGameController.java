package main.hr.kcosic.project.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.SceneUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewGameController extends MyController implements Initializable {
    @FXML
    public Button btnHotSeatGame;
    @FXML
    public Button btnNetworkGame;
    @FXML
    public Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void newHotSeatGame() {
        settings.put(SettingsEnum.IS_OVER_NETWORK, String.valueOf(false));
        saveSettings(settings);
        try {
            SceneUtils.createAndReplaceStage(ViewEnum.HOTSEAT_GAME_OPTIONS, "Setup", settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void newNetworkGame() {
        settings.put(SettingsEnum.IS_OVER_NETWORK, String.valueOf(true));
        saveSettings(settings);
        try {
            SceneUtils.createAndReplaceStage(ViewEnum.NETWORK_GAME_OPTIONS, "Network game", settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void back() {
        try {
            SceneUtils.createAndReplaceStage(ViewEnum.MAIN_MENU_VIEW, "Main menu", settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
