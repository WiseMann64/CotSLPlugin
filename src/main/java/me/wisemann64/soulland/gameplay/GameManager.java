package me.wisemann64.soulland.gameplay;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.cutscene.Cutscene;
import me.wisemann64.soulland.gameplay.cutscene.Frame;
import me.wisemann64.soulland.gameplay.objective.Objective;
import me.wisemann64.soulland.gameplay.objective.Trigger;
import me.wisemann64.soulland.gameplay.objects.ObjectParser;
import me.wisemann64.soulland.gameplay.objects.ObjectSource;
import me.wisemann64.soulland.gameplay.objects.Sequence;
import me.wisemann64.soulland.system.players.SLPlayer;
import me.wisemann64.soulland.system.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GameManager {

    private final List<SLPlayer> registeredPlayers = new ArrayList<>();
    private ObjectParser op() {
        return SoulLand.getObjectParser();
    } 
    
    private GameState state;

    private Cutscene currentCutscene = null;
    private int cutsceneTick = 0;
    private Map<SLPlayer,Location> cutsceneAfterPosition = new HashMap<>();

    private Objective currentObjective = null;
    private final Map<String,Trigger> activeTriggers = new HashMap<>();


    public GameManager() {
        state = GameState.PAUSE;
    }

    public void mainTick() {
        switch (state) {
            case TESTING -> demoTick();
        }
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
        switch (state) {
            case TESTING -> startDemo();
        }
    }

    private void startDemo() {
        registeredPlayers.clear();
        registeredPlayers.addAll(SoulLand.getPlayerManager().getPlayers());
        Cutscene c = op().getCutscene("intro_cutscene", ObjectSource.TEST_PROLOGUE);
        c.play();
    }

    public void setCurrentCutscene(Cutscene c) {
        cutsceneAfterPosition = new HashMap<>();
        registeredPlayers.forEach(p -> {
            cutsceneAfterPosition.put(p, p.getLocation());
            p.getHandle().setGameMode(GameMode.SPECTATOR);
        });
        cutsceneTick = 0;
        currentCutscene = c;
    }

    public void setCurrentCutscene(Cutscene c, Location endPosition) {
        setCurrentCutscene(c);
        cutsceneAfterPosition = new HashMap<>();
        registeredPlayers.forEach(p -> cutsceneAfterPosition.put(p,endPosition));
    }

    public void stopCutscene() {
        if (currentCutscene == null) return;
        cutsceneAfterPosition.forEach((p,l) -> {
            p.getHandle().setGameMode(GameMode.SURVIVAL);
            p.getHandle().teleport(l);
        });
        if (currentCutscene.finishEvent() != null) {
            currentCutscene.finishEvent().accept(this);
        }
        currentCutscene = null;
        cutsceneTick = 0;
    }

    private void cutsceneTick() {
        cutsceneTick++;
        if (cutsceneTick > currentCutscene.getTotalDuration()) {
            stopCutscene();
            return;
        }
        Frame f = currentCutscene.getFrameAt(cutsceneTick);
        registeredPlayers.forEach(p -> p.getHandle().teleport(f.getLocation()));
        Consumer<SLPlayer> c = currentCutscene.getDialogueAt(cutsceneTick);
        currentCutscene.getEventAt(cutsceneTick).forEach(e -> e.action().accept(this));
        if (c != null) registeredPlayers.forEach(c);
    }

    private void objectiveTick() {
        boolean b = currentObjective.check(this);
        if (b) finishObjective();
    }

    public void setObjective(Objective o) {
        String prevMsg = currentObjective == null ? null : currentObjective.getMessage();
        currentObjective = o;
        if (o.getMessage() != null && !o.getMessage().equals(prevMsg)) shout("&eObjective &6&l>> &r" + o.getMessage());
        if (o.startEvent() != null) o.startEvent().accept(this);
    }

    private void finishObjective() {
        Consumer<GameManager> t = currentObjective.finishEvent();
        currentObjective = null;
        if (t != null) t.accept(this);
    }

    private void triggerTick() {
        activeTriggers.entrySet().removeIf(e -> e.getValue().isFinished());
        activeTriggers.forEach((key,t) -> {
            boolean b = t.check(this);
            if (b) {
                t.finish();
                if (t.finishEvent() != null) t.finishEvent().accept(this);
            }
        });
    }

    public void addTrigger(Trigger t) {
        activeTriggers.put(t.identifier(),t);
        if (t.startEvent() != null) t.startEvent().accept(this);
    }

    private void demoTick() {
        // Cutscene
        if (currentCutscene != null) cutsceneTick();

        // Objective
        if (currentObjective != null) objectiveTick();

        // Trigger
        if (activeTriggers.size() != 0) triggerTick();
    }

    public List<SLPlayer> getRegisteredPlayers() {
        return registeredPlayers;
    }

    public void shout(String msg) {
        registeredPlayers.forEach(p -> p.sendMessage(Utils.color(msg)));
    }

    public void action(@NotNull Consumer<SLPlayer> action) {
        registeredPlayers.forEach(action);
    }
    public void playSequence(@NotNull Sequence seq) {
        seq.play(this);
    }
}
