package me.wisemann64.soulland.system.players;

import me.wisemann64.soulland.system.PlayerConfigManager;
import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.combat.CombatEntity;
import me.wisemann64.soulland.system.combat.Damage;
import me.wisemann64.soulland.system.combat.DamageType;
import me.wisemann64.soulland.system.items.SLItems;
import me.wisemann64.soulland.system.menu.Menu;
import me.wisemann64.soulland.system.util.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Random;
import java.util.UUID;

public class SLPlayer implements CombatEntity {

    private final Player handle;
    private YamlConfiguration config;
    private final UUID uuid;
    private PlayerAttributes attributes;
    private Mastery mastery;
    private boolean debugMode = false;
    private boolean magicUnlocked = false;
    private final EnumMap<EntityDamageEvent.DamageCause,Integer> envDamageCooldown = new EnumMap<>(EntityDamageEvent.DamageCause.class);

    private final BukkitRunnable tickAction = new BukkitRunnable() {
        @Override
        public void run() {
            tick();
        }
    };

    private boolean abmInterrupt = false;
    private String abmIMessage = "";
    private long abmRemainingTicks = 0;
    private long xpTick = 0;
    private int lastXp = 0;

    private void xpTimer(int amount) {
        xpTick = 40;
        lastXp = amount;
    }

