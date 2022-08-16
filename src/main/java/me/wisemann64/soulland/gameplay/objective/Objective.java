package me.wisemann64.soulland.gameplay.objective;

import me.wisemann64.soulland.gameplay.GameManager;

import java.util.function.Consumer;

public abstract class Objective {

    protected boolean completed = false;
    protected Consumer<GameManager> finishEvent = null;
    protected Consumer<GameManager> startEvent = null;

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public abstract boolean check(GameManager game);
    public void setFinishEvent(Consumer<GameManager> action) {
        finishEvent = action;
    }
    public Consumer<GameManager> getFinishEvent() {
        return finishEvent;
    }

    public Consumer<GameManager> getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(Consumer<GameManager> startEvent) {
        this.startEvent = startEvent;
    }
}
