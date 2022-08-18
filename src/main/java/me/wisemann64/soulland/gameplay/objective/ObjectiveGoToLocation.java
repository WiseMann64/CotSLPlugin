package me.wisemann64.soulland.gameplay.objective;

import me.wisemann64.soulland.gameplay.GameManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.concurrent.atomic.AtomicBoolean;

public class ObjectiveGoToLocation extends Objective {

    private final BoundingBox destination;
    private final World world;

    public ObjectiveGoToLocation(BoundingBox destination, World world) {
        this.destination = destination;
        this.world = world;
    }

    public BoundingBox getDestination() {
        return destination;
    }

    @Override
    public boolean check(GameManager game) {
        if (completed) return true;
        AtomicBoolean b = new AtomicBoolean(false);
        game.getRegisteredPlayers().forEach(p -> {
            Location l = p.getLocation();
            if (world.equals(l.getWorld()) && destination.contains(l.getX(),l.getY(),l.getZ())) b.set(true);
        });
        return b.get();
    }
}
