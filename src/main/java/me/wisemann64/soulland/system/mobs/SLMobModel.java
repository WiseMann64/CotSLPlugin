package me.wisemann64.soulland.system.mobs;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.objects.Moveset;
import net.minecraft.server.v1_16_R3.EntityCreature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

public abstract class SLMobModel {

    protected final CraftServer server = ((CraftServer) Bukkit.getServer());
    protected EntityCreature handle;
    protected final String id;
    protected final String name;

    public SLMobModel(World w, String name, String id, boolean delayCreate) {
        this.id = SoulLand.getMobManager().putModelToRegistry(this,id);
        this.name = name;
        if (!delayCreate) createHandle(w);
    }

    public SLMobModel(World w, String name, String id) {
        this(w,name,id,false);
    }

    public Entity getHandle() {
        return handle.getBukkitEntity();
    }

    public static void setLocation(net.minecraft.server.v1_16_R3.Entity handle, Location loc) {
        handle.setLocation(loc.getX(),loc.getY(),loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public void spawn(Location l) {
        if (handle == null) return;
        setLocation(handle,l);
        ((CraftWorld)l.getWorld()).getHandle().addEntity(handle);
        teleport(l);
        handle.setNoAI(true);
        handle.setInvulnerable(true);
        handle.setPersistent();
    }

    public void remove() {
        if (getHandle() instanceof LivingEntity g) g.setHealth(0);
        else getHandle().remove();
        SoulLand.getMobManager().removeModelFromRegistry(id);
    }

    public abstract void createHandle(World world);

    public void teleport(Location location) {
        handle.getBukkitEntity().teleport(location);
    }

    public void applyMoveset(Moveset ms) {
        ms.apply(this);
    }

    public EntityEquipment getEquipment() {
        if (getHandle() instanceof LivingEntity e) {
            return e.getEquipment();
        }
        return null;
    }
}
