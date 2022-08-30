package me.wisemann64.soulland.gameplay;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.cutscene.Action;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Sequence {

    final List<Action> actions = new ArrayList<>();
    int duration = 0;

    public Sequence addAction(Action a) {
        actions.add(a);
        duration = Math.max(duration,a.tickAt());
        return this;
    }

    public void play(GameManager gm) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                actions.forEach(a -> {
                    if (tick == a.tickAt()) a.action().accept(gm);
                });
                if (tick == duration) cancel();
                tick++;
            }
        }.runTaskTimer(SoulLand.getPlugin(),0,1);
    }
}
