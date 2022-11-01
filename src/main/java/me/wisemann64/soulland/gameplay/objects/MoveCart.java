package me.wisemann64.soulland.gameplay.objects;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MoveCart extends Move {

    final int duration;
    final double[] xyz = new double[3];
    final float[] headrot = new float[2];
    final boolean ignoreHeadRotation;
    final boolean lookAtPlayer;

    public MoveCart(int duration, double x, double y, double z, float yaw, float pitch, boolean ignoreHeadRotation, boolean lookAtPlayer) {
        this.duration = duration;
        xyz[0] = x;
        xyz[1] = y;
        xyz[2] = z;
        headrot[0] = yaw;
        headrot[1] = pitch;
        this.ignoreHeadRotation = ignoreHeadRotation;
        this.lookAtPlayer = lookAtPlayer;
    }

    public MoveCart(int duration, double x, double y, double z, float yaw, float pitch) {
        this(duration,x,y,z,yaw,pitch,false,false);
    }

    @Override
    public Vector getMovement() {
        return new Vector(xyz[0],xyz[1],xyz[2]);
    }

    @Override
    public Vector getHeadRotation() {
        return new Location(null,0,0,0,headrot[0],headrot[1]).getDirection();
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
}
