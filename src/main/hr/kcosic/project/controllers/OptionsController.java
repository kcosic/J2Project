package main.hr.kcosic.project.controllers;

import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.SceneUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Controller for Options view.
 */
public class OptionsController extends MyController implements Initializable {

    @FXML
    public Button btnSave;
    @FXML
    public CheckBox chkFullscreen;
    @FXML
    public Button btnCancel;
    @FXML
    public ComboBox<String> cbResolution;


    private final ObservableList<String> resolutions = FXCollections.observableList(new ArrayList<>(List.of(
            "1920x1080",
            "1280x1024",
            "800x600"
    )));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComponents();
    }

    private void initializeComponents() {
        chkFullscreen.setSelected(Boolean.parseBoolean(settings.get(SettingsEnum.FULLSCREEN).toString()));
        cbResolution.setItems(resolutions);
        cbResolution.getSelectionModel().select((String)settings.get(SettingsEnum.RESOLUTION));
        cbResolution.setDisable(chkFullscreen.isSelected());
    }

    @FXML
    public void toggleFullscreen() {
        cbResolution.getSelectionModel().clearSelection();
        cbResolution.setDisable(chkFullscreen.isSelected());
        settings.put(SettingsEnum.FULLSCREEN, chkFullscreen.isSelected());
    }

    @FXML
    public void cancel(ActionEvent event) throws IOException {

        SceneUtils.createAndReplaceStage(ViewEnum.MAIN_MENU_VIEW, "Main menu", settings);

    }

    @FXML
    public void changeResolution(ActionEvent actionEvent) {

    }

    @FXML
    public void save(ActionEvent event) throws IOException {
        saveSettings(settings);
        SceneUtils.createAndReplaceStage(ViewEnum.MAIN_MENU_VIEW, "Main menu", settings);
    }

    @Override
    public void saveSettings(Properties settings) {
        if(cbResolution.getValue() != null)
        {
            if(settings.containsKey(SettingsEnum.RESOLUTION)){
                settings.replace(SettingsEnum.RESOLUTION, cbResolution.getValue());
            }
            else {
                settings.put(SettingsEnum.RESOLUTION, cbResolution.getValue());
            }
        }
        else {
            settings.remove(SettingsEnum.RESOLUTION);

        }
        settings.replace(SettingsEnum.FULLSCREEN, String.valueOf(chkFullscreen.isSelected()));
        super.saveSettings(settings);
    }
}
