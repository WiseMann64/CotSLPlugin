package me.wisemann64.soulland.gameplay.cutscene;

import me.wisemann64.soulland.system.util.Utils;

public record DialogueLine(int tickAt, String dialogue) {

    public String getMessage() {
        return Utils.color(dialogue);
    }

    @Override
    public String toString() {
        return "Dialogue: [" + tickAt + ":" + dialogue + "]";
    }
}
