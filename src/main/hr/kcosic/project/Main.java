package main.hr.kcosic.project;

import main.hr.kcosic.project.utils.*;
import main.hr.kcosic.project.models.enums.ViewEnum;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        var generateDocs = true;
        try {
            if(generateDocs){
                DocsUtils.generateDocs();
            }
            SceneUtils.createStage(ViewEnum.MAIN_MENU_VIEW.getResourcePath(), "Main menu", SettingsUtils.loadSettings()).show();
        } catch (Exception e){
            e.printStackTrace();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            try {
                in.readLine();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
