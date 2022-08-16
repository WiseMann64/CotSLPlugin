package me.wisemann64.soulland.gameplay.cutscene;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.GameManager;
import me.wisemann64.soulland.system.players.SLPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Cutscene {

    final List<Frame> frames = new ArrayList<>();
    final List<CutsceneDialogue> dialogues = new ArrayList<>();
    final List<CutsceneEvent> events = new ArrayList<>();
    Consumer<GameManager> finishEvent = null;

    public Cutscene addFrame(Frame frame) {
        frames.add(frame);
        return this;
    }

    public Cutscene addDialogue(CutsceneDialogue dialogue) {
        dialogues.add(dialogue);
        return this;
    }

    public void play() {
        SoulLand.getGameManager().setCurrentCutscene(this);
    }

    public void play(Location endLocation) {
        SoulLand.getGameManager().setCurrentCutscene(this,endLocation);
    }

    public int getTotalDuration() {
       AtomicInteger ret = new AtomicInteger(0);
       frames.forEach(f -> ret.addAndGet(f.duration));
       return ret.get();
    }

    public Frame getFrameAt(int tick) {
        if (tick < 1) return null;
        if (tick > getTotalDuration()) return null;
        Frame ret = null;
        int duration = 0;
        for (Frame f : frames) {
            duration += f.duration;
            if (tick <= duration) {
                ret = f;
                break;
            }
        }
        return ret;
    }

    public Consumer<SLPlayer> getDialogueAt(int tick) {
        List<CutsceneDialogue> a = new ArrayList<>();
        dialogues.forEach(d -> {
            if (d.getTickAt() == tick) a.add(d);
        });
        if (a.isEmpty()) return null;
        return (t) -> a.forEach(msg -> t.sendMessage(msg.dialogue));
    }

    public List<CutsceneEvent> getEventAt(int tick) {
        List<CutsceneEvent> g = new ArrayList<>();
        events.forEach(e -> {
            if (e.tickAt == tick) g.add(e);
        });
        return g;
    }

    public Consumer<GameManager> getFinishEvent() {
        return finishEvent;
    }

    public void setFinishEvent(Consumer<GameManager> finishEvent) {
        this.finishEvent = finishEvent;
    }
}
