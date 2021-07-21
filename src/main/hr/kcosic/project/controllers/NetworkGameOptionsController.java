package main.hr.kcosic.project.controllers;


import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.SceneUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NetworkGameOptionsController extends MyController implements Initializable {

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

    private void initializeComponents() {


    }

    @FXML
    public void back() throws IOException {
        SceneUtils.createAndReplaceStage(ViewEnum.NEW_GAME_VIEW, "New game", settings);
    }

    @FXML
    public void host() throws IOException {
        SceneUtils.createAndReplaceStage(ViewEnum.HOST_VIEW, "Host game", settings);
    }

    @FXML
    public void join() throws IOException {
        SceneUtils.createAndReplaceStage(ViewEnum.JOIN_VIEW, "Join game", settings);
    }
}
