package main.hr.kcosic.project.controllers;

import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.LogUtils;
import main.hr.kcosic.project.utils.SceneUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController extends MyController implements Initializable {

    @FXML
    public Button btnOptions;
    @FXML
    public Button btnExit;
    @FXML
    public Button btnNewGame;
    @FXML
    public BorderPane bpMainWindow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void newGame(ActionEvent actionEvent) {
        try {


            SceneUtils.createAndReplaceStage(ViewEnum.NEW_GAME_VIEW, "New game",settings);
        } catch (Exception e) {
            //Dialog to catch exception
            LogUtils.logInfo(e.getMessage());
        }
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        SceneUtils.closeStage((Node)actionEvent.getSource());
    }

    @FXML
    public void options(ActionEvent actionEvent) {
        try {
            SceneUtils.createAndReplaceStage(ViewEnum.OPTIONS_VIEW, "Options", settings);
        } catch (Exception e) {
            //Dialog to catch exception
            //SceneUtils.log(e.getMessage());
            e.printStackTrace();
        }
    }


}