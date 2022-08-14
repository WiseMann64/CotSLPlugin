package me.wisemann64.soulland.mobs;

import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.util.List;

public class MobZombie extends SLMob {

    public MobZombie(World w, String name) {
        super(w, name, 0);
    }

    @Override
    public HandleZombie getMobHandle() {
        return (HandleZombie) handle;
    }

    @Override
    public double getExplosionPower() {
        return 0;
    }

    @Override
    public void createHandle(World world) {
        WorldServer ws = ((CraftWorld)world).getHandle();
        this.handle = new HandleZombie(ws,this);
    }

    @Override
    public void initAttribute() {
        MobAttributes a = getAttributes();
        a.setMaxHealth(20);
        a.setHealth(20);
        a.setAttackPower(4);
        a.setDefense(0);
        a.setMagicAttack(0);
        a.setMagicDefense(0);
        a.setMagicPEN(0);
        a.setPhysicalPEN(0);
    }

    @Override
    public List<String> drops() {
        return List.of("1:BOW:1","0.75:PEDANG_LOREM_IPSUM:1~5");
    }
}
