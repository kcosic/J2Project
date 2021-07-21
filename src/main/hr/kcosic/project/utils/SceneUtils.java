package main.hr.kcosic.project.utils;

import main.hr.kcosic.project.models.enums.SettingsEnum;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

public class SceneUtils {

    public static Stage stage = null;
    /**
     * Creates Stage object for given view with given title.
     * @param view Type of ViewEnum.
     * @param stageTitle Title of Stage.
     * @param settings Game settings.
     * @return Loaded stage object.
     * @throws IOException
     */
    public static Stage createStage(String view, String stageTitle, Properties settings) throws IOException {
        String resolution = null;
        if(settings.containsKey(SettingsEnum.RESOLUTION)){
            resolution = settings.get(SettingsEnum.RESOLUTION).toString();
        }
        var isFullscreen = Boolean.parseBoolean(settings.get(SettingsEnum.FULLSCREEN).toString());

        Parent root = FXMLLoader.load(SceneUtils.class.getResource("/views/".concat(view).concat(".fxml")));
        if(stage == null){
            stage = new Stage();
        }
        stage.setTitle(stageTitle != null ? stageTitle : "");
        stage.setScene(new Scene(root));
        stage.setMaximized(isFullscreen);
        stage.setResizable(false);
        if(resolution != null && !resolution.isEmpty()){
            stage.setWidth(Double.parseDouble(resolution.substring(0, resolution.indexOf('x'))));
            stage.setHeight(Double.parseDouble(resolution.substring(resolution.indexOf('x') + 1)));
        }

        return stage;
    }

    /**
     * Creates Stage object for given view with given title.
     * @return Loaded stage object.
     * @throws IOException
     */
    public static void replaceStage( Stage newStage) {
        stage = newStage;
    }

    public static void createAndReplaceStage(String view, String stageTitle, Properties settings) throws IOException{
        replaceStage(createStage(view, stageTitle, settings));
    }



    public static void closeStage(Node node) {
        node.getScene().getWindow().hide();
    }


}
