package main.hr.kcosic.project.models;

import main.hr.kcosic.project.models.enums.SvgEnum;

import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {
    private final int id;
    private String name;
    private String color;

    private SvgEnum pawn;
    private Tile currentTile;

    private int playerRow;
    private int playerColumn;

    public Player(int id, String name, String color, SvgEnum pawn) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.pawn = pawn;
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

    public SvgEnum getPawn() {
        return pawn;
    }

    public void setPawn(SvgEnum pawn) {
        this.pawn = pawn;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
