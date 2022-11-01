package me.wisemann64.soulland.gameplay.objects;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.mobs.SLMob;
import me.wisemann64.soulland.system.mobs.SLMobModel;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Moveset {

    private int duration = 0;
    private final List<Move> moves = new ArrayList<>();

    public void addMove(Move move) {
        moves.add(move);
        duration += move.getDuration();
    }

    public int getDuration() {
        return duration;
    }

    public List<Move> getMoves() {
        return moves;
    }

    private Move at(int at) {
        int loop = 0;
        for (Move move: moves) {
            loop += move.getDuration();
            if (at <= loop) return move;
        }
        return null;
    }

    public void apply(@NotNull SLMob mob) {
        if (mob.getHandle().isDead()) return;
        boolean aii = false;
        if (mob.getHandle() instanceof LivingEntity le) if (((CraftLivingEntity) le).getHandle() instanceof EntityInsentient ei) {
            aii = !ei.isNoAI();
            if (aii) ei.setNoAI(true);
        }

        boolean finalAii = aii;
        new BukkitRunnable() {

            int tick = 1;
            final boolean ai = finalAii;

            @Override
            public void run() {

                if (mob.getHandle().isDead()) cancel();
                else {
                    Move move = at(tick);

                    if (move == null) {
                        if (ai) ((EntityInsentient) ((CraftLivingEntity) mob.getHandle()).getHandle()).setNoAI(false);
                        cancel();
                    }
                    else {

                        Vector vector = move.getMovement();
                        mob.getHandle().teleport(mob.getHandle().getLocation().clone().add(vector));
                        if (move.isLookAtPlayer()) {
                            // TODO
                        } else if (move.ignoreHeadRotation()) {
                            mob.getHandle().teleport(mob.getHandle().getLocation().setDirection(move.getHeadRotation()));
                        } else {
                            mob.getHandle().teleport(mob.getHandle().getLocation().setDirection(vector));
                        }
                        tick++;

                        if (tick == duration) {
                            if (ai) ((EntityInsentient) ((CraftLivingEntity) mob.getHandle()).getHandle()).setNoAI(false);
                            cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(SoulLand.getPlugin(),0L,1L);
    }

    public void apply(@NotNull SLMobModel model) {
        if (model.getHandle().isDead()) return;
        new BukkitRunnable() {

            int tick = 1;

            @Override
            public void run() {

                if (model.getHandle().isDead()) cancel();
                else {
                    Move move = at(tick);

                    if (move == null) cancel();
                    else {

                        Vector vector = move.getMovement();
                        model.getHandle().teleport(model.getHandle().getLocation().clone().add(vector));
                        if (move.isLookAtPlayer()); // TODO;
                        else if (move.ignoreHeadRotation()) model.getHandle().teleport(model.getHandle().getLocation().setDirection(move.getHeadRotation()));
                        else model.getHandle().teleport(model.getHandle().getLocation().setDirection(vector));
                        tick++;

                        if (tick == duration) cancel();
                    }
                }
            }
        }.runTaskTimer(SoulLand.getPlugin(),0L,1L);
    }
}
