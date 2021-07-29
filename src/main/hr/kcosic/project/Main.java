package main.hr.kcosic.project;

import main.hr.kcosic.project.controllers.*;
import main.hr.kcosic.project.models.*;
import main.hr.kcosic.project.models.enums.DataType;
import main.hr.kcosic.project.models.enums.SettingsEnum;
import main.hr.kcosic.project.models.enums.SvgEnum;
import main.hr.kcosic.project.models.exceptions.BoardException;
import main.hr.kcosic.project.models.exceptions.EndOfBoardException;
import main.hr.kcosic.project.models.exceptions.GameStateException;
import main.hr.kcosic.project.models.exceptions.SettingsException;
import main.hr.kcosic.project.utils.*;
import main.hr.kcosic.project.models.enums.ViewEnum;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            /*StringBuilder sb = new StringBuilder();
            ReflectionUtils.readClassAndMembersInfo(BoardController.class, sb);
            ReflectionUtils.readClassAndMembersInfo(GameServer.class, sb);
            ReflectionUtils.readClassAndMembersInfo(HostController.class, sb);
            ReflectionUtils.readClassAndMembersInfo(HotSeatController.class, sb);
            ReflectionUtils.readClassAndMembersInfo(JoinController.class, sb);
            ReflectionUtils.readClassAndMembersInfo(MainMenuController.class, sb);
            ReflectionUtils.readClassAndMembersInfo(MyController.class, sb);
            ReflectionUtils.readClassAndMembersInfo(NetworkController.class, sb);
            ReflectionUtils.readClassAndMembersInfo(NewGameController.class, sb);
            ReflectionUtils.readClassAndMembersInfo(OptionsController.class, sb);
            ReflectionUtils.readClassAndMembersInfo(DataType.class, sb);
            ReflectionUtils.readClassAndMembersInfo(SettingsEnum.class, sb);
            ReflectionUtils.readClassAndMembersInfo(SvgEnum.class, sb);
            ReflectionUtils.readClassAndMembersInfo(ViewEnum.class, sb);
            ReflectionUtils.readClassAndMembersInfo(BoardException.class, sb);
            ReflectionUtils.readClassAndMembersInfo(EndOfBoardException.class, sb);
            ReflectionUtils.readClassAndMembersInfo(GameStateException.class, sb);
            ReflectionUtils.readClassAndMembersInfo(SettingsException.class, sb);
            ReflectionUtils.readClassAndMembersInfo(Board.class, sb);
            ReflectionUtils.readClassAndMembersInfo(DataWrapper.class, sb);
            ReflectionUtils.readClassAndMembersInfo(GameState.class, sb);
            ReflectionUtils.readClassAndMembersInfo(Ladder.class, sb);
            ReflectionUtils.readClassAndMembersInfo(Player.class, sb);
            ReflectionUtils.readClassAndMembersInfo(Snake.class, sb);
            ReflectionUtils.readClassAndMembersInfo(Tile.class, sb);
            ReflectionUtils.readClassAndMembersInfo(ByteUtils.class, sb);
            ReflectionUtils.readClassAndMembersInfo(DrawingUtils.class, sb);
            ReflectionUtils.readClassAndMembersInfo(LogUtils.class, sb);
            ReflectionUtils.readClassAndMembersInfo(MessageUtils.class, sb);
            ReflectionUtils.readClassAndMembersInfo(NetworkUtils.class, sb);
            ReflectionUtils.readClassAndMembersInfo(ReflectionUtils.class, sb);
            ReflectionUtils.readClassAndMembersInfo(SceneUtils.class, sb);
            ReflectionUtils.readClassAndMembersInfo(SerializationUtils.class, sb);
            LogUtils.logInfo(sb.toString());*/
            SceneUtils.createStage(ViewEnum.MAIN_MENU_VIEW.getResourcePath(), "Main menu", FileUtils.loadSettings()).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
