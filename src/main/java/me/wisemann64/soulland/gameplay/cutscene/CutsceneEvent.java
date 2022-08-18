package me.wisemann64.soulland.gameplay.cutscene;

import me.wisemann64.soulland.gameplay.GameManager;

import java.util.function.Consumer;

public record CutsceneEvent(int tickAt, Consumer<GameManager> action) {

}
