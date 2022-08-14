package me.wisemann64.soulland.mobs;

import net.minecraft.server.v1_16_R3.EntityCreature;

public enum MobGenericTypes {

    ZOMBIE(HandleZombie.class),
    SKELETON(HandleSkeleton.class),
    CREEPER(HandleCreeper.class),
    ;
    private final Class<? extends EntityCreature> handleClass;
    MobGenericTypes(Class<? extends EntityCreature> g) {
        handleClass = g;
    }

    public Class<? extends EntityCreature> getHandleClass() {
        return handleClass;
    }
}
