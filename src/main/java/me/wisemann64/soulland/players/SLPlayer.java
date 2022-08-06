package me.wisemann64.soulland.players;

import me.wisemann64.soulland.PlayerConfigManager;
import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.Utils;
import me.wisemann64.soulland.items.SLItems;
import me.wisemann64.soulland.menu.Menu;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.UUID;

public class SLPlayer {

    private final Player handle;
    private YamlConfiguration config;
    private final UUID uuid;
    private PlayerAttributes attributes;
    private Mastery mastery;
    private boolean debugMode;

    private BukkitRunnable tickAction;

    private boolean abmInterrupt = false;
    private String abmIMessage = "";
    private long abmRemainingTicks = 0;
    private final String defaultActionBarMessage = "&c%health/%maxHealth &4[&c❤&4]    &b%mana/%maxMana &3[&b✦&3]";
    private final String absorptionMessage = "&c%health/%maxHealth &4[&c❤&4]  %absorption  &b%mana/%maxMana &3[&b✦&3]";

    public SLPlayer(Player player) {
        handle = player;
        uuid = handle.getUniqueId();
        config = PlayerConfigManager.getData(handle);
        readData();
//        if (!PlayerConfigManager.hasData(handle)) createData();
//        else readData();
        mastery = new Mastery(this);

        attributes = new PlayerAttributes(this);

        tickAction = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        tickAction.runTaskTimer(SoulLand.getPlugin(),0L,1L);

        debugMode = false;
    }

    private void readData() {

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
        handle.setHealth(Math.max(1,Math.ceil(getHealthFraction()*40)));
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
    }

    public void logout() {
        try {
            tickAction.cancel();
        } catch (IllegalStateException ex) {
            System.err.println("Failed to stop tickAction from player with uuid " + uuid);
        }
        saveData();
    }

    private void saveData() {

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

    public double getHealth() {
        return attributes.getHealth();
    }

    public void setHealth(double amount) {
        attributes.setHealth(Math.max(0,Math.min(amount,getMaxHealth())));
    }

    public double getMaxHealth() {
        return attributes.getMaxHealth();
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
    public void absorptionTick() {
        absorptionDuration = absorptionDuration == 0 ? 0 : absorptionDuration-1;
        if (absorptionDuration == 0) absorptionAmount = 0;
        if (absorptionAmount <= 0) absorptionDuration = 0;

        if (hasAbsorption()) handle.setAbsorptionAmount((int) Math.round(40D*absorptionAmount/getMaxHealth()));
        else handle.setAbsorptionAmount(0);
    }

    public void heal(double amount) {
        setHealth(getHealth() + amount);
    }

    public void damage(double amount) {
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
    }

    public float getHealthFraction() {
        return (float) (getHealth()/getMaxHealth());
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

    public double getMainHandATK() {
        return 1.0;
    }

    public double getMainHandMATK() {
        return 1.0;
    }

    @Override
    public String toString() {
        return "{" + "name=" + handle.getName() + " uuid=" + uuid + "}";
    }
}
