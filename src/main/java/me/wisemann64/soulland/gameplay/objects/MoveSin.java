package me.wisemann64.soulland.gameplay.objects;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MoveSin extends Move {

    final char direction;
    final int duration;
    private int iter = 0;
    final double amplitude;
    final double period;
    final float[] headrot = new float[2];
    final boolean ignoreHeadRotation;
    final boolean lookAtPlayer;

    public MoveSin(int duration, double amplitude, double period, char direction, float yaw, float pitch, boolean ignoreHeadRotation, boolean lookAtPlayer) {
        if (direction != 'x' && direction != 'y' && direction != 'z') throw new IllegalArgumentException("Direction must be x, y or z");
        this.direction = direction;
        this.duration = duration;
        this.amplitude = amplitude;
        this.period = period;
        this.ignoreHeadRotation = ignoreHeadRotation;
        this.lookAtPlayer = lookAtPlayer;
        headrot[0] = yaw;
        headrot[1] = pitch;
    }

    public MoveSin(int duration, double amplitude, double period, char direction) {
        this(duration,amplitude,period,direction,0f,0f,true,false);
    }

    private double f(int x) {
        return amplitude*Math.sin(2*Math.PI/period*x);
    }

    @Override
    public Vector getMovement() {
        double f1 = f(iter);
        iter++;
        double d = f(iter)-f1;
        switch (direction) {
            case 'x' -> {
                return new Vector(d,0,0);
            }
            case 'y' -> {
                return new Vector(0,d,0);
            }
            case 'z' -> {
                return new Vector(0,0,d);
            }
            default -> throw new RuntimeException("Invalid");
        }
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
