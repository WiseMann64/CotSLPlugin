package me.wisemann64.soulland.system.mobs;

import net.minecraft.server.v1_16_R3.EntitySkeletonWither;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class ModelWitherSkeleton extends SLMobModel {

    public ModelWitherSkeleton(World w, String name, String id) {
        super(w, name, id);
    }

    @Override
    public void createHandle(World world) {
        WorldServer ws = ((CraftWorld)world).getHandle();
        this.handle = new EntitySkeletonWither(EntityTypes.WITHER_SKELETON,ws);
        this.handle.setCustomNameVisible(name != null);
        this.handle.setNoAI(true);
        this.handle.setInvulnerable(true);
        if (name != null) this.handle.getBukkitEntity().setCustomName(ChatColor.translateAlternateColorCodes('&',name));
    }
}
