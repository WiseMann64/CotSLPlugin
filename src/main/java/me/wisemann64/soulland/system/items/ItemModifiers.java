package me.wisemann64.soulland.system.items;

public enum ItemModifiers {
    DAMAGE("damage","&cDamage"),
    PROJECTILE_DAMAGE("proj_damage","&cProjectile Damage"),
    CRITICAL_RATE("crit_rate","&cCritical Rate"),
    CRITICAL_DAMAGE("crit_damage","&cCritical Damage"),
    PEN("ppen","&cPhysical PEN"),

    MAGIC_DAMAGE("magic_damage","&dMagic Power"),
    MPEN("mpen","&dMagic PEN"),
    MANA("mana","&dMana"),

    HEALTH("health","&aHealth"),
    DEF("def","&aDefense"),
    MDEF("mdef","&aMagic Defense"),

    STR("str","&6Strength"),
    CRIT("crit","&6Critical"),
    INT("int","&6Intelligence"),
    VIT("vit","&6Vitality");
    private final String path;
    private final String display;
    ItemModifiers(String path, String display) {
        this.path = path;
        this.display = display;
    }
    public String getPath() {
        return path;
    }
    public String getDisplay() {
        return display;
    }
}
