package com.snakesandladders.model;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private int size;
    private int finishPosition;
    private Map<Integer, Snake> snakes;
    private Map<Integer, Ladder> ladders;

    public Board(int n) {
        this.size = n;
        this.finishPosition = n * n;
        this.snakes = new HashMap<>();
        this.ladders = new HashMap<>();
    }

    public int getSize() {
        return size;
    }

    public int getFinishPosition() {
        return finishPosition;
    }

    public Map<Integer, Snake> getSnakes() {
        return snakes;
    }

    public void setSnakes(Map<Integer, Snake> snakes) {
        this.snakes = snakes;
    }

    public Map<Integer, Ladder> getLadders() {
        return ladders;
    }

    public void setLadders(Map<Integer, Ladder> ladders) {
        this.ladders = ladders;
    }
}
