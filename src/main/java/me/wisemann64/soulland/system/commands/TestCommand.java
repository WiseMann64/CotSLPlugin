package me.wisemann64.soulland.system.commands;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.objects.*;
import me.wisemann64.soulland.system.combat.Damage;
import me.wisemann64.soulland.system.combat.DamageType;
import me.wisemann64.soulland.system.items.ItemAbstract;
import me.wisemann64.soulland.system.items.SLItems;
import me.wisemann64.soulland.system.mobs.*;
import me.wisemann64.soulland.system.players.SLPlayer;
import me.wisemann64.soulland.system.players.Stats;
import me.wisemann64.soulland.system.util.Utils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCommand implements TabExecutor {

    private String[] args = {"item","ping","item2","item3","slot","damage","zombie","creeper","skeleton"};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) return false;
        switch (strings[0]) {
            case "json" -> json();
        }
        if (!(commandSender instanceof Player pl)) return false;
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(pl);
        if (p == null) return false;
        switch (strings[0]) {
            case "item" -> {
                ItemStack stone = new ItemStack(Material.STONE);
                ItemMeta meta = stone.getItemMeta();
                PersistentDataContainer stoneData = meta.getPersistentDataContainer();
                stoneData.set(SLItems.key("id"), PersistentDataType.STRING,"batu_ngab");
                PersistentDataContainer two = stoneData.getAdapterContext().newPersistentDataContainer();
                two.set(SLItems.key("ngab"),PersistentDataType.STRING,"1");
                two.set(SLItems.key("ngabs"),PersistentDataType.STRING,"2");
                stoneData.set(SLItems.key("bego"), PersistentDataType.TAG_CONTAINER,two);
                stone.setItemMeta(meta);
                pl.getInventory().addItem(stone);
            }
            case "ping" -> p.sendMessage(String.valueOf(p.getHandle().getPing()));
            case "item2" -> {
                ItemStack stone = new ItemStack(Material.STONE);
                ItemMeta meta = stone.getItemMeta();
                meta.setDisplayName(Utils.color("&3Lorem Ipsum"));
                List<String> lore = new ArrayList<>();
                lore.add("&8Melee Weapon");
                lore.add("&c• Damage: &75.5");
                lore.add("&c• Critical Rate: &7+25%");
                lore.add("&c• Critical Damage: &7+50");
                lore.add("&c• Physical PEN: &7+40");
                lore.add("&d• Mana: &7+400");
                lore.add("&d• Magic PEN: &7+69");
                lore.add("&d• Magic Defense: &7+230");
                lore.add("&a• Health: &7+40");
                lore.add("&a• Defense: &7+69");
                lore.add("&6• Vitality: &7+5");
                lore.add("&6• Intelligence: &7+5");
                lore.add("&6• Strength: &7+5");
                lore.add("&6• Critical: &7+5");
                lore.add("");
                lore.add("&7Lorem ipsum dolor sit amet, consectetuer");
                lore.add("&7adipiscing elit, sed diam nonummy nibh");
                lore.add("&7euismod tincidunt ut laoreet dolore magna");
                lore.add("");
                lore.add("&9Upgrade: &70&c/&616");
                lore.add("&9Rarity: &6Legendary");
                lore.add("");
                lore.add("&bPower Stone #1: &7Lorem Ipsum");
                lore.add("&bPower Stone #2: &7Lorem Ipsum");
                meta.setLore(Utils.color(lore));
                stone.setItemMeta(meta);
                pl.getInventory().addItem(stone);
            }
            case "item3" -> {
                ItemStack i = pl.getInventory().getItemInMainHand();
                pl.getInventory().addItem(ItemAbstract.fromItem(i).toItem());
            }
            case "damage" -> {
                double in = NumberUtils.toDouble(strings[1],0);
                double pen = NumberUtils.toDouble(strings[2],0);
                Damage d = new Damage(in, DamageType.PHYSICAL,pen,true);
                p.sendMessage(String.valueOf(p.dealDamage(d)));
            }
            case "zombie" -> {
                Location l = p.getHandle().getLocation();
                new MobZombie(l.getWorld(),"Basic Zombie").spawn(l);
            }
            case "creeper" -> {
                Location l = p.getHandle().getLocation();
                new MobCreeper(l.getWorld(),"Kripeng").spawn(l);
            }
            case "skeleton" -> {
                Location l = p.getHandle().getLocation();
                new MobSkeleton(l.getWorld(),"SkelSeton").spawn(l);
            }
            case "generic" -> {
                Location l = p.getHandle().getLocation();
                MobGeneric.Customizer c = new MobGeneric.Customizer(MobGenericTypes.ZOMBIE);
                c.setName("Darmaji");
                c.setLevel(40);
                c.setExplosionPower(20);
                c.getInitStats().put(Stats.ATK, 50.0);
                c.getInitStats().put(Stats.HEALTH, 150.0);
                c.getInitStats().put(Stats.DEF,750.0);
                new MobGeneric(pl.getWorld(),c).spawn(l);
                c.setType(MobGenericTypes.SKELETON);
                new MobGeneric(pl.getWorld(),c).spawn(l);
                c.setType(MobGenericTypes.CREEPER);
                new MobGeneric(pl.getWorld(),c).spawn(l);
            }
            case "parse" -> SoulLand.getObjectParser().getDialogue("dia1",ObjectSource.DEMO_OBJECTS).play(SoulLand.getGameManager());
            case "sequence" -> SoulLand.getGameManager().playSequence(SoulLand.getObjectParser().getSequence("sequence1",ObjectSource.DEMO_OBJECTS));
            case "key" -> {
                ItemAbstract a = SoulLand.getItemManager().getItem("GENERIC_KEY").setLock("pali ngab ngab");
                p.getHandle().getInventory().addItem(a.toItem());
            }
            case "fill" -> SoulLand.getObjectParser().getAction("clear1",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "testmobwithid" -> SoulLand.getObjectParser().getAction("spawn_action",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "killmob" -> SoulLand.getObjectParser().getAction("despawn_action",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "tpmob" -> SoulLand.getObjectParser().getAction("move_action",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "moveset" -> {
                SoulLand.getObjectParser().getAction("spawn_action",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
                SLMob hantuPakRT = SoulLand.getMobManager().getMobId("hantu_pak_rt");
                hantuPakRT.getHandle().teleport(new Location(hantuPakRT.getWorld(),-228.5,63.0,-181.5));
                Moveset m = new Moveset();
                m.addMove(new MoveSin(301,0.75,60,'y',0.0f,0.0f,true,false));
                hantuPakRT.applyMoveset(m);
            }
            case "moveset2" -> {
                SoulLand.getObjectParser().getAction("spawn_action",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
                SLMob hantuPakRT = SoulLand.getMobManager().getMobId("hantu_pak_rt");
                hantuPakRT.getHandle().teleport(new Location(hantuPakRT.getWorld(),-228.5,63.0,-181.5));
                Moveset m = SoulLand.getObjectParser().getMoveset("moveset1",ObjectSource.DEMO_OBJECTS);
                hantuPakRT.applyMoveset(m);
            }
            case "model" -> {
                SLMobModel model = new ModelKripeng(p.getHandle().getWorld(), null,"kripeng");
                model.spawn(p.getLocation());
            }
            case "tp1" -> {
                SLMobModel model = SoulLand.getMobManager().getModelId("kripeng");
                model.teleport(new Location(p.getHandle().getWorld(),-221.5,67.0,-158.5,90.0f,0.0f));
            }
            case "tp2" -> {
                SLMobModel model = SoulLand.getMobManager().getModelId("kripeng");
                model.teleport(new Location(p.getHandle().getWorld(),-268.5,63.0,-149.5,-128.8f,9.8f));
            }
            case "modelmoveset" -> {
                SLMobModel model = new ModelKripeng(p.getHandle().getWorld(), null,"kripeng");
                model.spawn(p.getLocation());
                model.teleport(new Location(p.getHandle().getWorld(),-268.5,63.0,-149.5,-128.8f,9.8f));
                Moveset m = SoulLand.getObjectParser().getMoveset("moveset1",ObjectSource.DEMO_OBJECTS);
                model.applyMoveset(m);
            }
            case "model1" -> {
                SLMobModel model = new ModelVillager(p.getLocation().getWorld(),"Dinnerbone",null, Villager.Type.SAVANNA, Villager.Profession.BUTCHER);
                model.spawn(p.getLocation());
                Moveset m = SoulLand.getObjectParser().getMoveset("moveset1",ObjectSource.DEMO_OBJECTS);
                model.applyMoveset(m);
            }
            case "model2" -> {
                SLMobModel model = new ModelVillager(p.getLocation().getWorld(),null,null, Villager.Type.TAIGA, Villager.Profession.WEAPONSMITH);
                model.spawn(p.getLocation());
                Moveset m = SoulLand.getObjectParser().getMoveset("moveset1",ObjectSource.DEMO_OBJECTS);
                model.applyMoveset(m);
            }
            case "model3" -> {
                SLMobModel model = new ModelKripeng(p.getLocation().getWorld(),null,null);
                model.spawn(p.getLocation());
                Moveset m = SoulLand.getObjectParser().getMoveset("moveset1",ObjectSource.DEMO_OBJECTS);
                model.applyMoveset(m);
            }
            case "model4" -> {
                ModelWitherSkeleton w = new ModelWitherSkeleton(pl.getWorld(),"Anubis","anubis");
                w.spawn(p.getLocation());
                EntityEquipment eq = w.getEquipment();
                eq.setHelmet(new ItemStack(Material.DRAGON_HEAD));
                eq.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
                eq.setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
                eq.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                eq.setItemInMainHand(new ItemStack(Material.GOLDEN_HOE));
                eq.setItemInOffHand(new ItemStack(Material.NETHER_STAR));
                Moveset m = SoulLand.getObjectParser().getMoveset("moveset1",ObjectSource.DEMO_OBJECTS);
                w.applyMoveset(m);
            }
            case "model5" -> {
                ModelGeneric g = new ModelGeneric(pl.getWorld(), ModelGenerator.ModelType.WITHER_SKELETON, "Minang",null);
                g.spawn(p.getLocation());
                EntityEquipment eq = g.getEquipment();
                eq.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
                Moveset m = SoulLand.getObjectParser().getMoveset("moveset1",ObjectSource.DEMO_OBJECTS);
                g.applyMoveset(m);
            }
            case "spawnmod1" -> SoulLand.getObjectParser().getAction("spawnmod1",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "spawnmod2" -> SoulLand.getObjectParser().getAction("spawnmod2",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "movemob" -> SoulLand.getObjectParser().getAction("movemob",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "tpp1" -> SoulLand.getObjectParser().getAction("tp1",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "tpp2" -> SoulLand.getObjectParser().getAction("tp2",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "despawn" -> SoulLand.getObjectParser().getAction("despawnmodel",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
            case "modelmove" -> SoulLand.getObjectParser().getAction("movemodel",ObjectSource.DEMO_OBJECTS).accept(SoulLand.getGameManager());
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) return Arrays.asList(args);
        return null;
    }

    void json() {

    }
}
