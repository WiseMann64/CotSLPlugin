package me.wisemann64.soulland.system.mobs;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityVillager;
import net.minecraft.server.v1_16_R3.VillagerType;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Villager;

public class ModelVillager extends SLMobModel {

    private final Villager.Type type;
    private final Villager.Profession profession;

    public ModelVillager(World w, String name, String id, Villager.Type type, Villager.Profession profession) {
        super(w, name, id);
        this.type = type;
        this.profession = profession;
    }

    @Override
    public void createHandle(World world) {
        WorldServer ws = ((CraftWorld)world).getHandle();
        this.handle = new EntityVillager(EntityTypes.VILLAGER,ws);
        this.handle.setNoAI(true);
        this.handle.setInvulnerable(true);
        if (name != null) this.handle.getBukkitEntity().setCustomName(ChatColor.translateAlternateColorCodes('&',name));
    }

    @Override
    public void spawn(Location l) {
        super.spawn(l);
        handle.setNoAI(true);
        handle.setInvulnerable(true);
        handle.setSilent(true);
        if (handle.getBukkitEntity() instanceof Villager v) {
            v.setVillagerType(type);
            v.setProfession(profession);
        }
    }
}
