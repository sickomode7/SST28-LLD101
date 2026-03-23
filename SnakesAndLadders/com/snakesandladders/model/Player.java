package com.snakesandladders.model;

public class Player {
    private String name;
    private int id;
    private int position;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.position = 0; // Each player initially kept outside the board
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
