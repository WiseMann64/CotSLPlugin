package me.wisemann64.soulland.gameplay.objective;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.GameManager;
import me.wisemann64.soulland.gameplay.GameplayEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class Trigger implements GameplayEvent {

    protected String finishEventRef = null;
    protected String startEventRef = null;

    protected Consumer<GameManager> finishEvent = null;
    protected Consumer<GameManager> startEvent = null;
    
    protected boolean finished = false;
    @NotNull private String identifier = "";

    public abstract boolean check(GameManager game);
    
    public void finish() {
        finished = true;
    }
    
    public boolean isFinished() {
        return finished;
    }

    @Override
    public Consumer<GameManager> finishEvent() {
        return finishEvent == null ? SoulLand.getObjectParser().getEventOrAction(finishEventRef) : finishEvent;
    }

    @Override
    public Consumer<GameManager> startEvent() {
        return startEvent == null ? SoulLand.getObjectParser().getEventOrAction(startEventRef) : startEvent;
    }

    @Override
    public Trigger setFinishEvent(Consumer<GameManager> action) {
        finishEventRef = null;
        finishEvent = action;
        return this;
    }

    @Override
    public Trigger setStartEvent(Consumer<GameManager> action) {
        startEventRef = null;
        startEvent = action;
        return this;
    }

    @Override
    public Trigger setFinishEventReference(String ref) {
        finishEvent = null;
        finishEventRef = ref;
        return this;
    }

    @Override
    public Trigger setStartEventReference(String ref) {
        startEvent = null;
        startEventRef = ref;
        return this;
    }

    public String identifier() {
        return identifier;
    }

    public void setIdentifier(@NotNull String identifier) {
        this.identifier = identifier;
    }
}
