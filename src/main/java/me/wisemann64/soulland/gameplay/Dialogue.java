package me.wisemann64.soulland.gameplay;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.cutscene.DialogueLine;
import me.wisemann64.soulland.system.players.SLPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dialogue {

    final Map<Integer,String> d = new HashMap<>();
    int duration = 0;

    public Dialogue addDialogue(int tick, String text) {
        tick = Math.max(0,tick);
        d.put(tick,text);
        duration = Math.max(duration,tick);
        return this;
    }

    public void play(GameManager gm) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (d.containsKey(tick)) gm.shout(d.get(tick));
                if (tick == duration) cancel();
                tick++;
            }
        }.runTaskTimer(SoulLand.getPlugin(),0,1);
    }

    public void play(SLPlayer pl) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (d.containsKey(tick)) pl.sendMessage(d.get(tick));
                if (tick == duration) cancel();
                tick++;
            }
        }.runTaskTimer(SoulLand.getPlugin(),0,1);
    }
}
