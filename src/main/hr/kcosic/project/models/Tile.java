package main.hr.kcosic.project.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tile implements Serializable {
    private List<Player> playersOnTile;


    private final int id;

    private Integer snakeEndId;
    private Integer snakeStartId;

    private Integer ladderEndId;
    private Integer ladderStartId;

    private int rowIndex;
    private int columnIndex;


    public Tile(int id) {
        this.id = id;
        playersOnTile = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    /**
     * Returns list of players
     * @return List of player ID that are on this tile
     */
    public List<Player> getPlayersOnTile() {
        return new ArrayList<>(this.playersOnTile);
    }

    public void setPlayersOnTile(List<Player> playersOnTile) {
        this.playersOnTile = playersOnTile;
    }

    public Integer getSnakeEndId() {
        return snakeEndId;
    }

    public void setSnakeEndId(int snakeEndId) {
        this.snakeEndId = snakeEndId;
    }

    public Integer getSnakeStartId() {
        return snakeStartId;
    }

    public void setSnakeStartId(int snakeStartId) {
        this.snakeStartId = snakeStartId;
    }

    public Integer getLadderEndId() {
        return ladderEndId;
    }

    public void setLadderEndId(int ladderEndId) {
        this.ladderEndId = ladderEndId;
    }

    public Integer getLadderStartId() {
        return ladderStartId;
    }

    public void setLadderStartId(int ladderStartId) {
        this.ladderStartId = ladderStartId;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public void removePlayerFromTile(Player player){
        playersOnTile.remove(player);
    }

    public void addPlayerToTile(Player player){
        playersOnTile.add(player);
    }


    public boolean hasNoShortcuts() {
        return getSnakeEndId() == null && getSnakeStartId() == null && getLadderStartId() == null && getLadderEndId() == null;
    }
}
