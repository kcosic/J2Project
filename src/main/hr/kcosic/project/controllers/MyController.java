package main.hr.kcosic.project.controllers;

import main.hr.kcosic.project.utils.FileUtils;

import java.net.Socket;
import java.util.Properties;

public class MyController {
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
}
