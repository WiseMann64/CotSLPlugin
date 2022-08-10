package me.wisemann64.soulland.mobs;

import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityZombie;
import net.minecraft.server.v1_16_R3.World;

public class HandleZombie extends EntityZombie {

    private final MobZombie handler;

    public HandleZombie(World world, MobZombie handler) {
        super(world);
        this.handler = handler;
        setCustomNameVisible(true);
        setPersistent();
    }

    @Override
    public boolean damageEntity(DamageSource damageSource, float f) {
        this.noDamageTicks = 0;
        return super.damageEntity(damageSource,f);
    }
}
