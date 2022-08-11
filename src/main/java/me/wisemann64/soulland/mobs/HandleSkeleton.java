package me.wisemann64.soulland.mobs;

import me.wisemann64.soulland.items.SLItems;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.inventory.ItemStack;

public class HandleSkeleton extends EntitySkeleton {

    private final MobSkeleton handler;

    public HandleSkeleton(World world, MobSkeleton handler) {
        super(EntityTypes.SKELETON,world);
        this.handler = handler;
        setCustomNameVisible(true);
        setPersistent();
        setSlot(EnumItemSlot.MAINHAND, SLItems.nms(new ItemStack(org.bukkit.Material.BOW)),true);
    }

    @Override
    public boolean damageEntity(DamageSource damageSource, float f) {
        this.noDamageTicks = 0;
        return super.damageEntity(damageSource,f);
    }
}
