package me.wisemann64.soulland.players;

import me.wisemann64.soulland.PlayerConfigManager;
import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.Utils;
import me.wisemann64.soulland.combat.CombatEntity;
import me.wisemann64.soulland.combat.Damage;
import me.wisemann64.soulland.items.SLItems;
import me.wisemann64.soulland.menu.Menu;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.wisemann64.soulland.items.SLItems.key;

public class SLPlayer implements CombatEntity {

    private final Player handle;
    private YamlConfiguration config;
    private final UUID uuid;
    private PlayerAttributes attributes;
    private Mastery mastery;
    private boolean debugMode = false;
    private final EnumMap<EntityDamageEvent.DamageCause,Integer> envDamageCooldown = new EnumMap<>(EntityDamageEvent.DamageCause.class);

    private final BukkitRunnable tickAction = new BukkitRunnable() {
        @Override
        public void run() {
            tick();
        }
    };;

    private boolean abmInterrupt = false;
    private String abmIMessage = "";
    private long abmRemainingTicks = 0;
    private final String defaultActionBarMessage = "&c%health/%maxHealth &4[&c❤&4]    &b%mana/%maxMana &3[&b✦&3]";
    private final String absorptionMessage = "&c%health/%maxHealth &4[&c❤&4]  %absorption  &b%mana/%maxMana &3[&b✦&3]";

    public SLPlayer(Player player) {
        handle = player;
        uuid = handle.getUniqueId();
        config = PlayerConfigManager.getData(handle);

        mastery = new Mastery(this);
        attributes = new PlayerAttributes(this);

        if (!PlayerConfigManager.hasData(handle)) createData();
        else readData();

        tickAction.runTaskTimer(SoulLand.getPlugin(),0L,1L);
    }

    private void createData() {
        mastery.initialize(0,0);
        attributes.initialize(0,0,0,0);
        saveData();
    }

    private void readData() {
        debugMode = config.getBoolean("debug");
        attributes.setHealth(config.getDouble("last_hp",20));
        attributes.setMana(config.getDouble("last_mana",100));
        absorptionDuration = config.getInt("last_absorption.duration",0);
        absorptionAmount = config.getDouble("last_absorption.amount",0);
        int str = config.getInt("attributes.str");
        int crit = config.getInt("attributes.crit");
        int vit = config.getInt("attributes.vit");
        int in = config.getInt("attributes.int");
        attributes.initialize(vit,in,str,crit);
        int xp = config.getInt("mastery.xp");
        int level = config.getInt("mastery.level");
        mastery.initialize(xp,level);
    }

    private void saveData() {
        config.set("name",handle.getName());
        config.set("uuid",handle.getUniqueId().toString());
        config.set("timestamp",System.currentTimeMillis());
        config.set("debug",debugMode);
        config.set("last_hp",attributes.getHealth());
        config.set("last_mana",attributes.getMana());
        config.set("last_absorption.amount",absorptionAmount);
        config.set("last_absorption.duration",absorptionDuration);
        config.set("attributes.str",attributes.getAttribute(Stats.STR));
        config.set("attributes.crit",attributes.getAttribute(Stats.CRIT));
        config.set("attributes.vit",attributes.getAttribute(Stats.VIT));
        config.set("attributes.int",attributes.getAttribute(Stats.INT));
        config.set("mastery.xp",getXp());
        config.set("mastery.level",getLevel());
        try {
            config.save(PlayerConfigManager.getFile(this));
        } catch (IOException e) {
            System.err.println("Couldn't save config for player with uuid " + getUUID());
        }
    }

    public void onJoin() {
        handle.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        handle.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(40);
        handle.setHealth(40);
        PlayerInventory inv = handle.getInventory();
        inv.setItem(8, SLItems.menuStar());
    }

