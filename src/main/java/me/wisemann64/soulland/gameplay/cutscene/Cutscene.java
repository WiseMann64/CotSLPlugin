package me.wisemann64.soulland.gameplay.cutscene;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.GameManager;
import me.wisemann64.soulland.gameplay.GameplayEvent;
import me.wisemann64.soulland.system.players.SLPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Cutscene implements GameplayEvent {

    final List<Frame> frames = new ArrayList<>();
    final List<CutsceneDialogue> dialogues = new ArrayList<>();
    final List<CutsceneEvent> events = new ArrayList<>();
    String finishEventRef = null;
    String startEventRef = null;
    Consumer<GameManager> finishEvent = null;
    Consumer<GameManager> startEvent = null;

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
                duration -= f.duration;
                break;
            }
        }
        if (ret == null) return null;
        return ret.frameAt(tick-duration);
    }

    public Consumer<SLPlayer> getDialogueAt(int tick) {
        List<CutsceneDialogue> a = new ArrayList<>();
        dialogues.forEach(d -> {
            if (d.tickAt() == tick) a.add(d);
        });
        if (a.isEmpty()) return null;
        return (t) -> a.forEach(msg -> t.sendMessage(msg.getMessage()));
    }

    public List<CutsceneEvent> getEventAt(int tick) {
        List<CutsceneEvent> g = new ArrayList<>();
        events.forEach(e -> {
            if (e.tickAt() == tick) g.add(e);
        });
        return g;
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
    public Cutscene setFinishEvent(Consumer<GameManager> action) {
        finishEventRef = null;
        finishEvent = action;
        return this;
    }

    @Override
    public Cutscene setStartEvent(Consumer<GameManager> action) {
        startEventRef = null;
        startEvent = action;
        return this;
    }

    @Override
    public Cutscene setFinishEventReference(String ref) {
        finishEvent = null;
        finishEventRef = ref;
        return this;
    }

    @Override
    public Cutscene setStartEventReference(String ref) {
        startEvent = null;
        startEventRef = ref;
        return this;
    }
}
