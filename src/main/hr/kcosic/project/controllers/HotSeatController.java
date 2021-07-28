package main.hr.kcosic.project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.converter.IntegerStringConverter;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class HotSeatController extends MyController {
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
        tfLadders.setText("5");
        tfSnakes.setText("5");
        tfTiles.setText("100");
        addIntegerMask(tfPlayers);
        bindBidirectional(tfPlayers, slPlayers);
    }

    @FXML
    public void start() throws IOException {
        settings.put(SettingsEnum.NUMBER_OF_TILES, tfTiles.getText());
        settings.put(SettingsEnum.NUMBER_OF_SNAKES, tfSnakes.getText());
        settings.put(SettingsEnum.NUMBER_OF_PLAYERS, tfPlayers.getText());
        settings.put(SettingsEnum.NUMBER_OF_LADDERS, tfLadders.getText());
        settings.put(SettingsEnum.IS_HARD_GAME, String.valueOf(chkHardGame.isSelected()));
        goToNextStage(ViewEnum.BOARD_VIEW, "Game");

    }

    @FXML
    public void back() throws IOException {
        settings.remove(SettingsEnum.NUMBER_OF_TILES);
        settings.remove(SettingsEnum.NUMBER_OF_SNAKES);
        settings.remove(SettingsEnum.NUMBER_OF_PLAYERS);
        settings.remove(SettingsEnum.NUMBER_OF_LADDERS);
        settings.remove(SettingsEnum.IS_HARD_GAME);
        goToNextStage(ViewEnum.NEW_GAME_VIEW, "New game");
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
