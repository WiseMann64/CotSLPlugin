package me.wisemann64.soulland.gameplay.cutscene;

import me.wisemann64.soulland.system.util.Utils;
import org.jetbrains.annotations.NotNull;

public class CutsceneDialogue {

    final int tickAt;
    final String dialogue;

    public CutsceneDialogue(int tickAt, String dialogue) {
        this.tickAt = tickAt;
        this.dialogue = Utils.color(dialogue);
    }

    public int getTickAt() {
        return tickAt;
    }

    public String getText() {
        return dialogue;
    }

    @Override
    public String toString() {
        return "Dialogue: [" + tickAt + ":" + dialogue + "]";
    }
}
