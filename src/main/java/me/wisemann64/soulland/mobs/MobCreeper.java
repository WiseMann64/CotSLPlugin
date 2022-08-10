package me.wisemann64.soulland.mobs;

import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class MobCreeper extends SLMob {

    public MobCreeper(World w, String name) {
        super(w, name, 25);
    }

    @Override
    public HandleZombie getSLHandler() {
        return (HandleZombie) handle;
    }

    @Override
    public double getExplosionPower() {
        return 50;
    }

    @Override
    public void createHandle(World world, String name) {
        WorldServer ws = ((CraftWorld)world).getHandle();
        this.handle = new HandleCreeper(ws,this);
    }

    @Override
    public void initAttribute() {
        MobAttributes a = getAttributes();
        a.setMaxHealth(120);
        a.setHealth(120);
        a.setAttackPower(0);
        a.setDefense(130);
        a.setMagicAttack(0);
        a.setMagicDefense(270);
        a.setMagicPEN(0);
        a.setPhysicalPEN(115);
    }
}
