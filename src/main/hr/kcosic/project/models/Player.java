package main.hr.kcosic.project.models;

import java.io.Serializable;

public class Player implements Serializable {
    private final int id;
    private String name;
    private String color;
    private Tile currentTile;

    private int playerRow;
    private int playerColumn;

    public Player(int id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public void setPlayerRow(int playerRow) {
        this.playerRow = playerRow;
    }

    public int getPlayerColumn() {
        return playerColumn;
    }

    public void setPlayerColumn(int playerColumn) {
        this.playerColumn = playerColumn;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }
}
