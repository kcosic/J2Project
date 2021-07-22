package main.hr.kcosic.project.models;

import javafx.scene.layout.GridPane;

import java.io.Serializable;
import java.util.List;

public class Board implements Serializable {
    private List<Tile> tiles;
    private List<Player> players;
    public Board() {
    }

    public Board(List<Tile> tiles, List<Player> players) {
        this.tiles = tiles;
        this.players = players;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }


    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
