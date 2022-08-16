package me.wisemann64.soulland.combat;

public class Damage {

    private final double oldValue;
    private double newValue;
    private final DamageType type;
    private double penetration;
    private final boolean crit;
    private CombatEntity damager = null;

    public Damage(double value, DamageType type, double penetration, boolean crit) {
        this.oldValue = value;
        this.type = type;
        this.penetration = penetration;
        this.crit = crit;
        newValue = oldValue;
    }

    public double getOldValue() {
        return oldValue;
    }

    public DamageType getType() {
        return type;
    }

    public double getPEN() {
        return penetration;
    }

    public boolean isCrit() {
        return crit;
    }

    public double getNewValue() {
        return newValue;
    }

    public void setNewValue(double newValue) {
        this.newValue = newValue;
    }

    public void addPen(double toAdd) {
        penetration += toAdd;
    }

    @Override
    public String toString() {
        return "{type=" + type + ", oldValue=" + oldValue + ", newValue=" + newValue + ", penetration=" + penetration + ", crit=" + crit + "}";
    }

    public CombatEntity getDamager() {
        return damager;
    }

    public void setDamager(CombatEntity damager) {
        this.damager = damager;
    }
}
