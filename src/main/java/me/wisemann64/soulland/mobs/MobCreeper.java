package me.wisemann64.soulland.mobs;

import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.util.List;

public class MobCreeper extends SLMob {

    private double explosionPower = 50;
    private List<String> drops = List.of("1:BOW:1","0.75:PEDANG_LOREM_IPSUM:1~5");

    public MobCreeper(World w, String name) {
        super(w, name, 25);
    }

    public MobCreeper(World w, String name, int level) {
        super(w,name,level);
    }

    @Override
    public HandleCreeper getSLHandler() {
        return (HandleCreeper) handle;
    }

    @Override
    public double getExplosionPower() {
        return explosionPower;
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

    @Override
    public List<String> drops() {
        return drops;
    }
}
