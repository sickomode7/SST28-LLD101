package com.moviebooking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a cinema multiplex.
 * A theatre contains multiple screens.
 */
public class Theatre {

    private final String       theatreId;
    private final String       name;
    private final String       location;
    private final List<Screen> screens;

    public Theatre(String theatreId, String name, String location) {
        this.theatreId = theatreId;
        this.name      = name;
        this.location  = location;
        this.screens   = new ArrayList<>();
    }

    /** Adds a screen to this theatre (called during setup). */
    public void addScreen(Screen screen) {
        screens.add(screen);
    }

    // ── Getters ─────────────────────────────────────────────────────────────
    public String       getTheatreId() { return theatreId; }
    public String       getName()      { return name; }
    public String       getLocation()  { return location; }
    public List<Screen> getScreens()   { return Collections.unmodifiableList(screens); }

    @Override
    public String toString() {
        return String.format("Theatre{id='%s', name='%s', location='%s', screens=%d}",
                theatreId, name, location, screens.size());
    }
}
