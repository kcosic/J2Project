package main.hr.kcosic.project.models;

import java.io.Serializable;

public class GameState implements Serializable {

    private Tile previousTile;
    private Tile nextTile;
    private Player nextPlayer;
    private int rolledDice;

    public GameState() {
    }

    public GameState(Tile previousTile, Tile nextTile, Player nextPlayer, int rolledDice) {
        this.previousTile = previousTile;
        this.nextTile = nextTile;
        this.nextPlayer = nextPlayer;
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

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public int getRolledDice() {
        return rolledDice;
    }

    public void setRolledDice(int rolledDice) {
        this.rolledDice = rolledDice;
    }



}
