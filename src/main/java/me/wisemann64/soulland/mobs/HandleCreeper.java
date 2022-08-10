package me.wisemann64.soulland.mobs;

import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityCreeper;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;

public class HandleCreeper extends EntityCreeper {

    private final MobCreeper handler;

    public HandleCreeper(World world, MobCreeper handler) {
        super(EntityTypes.CREEPER, world);
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
