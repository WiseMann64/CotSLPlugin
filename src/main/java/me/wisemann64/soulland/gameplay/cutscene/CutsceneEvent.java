package me.wisemann64.soulland.gameplay.cutscene;

import me.wisemann64.soulland.gameplay.GameManager;

import java.util.function.Consumer;

public class CutsceneEvent {

    final int tickAt;
    final Consumer<GameManager> action;

    public CutsceneEvent(int tickAt, Consumer<GameManager> action) {
        this.tickAt = tickAt;
        this.action = action;
    }

    public int getTickAt() {
        return tickAt;
    }

    public Consumer<GameManager> getAction() {
        return action;
    }
}
