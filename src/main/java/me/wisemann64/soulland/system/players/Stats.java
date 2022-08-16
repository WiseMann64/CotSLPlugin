package me.wisemann64.soulland.system.players;

public enum Stats {
    VIT(0,"Vitality",Type.ATTRIBUTE),
    INT(1,"Intelligence",Type.ATTRIBUTE),
    STR(2,"Strength",Type.ATTRIBUTE),
    CRIT(3,"Critical",Type.ATTRIBUTE),
    HEALTH(4,"Health",Type.STATS),
    MAX_HEALTH(5,"Max Health",Type.STATS),
    MANA(6,"Mana",Type.STATS),
    MAX_MANA(7,"Max Mana",Type.STATS),
    ATK(8,"Attack Power",Type.STATS),
    MATK(9,"Magic Power",Type.STATS),
    CRIT_RATE(10,"Critical Rate",Type.STATS),
    CRIT_DAMAGE(11,"Critical Damage",Type.STATS),
    DEF(12,"Defense",Type.STATS),
    MDEF(13,"Magic Defense",Type.STATS),
    PEN(14,"Physical Penetration",Type.STATS),
    MPEN(15,"Magic Penetration",Type.STATS),
    RATK(16,"Ranged Attack Power",Type.STATS),
    ;
    public final String DISPLAY;
    public final int ID;
    public final Type TYPE;

    Stats(int a, String b, Type c) {
        DISPLAY = b;
        ID = a;
        TYPE = c;
    }

    public enum Type {
        ATTRIBUTE, STATS
    }
}
