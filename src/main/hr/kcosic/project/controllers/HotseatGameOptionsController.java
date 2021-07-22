package main.hr.kcosic.project.controllers;

import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.SceneUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class HotseatGameOptionsController extends MyController implements Initializable {
    @FXML
    public Slider slPlayers;
    @FXML
    public Slider slTiles;
    @FXML
    public Slider slSnakes;
    @FXML
    public Slider slLadders;
    @FXML
    public CheckBox chkHardGame;
    @FXML
    public TextField tfPlayers;
    @FXML
    public TextField tfTiles;
    @FXML
    public TextField tfSnakes;
    @FXML
    public TextField tfLadders;
    @FXML
    public Button btnStart;
    @FXML
    public Button btnBack;

    @FXML
    public StackPane spMain;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComponents();
    }

    private void initializeComponents() {
        addIntegerMask(tfLadders);
        addIntegerMask(tfSnakes);
        addIntegerMask(tfTiles);
        bindBidirectional(tfLadders, slLadders);
        bindBidirectional(tfTiles, slTiles);
        bindBidirectional(tfSnakes, slSnakes);
        tfLadders.setText(settings.get(SettingsEnum.NUMBER_OF_LADDERS).toString());
        tfSnakes.setText(settings.get(SettingsEnum.NUMBER_OF_SNAKES).toString());
        tfTiles.setText(settings.get(SettingsEnum.NUMBER_OF_TILES).toString());
        addIntegerMask(tfPlayers);
        bindBidirectional(tfPlayers, slPlayers);
    }

    @Override
    public void saveSettings(Properties settings) {
        settings.replace(SettingsEnum.NUMBER_OF_TILES, tfTiles.getText());
        settings.replace(SettingsEnum.NUMBER_OF_SNAKES, tfSnakes.getText());
        settings.replace(SettingsEnum.NUMBER_OF_PLAYERS, tfPlayers.getText());
        settings.replace(SettingsEnum.NUMBER_OF_LADDERS, tfLadders.getText());
        settings.replace(SettingsEnum.IS_HARD_GAME, String.valueOf(chkHardGame.isSelected()));
        super.saveSettings(settings);
    }

    @FXML
    public void start() throws IOException {
        saveSettings(settings);
        SceneUtils.createAndReplaceStage(ViewEnum.BOARD_VIEW, "Game", settings);
    }

    @FXML
    public void back() throws IOException {
        SceneUtils.createAndReplaceStage(ViewEnum.MAIN_MENU_VIEW, "Main menu", settings);
    }

    private void addIntegerMask(TextField tf) {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("\\d*")) {
                return change;
            }
            return null;
        };
        tf.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
    }

    private void bindBidirectional(TextField tf, Slider sl) {
        tf.textProperty().bindBidirectional(sl.valueProperty(), NumberFormat.getIntegerInstance());
    }


}
