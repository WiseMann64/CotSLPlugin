package me.wisemann64.soulland.gameplay.cutscene;

import org.bukkit.Location;

public class MovingFrame extends Frame {

    private final Location end;

    public MovingFrame(int duration, Location start, Location end) {
        super(duration, start);
        this.end = end;
    }

    @Override
    public Frame frameAt(int tick) {
        double ratio = Math.min(1,Math.max(0,(double)tick/duration));
        Location l = loc.clone().add(end.toVector().subtract(loc.toVector()).multiply(ratio));
        return new Frame(0,l);
    }
}
