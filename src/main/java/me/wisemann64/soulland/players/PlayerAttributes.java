package me.wisemann64.soulland.players;

import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;

public class PlayerAttributes {

    private final SLPlayer owner;

    private final EnumMap<Stats,Integer> attributesMap;
    private final EnumMap<Stats,Double> statsMap;


    public PlayerAttributes(SLPlayer player) {
        this.owner = player;
        attributesMap = new EnumMap<>(Stats.class);
        statsMap =  new EnumMap<>(Stats.class);
        readData();
        updateBasicStats();
        statsMap.put(Stats.HEALTH,statsMap.get(Stats.MAX_HEALTH));
        statsMap.put(Stats.MANA,statsMap.get(Stats.MAX_MANA));
    }

    private void readData() {
        attributesMap.put(Stats.VIT,0);
        attributesMap.put(Stats.INT,0);
        attributesMap.put(Stats.STR,0);
        attributesMap.put(Stats.CRIT,0);
    }

    private void updateBasicStats() {
        for (Stats v : Stats.values()) {
            if (v == Stats.HEALTH || v == Stats.MANA) continue;
            setStats(v,69420.5);
        }
        statsMap.put(Stats.MAX_HEALTH, 20.0);
        statsMap.put(Stats.MAX_MANA,100.0);

        /*
        Start
         */
        double stat = 0;
        int lv = level();
        double main = owner.getMainHandATK();
        double magic = owner.getMainHandMATK();

        // ATK
        stat = 1 + main + 0.12*lv*lv + Math.min(main,1.2*getAttribute(Stats.STR)) + 0.0125*lv*getAttribute(Stats.STR);
        setStats(Stats.ATK,stat);

        // CRIT RATE
        stat = 0.25 + 0.00375*lv + 0.005*getAttribute(Stats.CRIT);
        setStats(Stats.CRIT_RATE,stat);

        // CRIT DAMAGE
        stat = 50 + 0.25*getAttribute(Stats.STR) + 0.75*getAttribute(Stats.CRIT);
        setStats(Stats.CRIT_DAMAGE,stat);

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
        updateBasicStats();
//        System.out.println(maxHealth);
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

    private int level() {
        return owner.getLevel();
    }
}