    private void sendActionBarMessage() {
        String defaultMessage = hasAbsorption() ? absorptionMessage : defaultActionBarMessage;
        handle.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(parseString(abmInterrupt ? abmIMessage : defaultMessage)));
    }

    public void interruptActionBarMessage(String s, long duration) {
        abmIMessage = s;
        abmRemainingTicks = duration;
        abmInterrupt = true;
    }

    private void reduceABMInterrupt() {
        abmRemainingTicks = abmRemainingTicks == 0 ? 0 : abmRemainingTicks-1;
        if (abmRemainingTicks == 0) abmInterrupt = false;
    }

    private void syncHealth() {
        if (getHealth() > getMaxHealth()) setHealth(getMaxHealth());
        handle.setHealth(Math.max(1,Math.floor(getHealthFraction()*40)));
    }

    private String parseString(String s) {
        if (s.contains("%health")) s = s.replace("%health",String.valueOf(Math.round(attributes.getHealth())));
        if (s.contains("%maxHealth")) s = s.replace("%maxHealth",String.valueOf(Math.round(attributes.getMaxHealth())));
        if (s.contains("%mana")) s = s.replace("%mana",String.valueOf(Math.round(attributes.getMana())));
        if (s.contains("%maxMana")) s = s.replace("%maxMana",String.valueOf(Math.round(attributes.getMaxMana())));
        if (s.contains("%absorption")) {
            long sec = (long) Math.ceil(absorptionDuration/20D);
            String time = DurationFormatUtils.formatDuration(sec*1000, "mm:ss", true);
            s = s.replace("%absorption", "&e" + Math.round(absorptionAmount) + "❤ &6[&e" + time + "&6]");
        }
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    private void tick() {
        attributes.tick();
        sendActionBarMessage();
        reduceABMInterrupt();
        syncHealth();
        absorptionTick();
        xpTick();
        cooldownTick();
    }

    private void cooldownTick() {
        handle.setMaximumNoDamageTicks(0);
        handle.setNoDamageTicks(0);
        for (DamageCause v : envDamageCooldown.keySet()) {
            int cd = envDamageCooldown.get(v)-1;
            if (cd == 0) envDamageCooldown.remove(v);
            else envDamageCooldown.put(v,cd);
        }
    }

    public void logout() {
        try {
            tickAction.cancel();
        } catch (IllegalStateException ex) {
            System.err.println("Failed to stop tickAction from player with uuid " + uuid);
        }
        saveData();
    }

    public Player getHandle() {
        return handle;
    }

    public UUID getUUID() {
        return uuid;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public int getXp() {
        return mastery.getXp();
    }

    public int getLevel() {
        return mastery.getLevel();
    }

    private double absorptionAmount = 0;
    private int absorptionDuration = 0;
    public void setAbsorption(double amount, int duration /*tick*/) {
        absorptionAmount = Math.min(amount,getMaxHealth());
        absorptionDuration = duration;
    }
    public void removeAbsorption() {
        absorptionAmount = 0;
        absorptionDuration = 0;
    }
    public boolean hasAbsorption() {
        return absorptionDuration > 0 && absorptionAmount > 0;
    }
    private void absorptionTick() {
        absorptionDuration = absorptionDuration == 0 ? 0 : absorptionDuration-1;
        if (absorptionDuration == 0) absorptionAmount = 0;
        if (absorptionAmount <= 0) absorptionDuration = 0;
        if (absorptionAmount > getMaxHealth()) absorptionAmount = getMaxHealth();

        if (hasAbsorption()) handle.setAbsorptionAmount((int) Math.round(40D*absorptionAmount/getMaxHealth()));
        else handle.setAbsorptionAmount(0);
    }

    private void xpTick() {
        handle.setExp(0.4F);
        handle.setLevel(getLevel());
    }

    public void heal(double amount) {
        setHealth(getHealth() + amount);
    }

    public double dealDamage(double amount) {
        double val0 = amount;
        if (hasAbsorption()) {
            if (absorptionAmount > amount) {
                absorptionAmount -= amount;
                amount = 0;
            }  else {
                amount -= absorptionAmount;
                absorptionAmount = 0;
            }
        }
        setHealth(getHealth() - amount);
        return val0;
    }
    public Location getLocation() {
        return handle.getLocation();
    }
    public Location getEyeLocation() {
        return handle.getEyeLocation();
    }
    public boolean isInvis() {
        return false;
    }
    public double getHealth() {
        return attributes.getHealth();
    }
    public void setHealth(double amount) {
        attributes.setHealth(Math.max(0,Math.min(amount,getMaxHealth())));
    }
    public double getMaxHealth() {
        return attributes.getMaxHealth();
    }
    public float getHealthFraction() {
        return Math.min((float) (getHealth()/getMaxHealth()),1.0F);
    }
    public double getAttackPower(){
        return attributes.getStats(Stats.ATK);
    }
    public double getMagicAttackPower(){
        return attributes.getStats(Stats.MATK);
    }
    public double getDefense(){
        double d = attributes.getStats(Stats.DEF);
        return Math.min(1000,Math.max(-300,d));
    }
    public double getMagicDefense(){
        double d = attributes.getStats(Stats.MDEF);
        return Math.min(1000,Math.max(-300,d));
    }
    public double getPhysicalPEN(){
        return attributes.getStats(Stats.PEN);
    }
    public double getMagicPEN(){
        return attributes.getStats(Stats.MPEN);
    }

    public double getMana() {
        return attributes.getMana();
    }

    public double getMaxMana() {
        return attributes.getMaxMana();
    }
    public void sendMessage(String msg) {
        handle.sendMessage(Utils.color(msg));
    }

    public void openMenu(Menu menu) {
        handle.closeInventory();
        handle.openInventory(menu.getInventory());
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public PlayerAttributes getAttributes() {
        return attributes;
    }
    public PersistentDataContainer getCalculatedContainer(EnumItemSlot slot) {
        String apply = switch(slot) {
            case MAINHAND -> "main";
            case OFFHAND -> "offhand";
            default -> "armor";
        };
        ItemStack it = switch(slot) {
            case MAINHAND -> handle.getInventory().getItemInMainHand();
            case OFFHAND -> handle.getInventory().getItemInOffHand();
            case HEAD -> handle.getInventory().getHelmet();
            case CHEST -> handle.getInventory().getChestplate();
            case LEGS -> handle.getInventory().getLeggings();
            case FEET -> handle.getInventory().getBoots();
            };
        if (it == null) return null;
        ItemMeta meta = it.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer p1 = meta.getPersistentDataContainer();
        PersistentDataContainer p2 = p1.get(key("modifiable"),PersistentDataType.TAG_CONTAINER);
        if (p2 == null) return null;
        String apply1 = p2.getOrDefault(key("apply"),PersistentDataType.STRING,"");
        if (!apply1.equals(apply)) return null;
        return p2.get(key("calculated"),PersistentDataType.TAG_CONTAINER);
    }
    public double getMainHandATK() {
        try {
            return getCalculatedContainer(EnumItemSlot.MAINHAND).get(key("damage"),PersistentDataType.DOUBLE);
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    public double getMainHandMATK() {
        try {
            return getCalculatedContainer(EnumItemSlot.MAINHAND).get(key("magic_damage"),PersistentDataType.DOUBLE);
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    public double getMainHandRATK() {
        try {
            return getCalculatedContainer(EnumItemSlot.MAINHAND).get(key("proj_damage"),PersistentDataType.DOUBLE);
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    public EnumMap<EntityDamageEvent.DamageCause, Integer> getEnvDamageCooldown() {
        return envDamageCooldown;
    }

    @Override
    public String toString() {
        return "{" + "name=" + handle.getName() + " uuid=" + uuid + "}";
    }
}
