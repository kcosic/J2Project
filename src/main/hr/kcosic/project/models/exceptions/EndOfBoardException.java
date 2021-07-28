package main.hr.kcosic.project.models.exceptions;

public class EndOfBoardException extends Exception {

    private boolean isRolledTooMuch;

    public EndOfBoardException() {
    }

    public EndOfBoardException(String message, boolean isRolledTooMuch) {
        super(message);
        this.isRolledTooMuch = isRolledTooMuch;
    }

    public boolean isRolledTooMuch() {
        return isRolledTooMuch;
    }

}
