package main.hr.kcosic.project.controllers;

import javafx.fxml.Initializable;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.FileUtils;
import main.hr.kcosic.project.utils.LogUtils;
import main.hr.kcosic.project.utils.SceneUtils;
import main.hr.kcosic.project.utils.SerializationUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public abstract class MyController implements Initializable {
    public Properties settings;
    public Socket clientSocket = null;

    public MyController() {

        settings = FileUtils.loadSettings();
    }

    public MyController(Socket clientSocket){
        this();
        this.clientSocket = clientSocket;
    }

    public void saveSettings(Properties settings){
        FileUtils.saveSettings(settings);
    }

    public void goToNextStage(ViewEnum view, String stageTitle) throws IOException {
        saveSettings(settings);
        SceneUtils.createAndReplaceStage(view.getResourcePath(), stageTitle, settings);
    }
}
