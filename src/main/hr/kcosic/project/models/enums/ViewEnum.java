package main.hr.kcosic.project.models.enums;

/**
 * Enumerator for FXML views.
 */
public enum ViewEnum {
    MAIN_MENU_VIEW("MainMenuView"),
    NEW_GAME_VIEW("NewGameView"),
    BOARD_VIEW("BoardView"),
    OPTIONS_VIEW("OptionsView"),
    HOTSEAT_GAME_OPTIONS("HotSeatGameOptions"),
    HOST_VIEW("HostView"),
    JOIN_VIEW("JoinView"),
    NETWORK_GAME_OPTIONS("NetworkGameOptions");

    private final String text;

    ViewEnum(String text){
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
