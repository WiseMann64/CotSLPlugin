package me.wisemann64.soulland.gameplay.objects;

import org.bukkit.util.Vector;

public abstract class Move {

    public abstract Vector getMovement();
    public abstract int getDuration();
    public abstract boolean isLookAtPlayer();
    public abstract boolean ignoreHeadRotation();
    public abstract Vector getHeadRotation();

}
