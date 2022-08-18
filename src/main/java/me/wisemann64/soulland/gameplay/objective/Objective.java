package me.wisemann64.soulland.gameplay.objective;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.GameManager;
import me.wisemann64.soulland.gameplay.GameplayEvent;

import java.util.function.Consumer;

public abstract class Objective implements GameplayEvent {

    protected boolean completed = false;
    protected String message = null;

    protected String finishEventRef = null;
    protected String startEventRef = null;

    protected Consumer<GameManager> finishEvent = null;
    protected Consumer<GameManager> startEvent = null;

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public abstract boolean check(GameManager game);

    @Override
    public Consumer<GameManager> finishEvent() {
        return finishEvent == null ? SoulLand.getObjectParser().getEventOrAction(finishEventRef) : finishEvent;
    }

    @Override
    public Consumer<GameManager> startEvent() {
        return startEvent == null ? SoulLand.getObjectParser().getEventOrAction(startEventRef) : startEvent;
    }

    @Override
    public Objective setFinishEvent(Consumer<GameManager> action) {
        finishEventRef = null;
        finishEvent = action;
        return this;
    }

    @Override
    public Objective setStartEvent(Consumer<GameManager> action) {
        startEventRef = null;
        startEvent = action;
        return this;
    }

    @Override
    public Objective setFinishEventReference(String ref) {
        finishEvent = null;
        finishEventRef = ref;
        return this;
    }

    @Override
    public Objective setStartEventReference(String ref) {
        startEvent = null;
        startEventRef = ref;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
