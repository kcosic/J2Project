package main.hr.kcosic.project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewGameController extends MyController {
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
    public void newHotSeatGame() throws IOException {
        settings.put(SettingsEnum.IS_OVER_NETWORK, String.valueOf(false));
        goToNextStage(ViewEnum.HOTSEAT_VIEW, "Setup");
    }

    @FXML
    public void newNetworkGame() throws IOException {
        settings.put(SettingsEnum.IS_OVER_NETWORK, String.valueOf(true));
        goToNextStage(ViewEnum.NETWORK_VIEW, "Network game");
    }

    @FXML
    public void back() throws IOException {
        settings.remove(SettingsEnum.IS_OVER_NETWORK);
        goToNextStage(ViewEnum.MAIN_MENU_VIEW, "Main menu");

    }
}
