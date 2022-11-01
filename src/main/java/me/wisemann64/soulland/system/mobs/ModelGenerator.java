package me.wisemann64.soulland.system.mobs;

import net.minecraft.server.v1_16_R3.*;

public class ModelGenerator {

    public enum ModelType {
        VILLAGER, WITHER_SKELETON,
    }

    public static EntityCreature generate(ModelType type, World world) {
        return switch (type) {
            case VILLAGER -> new EntityVillager(EntityTypes.VILLAGER,world);
            case WITHER_SKELETON -> new EntitySkeletonWither(EntityTypes.WITHER_SKELETON,world);
        };
    }
}
