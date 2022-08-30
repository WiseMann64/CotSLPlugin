package me.wisemann64.soulland.gameplay.cutscene;

import me.wisemann64.soulland.gameplay.GameManager;

import java.util.function.Consumer;

public record Action(int tickAt, Consumer<GameManager> action) {

}
