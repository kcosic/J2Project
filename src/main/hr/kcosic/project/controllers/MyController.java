package main.hr.kcosic.project.controllers;

import javafx.fxml.Initializable;
import main.hr.kcosic.project.models.XmlProperties;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.SettingsUtils;
import main.hr.kcosic.project.utils.SceneUtils;

import java.io.IOException;
import java.net.Socket;

public abstract class MyController implements Initializable {
    public XmlProperties settings;
    public Socket clientSocket = null;

    public MyController() {

        settings = SettingsUtils.loadSettings();
    }

    public MyController(Socket clientSocket){
        this();
        this.clientSocket = clientSocket;
    }

    public void saveSettings(XmlProperties settings){
        SettingsUtils.saveSettings(settings);
    }

    public void goToNextStage(ViewEnum view, String stageTitle) throws IOException {
        saveSettings(settings);
        SceneUtils.createAndReplaceStage(view.getResourcePath(), stageTitle, settings);
    }
}
