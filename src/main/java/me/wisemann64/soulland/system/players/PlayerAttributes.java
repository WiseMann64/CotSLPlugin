package me.wisemann64.soulland.system.players;

import me.wisemann64.soulland.system.items.SLItems;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public class PlayerAttributes {

    private final SLPlayer owner;
    private final EnumMap<Stats,Integer> attributesMap = new EnumMap<>(Stats.class);
    private final EnumMap<Stats,Integer> attributesFinal = new EnumMap<>(Stats.class);
    private final EnumMap<Stats,Double> statsMap = new EnumMap<>(Stats.class);
    private int statsPoint = 0;


    public PlayerAttributes(SLPlayer player) {
        this.owner = player;
        initialize();
    }

    private void initialize() {
        attributesMap.put(Stats.VIT,0);
        attributesMap.put(Stats.INT,0);
        attributesMap.put(Stats.STR,0);
        attributesMap.put(Stats.CRIT,0);
        for (Stats v : Stats.values()) {
            switch (v) {
                case HEALTH,MAX_HEALTH -> setStats(v,20);
                case MANA,MAX_MANA -> setStats(v,100);
                default -> setStats(v,0);
            }
        }
        attributesFinal.put(Stats.VIT,0);
        attributesFinal.put(Stats.INT,0);
        attributesFinal.put(Stats.STR,0);
        attributesFinal.put(Stats.CRIT,0);
    }

    void initialize(int vit, int i, int str, int crt, int statsPoint) {
        attributesMap.put(Stats.VIT,vit);
        attributesMap.put(Stats.INT,i);
        attributesMap.put(Stats.STR,str);
        attributesMap.put(Stats.CRIT,crt);
        attributesFinal.put(Stats.VIT,vit);
        attributesFinal.put(Stats.INT,i);
        attributesFinal.put(Stats.STR,str);
        attributesFinal.put(Stats.CRIT,crt);
        this.statsPoint = statsPoint;
    }

    private void updateStats() {
        /*
        Start
         */
        EnumMap<EnumItemSlot, PersistentDataContainer> equipment = new EnumMap<>(EnumItemSlot.class);
        for (EnumItemSlot v : EnumItemSlot.values()) {
            PersistentDataContainer p = owner.getCalculatedContainer(v);
            if (p != null) equipment.put(v,p);
        }

        int attr;

        // STR
        attr = getAttribute(Stats.STR);
        for (EnumItemSlot v : equipment.keySet()) attr += Math.round(equipment.get(v).getOrDefault(SLItems.key("str"), PersistentDataType.DOUBLE,0.0));
        attributesFinal.put(Stats.STR,attr);

        // CRIT
        attr = getAttribute(Stats.CRIT);
        for (EnumItemSlot v : equipment.keySet()) attr += Math.round(equipment.get(v).getOrDefault(SLItems.key("crit"), PersistentDataType.DOUBLE,0.0));
        attributesFinal.put(Stats.CRIT,attr);

        // INT
        attr = getAttribute(Stats.INT);
        for (EnumItemSlot v : equipment.keySet()) attr += Math.round(equipment.get(v).getOrDefault(SLItems.key("int"), PersistentDataType.DOUBLE,0.0));
        attributesFinal.put(Stats.INT,attr);

        // VIT
        attr = getAttribute(Stats.VIT);
        for (EnumItemSlot v : equipment.keySet()) attr += Math.round(equipment.get(v).getOrDefault(SLItems.key("vit"), PersistentDataType.DOUBLE,0.0));
        attributesFinal.put(Stats.VIT,attr);

        int str = attributesFinal.get(Stats.STR);
        int crit = attributesFinal.get(Stats.CRIT);
        int in = attributesFinal.get(Stats.INT);
        int vit = attributesFinal.get(Stats.VIT);

        double stat;
        int lv = level();
        double main = owner.getMainHandATK();
        double magic = owner.getMainHandMATK();
        double ranged = owner.getMainHandRATK();

        EnumMap<Stats, Double> effBonus = owner.getPotionBoost();
        double bonus;

        // ATK
        stat = 1 + main + Math.min(main,0.12*lv*lv) + Math.min(main,1.2*str) + 0.0125*lv*str;
        bonus = 1 + effBonus.getOrDefault(Stats.ATK,0.0);
        setStats(Stats.ATK,Math.max(stat*bonus,1.0));

        // MATK
        stat = magic + Math.min(magic,0.14*lv*lv) + Math.min(magic,1.8*in) + 0.014*lv*in;
        setStats(Stats.MATK,stat);

        // RATK
        stat = ranged + Math.min(0.125*lv*lv,ranged) + Math.min(main,1.3*str) + 0.0125*lv*str;
        bonus = 1 + effBonus.getOrDefault(Stats.RATK,0.0);
        setStats(Stats.RATK,Math.max(stat*bonus,1.0));

        // CRIT RATE
        stat = 0.25 + 0.00375*lv + 0.005*crit;
        for (EnumItemSlot v : equipment.keySet()) stat += equipment.get(v).getOrDefault(SLItems.key("crit_rate"), PersistentDataType.DOUBLE,0.0);
        bonus = effBonus.getOrDefault(Stats.CRIT_RATE,0.0);
        setStats(Stats.CRIT_RATE,Math.max(stat+bonus,0.0));

        // CRIT DAMAGE
        stat = 50 + 0.25*str + 0.75*crit;
        for (EnumItemSlot v : equipment.keySet()) stat += equipment.get(v).getOrDefault(SLItems.key("crit_damage"), PersistentDataType.DOUBLE,0.0);
        bonus = effBonus.getOrDefault(Stats.CRIT_DAMAGE,0.0);
        setStats(Stats.CRIT_DAMAGE,stat+bonus);

        // MAX HP
        stat = 20;
        for (EnumItemSlot v : equipment.keySet()) stat += equipment.get(v).getOrDefault(SLItems.key("health"), PersistentDataType.DOUBLE,0.0);
        stat += 2*lv + 0.4*vit + 0.04*vit*lv;
        stat *= 1 + 0.0015*vit;
        setStats(Stats.MAX_HEALTH,stat);

        // MAX MANA
        stat = 100;
        for (EnumItemSlot v : equipment.keySet()) stat += equipment.get(v).getOrDefault(SLItems.key("mana"), PersistentDataType.DOUBLE,0.0);
        setStats(Stats.MAX_MANA,stat);

        // DEFENSE
        double vitBonus = 1.25 * vit + 0.00012 * lv * lv * vit;

        stat = 0;
        for (EnumItemSlot v : equipment.keySet()) stat += equipment.get(v).getOrDefault(SLItems.key("def"), PersistentDataType.DOUBLE,0.0);
        bonus = effBonus.getOrDefault(Stats.DEF,0.0);
        stat += vitBonus;
        if (stat > 0) stat *= (1+0.0025*vit);
        setStats(Stats.DEF,Math.max(-300,Math.min(stat+bonus,1000)));

        // MAGIC DEFENSE
        stat = 0;
        for (EnumItemSlot v : equipment.keySet()) stat += equipment.get(v).getOrDefault(SLItems.key("mdef"), PersistentDataType.DOUBLE,0.0);
        bonus = effBonus.getOrDefault(Stats.MDEF,0.0);
        stat += vitBonus;
        if (stat > 0) stat *= (1+0.0025*vit);
        setStats(Stats.MDEF,Math.max(-300,Math.min(stat+bonus,1000)));

        // PEN
        stat = 0;
        for (EnumItemSlot v : equipment.keySet()) stat += equipment.get(v).getOrDefault(SLItems.key("ppen"), PersistentDataType.DOUBLE,0.0);
        setStats(Stats.PEN,stat);

        // MPEN
        stat = 0;
        for (EnumItemSlot v : equipment.keySet()) stat += equipment.get(v).getOrDefault(SLItems.key("mpen"), PersistentDataType.DOUBLE,0.0);
        setStats(Stats.MPEN,stat);
    }

    public double getHealth() {
        return (double) get(Stats.HEALTH);
    }

    public double getMana() {
        return (double) get(Stats.MANA);
    }

    public void setHealth(double health) {
        set(Stats.HEALTH,health);
    }

    public void setMana(double mana) {
        set(Stats.MANA,mana);
    }

    public double getMaxHealth() {
        return (double) get(Stats.MAX_HEALTH);
    }

    public double getMaxMana() {
        return (double) get(Stats.MAX_MANA);
    }

    public void tick() {
        updateStats();
    }

    public SLPlayer getOwner() {
        return owner;
    }

    public Number get(@NotNull Stats stats) {
        if (stats.TYPE == Stats.Type.ATTRIBUTE) return attributesMap.get(stats);
        else return statsMap.get(stats);
    }

    public int getAttribute(@NotNull Stats stats) {
        if (stats.TYPE == Stats.Type.ATTRIBUTE) return attributesMap.get(stats);
        else return 0;
    }

    public int getFinalAttribute(@NotNull Stats stats) {
        if (stats.TYPE == Stats.Type.ATTRIBUTE) return attributesFinal.get(stats);
        else return 0;
    }

    public double getStats(@NotNull Stats stats) {
        if (stats.TYPE == Stats.Type.ATTRIBUTE) return 0;
        else return statsMap.get(stats);
    }

    public void set(@NotNull Stats stats, @NotNull Number val) {
        if (stats.TYPE == Stats.Type.ATTRIBUTE) attributesMap.put(stats,(int)val);
        else statsMap.put(stats,(double)val);
    }

    public void setAttribute(@NotNull Stats stats, int val) {
        if (stats.TYPE == Stats.Type.ATTRIBUTE) attributesMap.put(stats,val);
    }

    public void setStats(@NotNull Stats stats, double val) {
        if (stats.TYPE != Stats.Type.ATTRIBUTE) statsMap.put(stats,val);
    }

    public int getStatsPoint() {
        return statsPoint;
    }

    private int getUsedStatsPoint() {
        return getAttribute(Stats.STR) + getAttribute(Stats.VIT) + getAttribute(Stats.CRIT) + getAttribute(Stats.INT);
    }

    public void addStatsPoint(int val) {
        statsPoint += val;
    }

    public void restoreStatsPoint() {
        statsPoint += getUsedStatsPoint();
        attributesMap.put(Stats.STR,0);
        attributesMap.put(Stats.CRIT,0);
        attributesMap.put(Stats.VIT,0);
        attributesMap.put(Stats.INT,0);
    }

    public void addStats(Stats stats) {
        if (statsPoint < 1) return;
        switch (stats) {
            case CRIT,INT,VIT,STR -> {
                statsPoint--;
                attributesMap.put(stats,attributesMap.get(stats)+1);
            }
        }
    }

    private int level() {
        return owner.getLevel();
    }
}
