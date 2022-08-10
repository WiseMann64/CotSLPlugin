package me.wisemann64.soulland.mobs;

import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class MobZombie extends SLMob {

    public MobZombie(World w, String name) {
        super(w, name, 5);
    }

    @Override
    public HandleZombie getSLHandler() {
        return (HandleZombie) handle;
    }

    @Override
    public double getExplosionPower() {
        return 0;
    }

    @Override
    public void createHandle(World world, String name) {
        WorldServer ws = ((CraftWorld)world).getHandle();
        this.handle = new HandleZombie(ws,this);
    }

    @Override
    public void initAttribute() {
        MobAttributes a = getAttributes();
        a.setMaxHealth(40);
        a.setHealth(40);
        a.setAttackPower(5);
        a.setDefense(20);
        a.setMagicAttack(0);
        a.setMagicDefense(20);
        a.setMagicPEN(70);
        a.setPhysicalPEN(70);
    }
}
