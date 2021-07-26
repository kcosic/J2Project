package main.hr.kcosic.project;

import main.hr.kcosic.project.utils.FileUtils;
import main.hr.kcosic.project.utils.SceneUtils;
import main.hr.kcosic.project.models.enums.ViewEnum;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            SceneUtils.createStage(ViewEnum.MAIN_MENU_VIEW.toString(), "Main menu", FileUtils.loadSettings()).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
