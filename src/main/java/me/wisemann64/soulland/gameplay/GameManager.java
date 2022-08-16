package me.wisemann64.soulland.gameplay;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.cutscene.Cutscene;
import me.wisemann64.soulland.gameplay.cutscene.CutsceneDialogue;
import me.wisemann64.soulland.gameplay.cutscene.Frame;
import me.wisemann64.soulland.gameplay.objective.Objective;
import me.wisemann64.soulland.gameplay.objective.ObjectiveGoToLocation;
import me.wisemann64.soulland.gameplay.objective.ObjectiveKillMob;
import me.wisemann64.soulland.system.mobs.MobGeneric;
import me.wisemann64.soulland.system.mobs.MobGenericTypes;
import me.wisemann64.soulland.system.players.SLPlayer;
import me.wisemann64.soulland.system.players.Stats;
import me.wisemann64.soulland.system.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class GameManager {

    private final List<SLPlayer> registeredPlayers = new ArrayList<>();

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
        Cutscene c = new Cutscene();
        c.addFrame(new Frame(60,new Location(Bukkit.getWorld("world"),-173.5,98.5,-213.5,52.5F,26.0F)));
        c.addDialogue(new CutsceneDialogue(10, "Halo Ngab"));
        c.addDialogue(new CutsceneDialogue(10, "Halo Ngabzz"));
        c.addDialogue(new CutsceneDialogue(10, "Halo Ngabzzz"));
        c.addDialogue(new CutsceneDialogue(5, "^_^"));
        c.addDialogue(new CutsceneDialogue(45, "Mantap!"));
        c.addDialogue(new CutsceneDialogue(120, "ga keliatan wkwkwk"));
        c.setFinishEvent(gm -> {
            BoundingBox bb = new BoundingBox(-226.0,63.0,-192.0,-214.0,70.0,-178.0);
            ObjectiveGoToLocation g = new ObjectiveGoToLocation(bb,Bukkit.getWorld("world"));
            g.setFinishEvent(gm1 -> {
                Location l = new Location(Bukkit.getWorld("world"),-220.0,64.0,-181.5,-180.0f,0.0f);
                MobGeneric.Customizer cust = new MobGeneric.Customizer(MobGenericTypes.ZOMBIE);
                cust.setXp(30);
                cust.setName("Pak RT");
                cust.setLevel(40);
                cust.getInitStats().put(Stats.HEALTH,200D);
                cust.getInitStats().put(Stats.ATK,50D);
                ObjectiveKillMob obj = new ObjectiveKillMob(l,cust);
                obj.setFinishEvent(gm2 -> shout("Beres Ngab!"));
                gm1.setObjective(obj);
            });
            gm.setObjective(g);
        });
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
        if (currentCutscene.getFinishEvent() != null) currentCutscene.getFinishEvent().accept(this);
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
        if (c != null) registeredPlayers.forEach(c);
    }

    private void objectiveTick() {
        boolean b = currentObjective.check(this);
        if (b) finishObjective();
    }

    public void setObjective(Objective o) {
        currentObjective = o;
    }

    private void finishObjective() {
        Consumer<GameManager> t = currentObjective.getFinishEvent();
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
