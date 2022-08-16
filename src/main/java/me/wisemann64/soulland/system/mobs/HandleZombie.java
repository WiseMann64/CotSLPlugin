package me.wisemann64.soulland.system.mobs;

import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityZombie;
import net.minecraft.server.v1_16_R3.World;

public class HandleZombie extends EntityZombie {

    private final SLMob handler;

    public HandleZombie(World world, MobZombie handler) {
        super(EntityTypes.ZOMBIE ,world);
        this.handler = handler;
        setPersistent();
        setCustomNameVisible(true);
    }

    public MobZombie getHandler() {
        return (MobZombie) handler;
    }

    @Override
    public boolean damageEntity(DamageSource damageSource, float f) {
        this.noDamageTicks = 0;
        return super.damageEntity(damageSource,f);
    }
}
