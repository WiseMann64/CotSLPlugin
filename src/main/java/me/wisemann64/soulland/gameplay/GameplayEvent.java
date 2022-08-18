package me.wisemann64.soulland.gameplay;

import java.util.function.Consumer;

public interface GameplayEvent {

   Consumer<GameManager> finishEvent();
   Consumer<GameManager> startEvent();

   GameplayEvent setFinishEvent(Consumer<GameManager> action);
   GameplayEvent setStartEvent(Consumer<GameManager> action);

    GameplayEvent setFinishEventReference(String ref);
    GameplayEvent setStartEventReference(String ref);

}
