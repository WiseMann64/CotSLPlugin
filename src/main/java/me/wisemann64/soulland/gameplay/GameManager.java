package me.wisemann64.soulland.gameplay;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.cutscene.Cutscene;
import me.wisemann64.soulland.gameplay.cutscene.Frame;
import me.wisemann64.soulland.gameplay.objective.Objective;
import me.wisemann64.soulland.gameplay.objects.ObjectParser;
import me.wisemann64.soulland.gameplay.objects.ObjectSource;
import me.wisemann64.soulland.system.players.SLPlayer;
import me.wisemann64.soulland.system.util.Utils;
import org.bukkit.Bukkit;
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
        Cutscene c = op().getCutscene("cs1", ObjectSource.DEMO_OBJECTS);
        c.play(new Location(Bukkit.getWorld("world"),-204.5,67,-202.5,90F,-15F));
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
        if (currentCutscene.finishEvent() != null) currentCutscene.finishEvent().accept(this);
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
        currentObjective = o;
        if (o.getMessage() != null) shout(o.getMessage());
        if (o.startEvent() != null) o.startEvent().accept(this);
    }

    private void finishObjective() {
        Consumer<GameManager> t = currentObjective.finishEvent();
        currentObjective = null;
        if (t != null) t.accept(this);
    }

    private void demoTick() {
        // Cutscene
        if (currentCutscene != null) cutsceneTick();

        // Objective
        if (currentObjective != null) objectiveTick();
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
}
