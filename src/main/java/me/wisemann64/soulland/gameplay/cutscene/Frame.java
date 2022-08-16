package me.wisemann64.soulland.gameplay.cutscene;

import org.bukkit.Location;

public class Frame {

    final int duration;
    final Location loc;

    public Frame(int duration, Location loc) {
        this.duration = duration;
        this.loc = loc;
    }

    public int getDuration() {
        return duration;
    }

    public Location getLocation() {
        return loc;
    }
}
