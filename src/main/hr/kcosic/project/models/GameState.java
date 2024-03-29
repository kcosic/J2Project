package main.hr.kcosic.project.models;

import java.io.Serializable;

public class GameState implements Serializable {

    private Tile previousTile;
    private Tile nextTile;
    private Player currentPlayer;
    private int rolledDice;

    private Player previousPlayer;
    public GameState() {
    }

    public GameState(Tile previousTile, Tile nextTile, Player nextPlayer, Player previousPlayer, int rolledDice) {
        this.previousTile = previousTile;
        this.nextTile = nextTile;
        this.currentPlayer = nextPlayer;
        this.previousPlayer = previousPlayer;
        this.rolledDice = rolledDice;
    }

    public Tile getPreviousTile() {
        return previousTile;
    }

    public void setPreviousTile(Tile previousTile) {
        this.previousTile = previousTile;
    }

    public Tile getNextTile() {
        return nextTile;
    }

    public void setNextTile(Tile nextTile) {
        this.nextTile = nextTile;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getRolledDice() {
        return rolledDice;
    }

    public void setRolledDice(int rolledDice) {
        this.rolledDice = rolledDice;
    }


    public Player getPreviousPlayer() {
        return previousPlayer;
    }

    public void setPreviousPlayer(Player previousPlayer) {
        this.previousPlayer = previousPlayer;
    }


}
