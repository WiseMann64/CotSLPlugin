package me.wisemann64.soulland.system.mobs;

import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class ModelGeneric extends SLMobModel {

    private final ModelGenerator.ModelType type;

    public ModelGeneric(World w, ModelGenerator.ModelType type, String name, String id) {
        super(w, name, id, true);
        this.type = type;
        createHandle(w);
    }

    @Override
    public void createHandle(World world) {
        WorldServer ws = ((CraftWorld)world).getHandle();
        handle = ModelGenerator.generate(type, ws);
        if (handle == null) throw new RuntimeException("Failed to create model!");
        handle.setNoAI(true);
        handle.setInvulnerable(true);
        handle.setSilent(true);
        if (name != null) this.handle.getBukkitEntity().setCustomName(ChatColor.translateAlternateColorCodes('&',name));
    }
}
