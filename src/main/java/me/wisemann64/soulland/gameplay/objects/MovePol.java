package me.wisemann64.soulland.gameplay.objects;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MovePol extends Move {

    final int duration;
    final double velocity;
    final float[] headrot = new float[2];
    final boolean ignoreHeadRotation;
    final boolean lookAtPlayer;

    public MovePol(int duration, float yaw, float pitch, double velocity, boolean ignoreHeadRotation, boolean lookAtPlayer) {
        this.duration = duration;
        headrot[0] = yaw;
        headrot[1] = pitch;
        this.velocity = velocity;
        this.ignoreHeadRotation = ignoreHeadRotation;
        this.lookAtPlayer = lookAtPlayer;
    }

    public MovePol(int duration, float yaw, float pitch, double velocity) {
        this(duration,yaw,pitch,velocity,false,false);
    }

    @Override
    public Vector getMovement() {
        return getHeadRotation().normalize().multiply(velocity);
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean isLookAtPlayer() {
        return lookAtPlayer;
    }

    @Override
    public boolean ignoreHeadRotation() {
        return ignoreHeadRotation;
    }

    @Override
    public Vector getHeadRotation() {
        return new Location(null,0,0,0,headrot[0],headrot[1]).getDirection();
    }
}
