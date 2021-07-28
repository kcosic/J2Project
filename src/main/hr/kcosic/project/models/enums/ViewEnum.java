package main.hr.kcosic.project.models.enums;

/**
 * Enumerator for FXML views.
 */
public enum ViewEnum {
    MAIN_MENU_VIEW("MainMenuView"),
    NEW_GAME_VIEW("NewGameView"),
    BOARD_VIEW("BoardView"),
    OPTIONS_VIEW("OptionsView"),
    HOTSEAT_VIEW("HotSeatView"),
    HOST_VIEW("HostView"),
    JOIN_VIEW("JoinView"),
    NETWORK_VIEW("NetworkView");

    private final String text;

    ViewEnum(String text){
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getResourcePath(){
        return "/views/" + text + ".fxml";
    }
}
