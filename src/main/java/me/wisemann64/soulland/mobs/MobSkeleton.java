package me.wisemann64.soulland.mobs;

import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.util.List;

public class MobSkeleton extends SLMob {

    public MobSkeleton(World w, String name) {
        super(w, name, 35);
    }

    @Override
    public HandleSkeleton getMobHandle() {
        return (HandleSkeleton) handle;
    }

    @Override
    public double getExplosionPower() {
        return 0;
    }

    @Override
    public void createHandle(World world) {
        WorldServer ws = ((CraftWorld)world).getHandle();
        this.handle = new HandleSkeleton(ws,this);
    }

    @Override
    public void initAttribute() {
        MobAttributes a = getAttributes();
        a.setMaxHealth(80);
        a.setHealth(80);
        a.setAttackPower(16);
        a.setDefense(14);
        a.setMagicAttack(0);
        a.setMagicDefense(28);
        a.setMagicPEN(0);
        a.setPhysicalPEN(128);
    }

    @Override
    public List<String> drops() {
        return List.of("1:BOW:1","0.75:PEDANG_LOREM_IPSUM:1~5");
    }
    @Override
    public int getXpYield() {
        return 25;
    }
}
