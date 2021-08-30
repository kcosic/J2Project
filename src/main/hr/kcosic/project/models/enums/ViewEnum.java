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

    private final String viewName;

    ViewEnum(String viewName){
        this.viewName = viewName;
    }

    @Override
    public String toString() {
        return viewName;
    }

    public String getResourcePath(){
        return "/views/" + viewName + ".fxml";
    }
}
