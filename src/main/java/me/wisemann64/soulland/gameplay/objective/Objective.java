package me.wisemann64.soulland.gameplay.objective;

import me.wisemann64.soulland.gameplay.GameManager;

import java.util.function.Consumer;

public class Objective {

    private String message = null;
    private final Trigger trigger;

    public Objective(Trigger trigger) {
        this.trigger = trigger;
    }

    public String getMessage() {
        return message;
    }

    public Objective setMessage(String message) {
        this.message = message;
        return this;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public Consumer<GameManager> startEvent() {
        return trigger == null ? null : trigger.startEvent();
    }

    public Consumer<GameManager> finishEvent() {
        return trigger == null ? null : trigger.finishEvent();
    }

    public boolean check(GameManager gm) {
        return trigger == null || trigger.check(gm);
    }
}
