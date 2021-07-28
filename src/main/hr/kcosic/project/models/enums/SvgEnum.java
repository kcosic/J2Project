package main.hr.kcosic.project.models.enums;

import java.io.Serializable;

/**
 * Enumerator for FXML views.
 */
public enum SvgEnum implements Serializable {
    PAWN_YELLOW("yellow pawn"),
    PAWN_BLUE("blue pawn"),
    PAWN_RED("red pawn"),
    PAWN_GREEN("greeen pawn"),
    DICE("dices");


    private final String text;

    SvgEnum(String text){
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getResourcePath(){
        return "/assets/" + text + ".svg";
    }
}
