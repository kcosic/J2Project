package main.hr.kcosic.project.controllers;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import main.hr.kcosic.project.models.DataWrapper;
import main.hr.kcosic.project.models.Player;
import main.hr.kcosic.project.models.enums.DataType;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.ViewEnum;
import main.hr.kcosic.project.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ResourceBundle;

public class JoinController extends MyController {
    @FXML
    public Button btnBack;
    @FXML
    public Button btnConnect;
    @FXML
    public TextField tfName;
    @FXML
    public TextField tfAddress;
    @FXML
    public Label lblStatus;


    private Thread newThread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializeComponents();
    }

    private void initializeComponents() {
        btnConnect.setDisable(true);
        tfAddress.setText("0.0.0.0");
    }

    @FXML
    public void back() throws IOException {
        if(newThread != null){
            newThread.interrupt();
            NetworkUtils.sendData(new DataWrapper(DataType.DISCONNECT, tfName.getText()));
            NetworkUtils.disconnectFromServer();
        }
        goToNextStage(ViewEnum.NETWORK_VIEW, "Network game");
    }

    /**
     * Connects to the server and awaits further instructions.
     * @throws IOException Throws IOException if Board scene was not created
     */
    @FXML
    public void connect() throws IOException {
        lblStatus.setText("Connecting...");
        boolean hasConnection;
        try {
            NetworkUtils.connectToServer(tfAddress.getText().trim(), GameServer.PORT_NUM);
            hasConnection = true;
        } catch (IOException e) {
            lblStatus.setText("Failed to connect.");
            hasConnection = false;
        }

        if(hasConnection){
            lblStatus.setText("Connected, waiting for host to start the game." );
            var player = new DataWrapper(DataType.PLAYER, new Player(0, tfName.getText(), null, null));
            NetworkUtils.sendData(player);
            btnConnect.setDisable(true);
            tfName.setDisable(true);
            tfAddress.setDisable(true);

            newThread = new Thread(() -> {
                LogUtils.logInfo("Client Waiting for data");
                var exit = false;
                while(!exit){
                    try {
                        if(newThread.isInterrupted()){
                            exit = true;
                        }
                        else {
                            var data = (DataWrapper)(new ObjectInputStream(NetworkUtils.getSocket().getInputStream())).readObject();
                            if(data.getType() == DataType.START_GAME){
                                settings.put(SettingsEnum.CURRENT_GAME_PLAYER, SerializationUtils.serializeToJson(data.getData()));
                                exit = true;
                                Platform.runLater(()->{
                                    try {
                                        goToNextStage(ViewEnum.BOARD_VIEW, "Game");
                                        newThread.interrupt();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }

                    }
                    catch (SocketException e){
                        LogUtils.logInfo("Disconnected from the server.");
                        exit = true;
                    }
                    catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }


                }
            });
            newThread.start();
        }

    }

    /**
     * Checks to see if input is valid. If is then button will be enabled.
     */
    @FXML
    public void checkValidity() {
        btnConnect.setDisable(tfAddress.getText().trim().isEmpty() || tfName.getText().trim().isEmpty());
    }

    public void onSelectedColor() {
    }

    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    private String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }
}