    private String buildActionBarMessage() {
        StringBuilder sb = new StringBuilder("&c%health/%maxHealth &4[&c❤&4]");
        if (xpTick > 0) sb.append("  %xp");
        if (hasAbsorption()) sb.append("  %absorption");
        if (magicUnlocked) sb.append("  &b%mana/%maxMana &3[&b✦&3]");
        return sb.toString();
    }

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
        attributes.initialize(0,0,0,0,0);
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
        int av = config.getInt("attributes.available");
        attributes.initialize(vit,in,str,crit,av);
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
        config.set("attributes.available",attributes.getStatsPoint());
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
        String defaultMessage = buildActionBarMessage();
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
        if (s.contains("%health")) s = s.replace("%health",String.valueOf(Math.round(attributes.getHealth())));
        if (s.contains("%maxHealth")) s = s.replace("%maxHealth",String.valueOf(Math.round(attributes.getMaxHealth())));
        if (s.contains("%mana")) s = s.replace("%mana",String.valueOf(Math.round(attributes.getMana())));
        if (s.contains("%maxMana")) s = s.replace("%maxMana",String.valueOf(Math.round(attributes.getMaxMana())));
        if (s.contains("%absorption")) {
            long sec = (long) Math.ceil(absorptionDuration/20D);
            String time = DurationFormatUtils.formatDuration(sec*1000, "mm:ss", true);
            s = s.replace("%absorption", "&e" + Math.round(absorptionAmount) + "❤ &6[&e" + time + "&6]");
        }
        if (s.contains("%xp")) s = s.replace("%xp","&3[&b+" + lastXp + " XP&3]");
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
        xpTick = xpTick > 0 ? xpTick-1 : 0;
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
        handle.setLevel(getLevel());
        handle.setExp(getLevel() == 40 ? 1.0f : mastery.getProgress());
    }

    public void heal(double amount) {
        setHealth(getHealth() + amount);
    }

    public double dealDamage(double amount, CombatEntity damager) {
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
        if (amount <= 0) die();
        else attributes.setHealth(Math.min(amount,getMaxHealth()));
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
    private boolean drawCrit() {
        Random r = new Random();
        return r.nextDouble() < attributes.getStats(Stats.CRIT_RATE);
    }
    public Damage basicAttack() {
        boolean crit = drawCrit();
        double cd = crit ? 1 + attributes.getStats(Stats.CRIT_DAMAGE)/100 : 1;
        double dmg = getAttackPower() * (1 + 0.01*attributes.getStats(Stats.STR))*cd;
        return new Damage(dmg, DamageType.PHYSICAL,getPhysicalPEN(),crit);
    }
    public Damage arrowAttack(boolean arrowCrit) {
        boolean crit = drawCrit() && arrowCrit;
        double cd = crit ? 1 + attributes.getStats(Stats.CRIT_DAMAGE)/100 : 1;
        double dmg = getRangedAttackPower() * (1 + 0.01*attributes.getStats(Stats.STR))*cd;
        return new Damage(dmg, DamageType.PHYSICAL,getPhysicalPEN(),crit);
    }
    public double getMagicAttackPower(){
        return attributes.getStats(Stats.MATK);
    }
    public double getRangedAttackPower() {
        return attributes.getStats(Stats.RATK);
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
        PersistentDataContainer p2 = p1.get(SLItems.key("modifiable"),PersistentDataType.TAG_CONTAINER);
        if (p2 == null) return null;
        String apply1 = p2.getOrDefault(SLItems.key("apply"),PersistentDataType.STRING,"");
        if (!apply1.equals(apply)) return null;
        return p2.get(SLItems.key("calculated"),PersistentDataType.TAG_CONTAINER);
    }
    public double getMainHandATK() {
        try {
            return getCalculatedContainer(EnumItemSlot.MAINHAND).get(SLItems.key("damage"),PersistentDataType.DOUBLE);
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    public double getMainHandMATK() {
        try {
            return getCalculatedContainer(EnumItemSlot.MAINHAND).get(SLItems.key("magic_damage"),PersistentDataType.DOUBLE);
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    public double getMainHandRATK() {
        try {
            return getCalculatedContainer(EnumItemSlot.MAINHAND).get(SLItems.key("proj_damage"),PersistentDataType.DOUBLE);
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    public EnumMap<EntityDamageEvent.DamageCause, Integer> getEnvDamageCooldown() {
        return envDamageCooldown;
    }
    public void sound(Sound sound, float v, float v1) {
        handle.playSound(handle.getLocation(),sound,v,v1);
    }
    public void sendSound(Sound sound, float v, float v1) {
        handle.getWorld().playSound(handle.getLocation(),sound,v,v1);
    }
    private void die() {
        sound(Sound.ENTITY_WITHER_SPAWN,1F,1F);
        setHealth(getMaxHealth());
        handle.sendTitle("Death Event",null,0,50,10);
    }

    public EnumMap<Stats,Double> getPotionBoost() {
        EnumMap<Stats,Double> ret = new EnumMap<>(Stats.class);
        Collection<PotionEffect> pot = handle.getActivePotionEffects();
        for (PotionEffect eff : pot) {
            int level = eff.getAmplifier()+1;
            if (eff.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                ret.put(Stats.DEF,level*40.0);
                ret.put(Stats.MDEF,level*40.0);
            }
            if (eff.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                ret.put(Stats.ATK,level*0.1);
                ret.put(Stats.RATK,level*0.1);
                ret.put(Stats.CRIT_RATE,level*0.015);
                ret.put(Stats.CRIT_DAMAGE,level*7.5);
            }
            if (eff.getType().equals(PotionEffectType.WEAKNESS)) {
                double s = ret.getOrDefault(Stats.ATK,0.0);
                ret.put(Stats.ATK,s - level*0.1);
                s = ret.getOrDefault(Stats.RATK,0.0);
                ret.put(Stats.RATK,s - level*0.1);
                s = ret.getOrDefault(Stats.CRIT_RATE,0.0);
                ret.put(Stats.CRIT_RATE,s - level*0.1);
            }
        }
        return ret;
    }

    public boolean isMagicUnlocked() {
        return magicUnlocked;
    }

    public void addXp(int xpYield) {
        sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1f,2f);
        mastery.addXp(xpYield);
        xpTimer(xpYield);
    }

    @Override
    public String toString() {
        return "{" + "name=" + handle.getName() + " uuid=" + uuid + "}";
    }
}
