package me.wisemann64.soulland.mobs;

public class MobAttributes {

    public final SLMob owner;
    private long tickCreated = 0;

    private double maxHealth = 20.0;
    private double health = 20.0;
    private double attackPower = 0;
    private double magicAttack = 0;
    private double defense = 0;
    private double magicDefense = 0;
    private double physicalPEN = 0;
    private double magicPEN = 0;


    public MobAttributes(SLMob mob) {
        this.owner = mob;
    }


    public SLMob getOwner() {
        return owner;
    }

    public long getTickCreated() {
        return tickCreated;
    }

    protected void setTickCreated(long tickCreated) {
        this.tickCreated = tickCreated;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    protected void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public double getHealth() {
        return health;
    }

    protected void setHealth(double health) {
        this.health = health;
    }

    public double getAttackPower() {
        return attackPower;
    }

    protected void setAttackPower(double attackPower) {
        this.attackPower = attackPower;
    }

    public double getMagicAttack() {
        return magicAttack;
    }

    protected void setMagicAttack(double magicAttack) {
        this.magicAttack = magicAttack;
    }

    public double getDefense() {
        return defense;
    }

    protected void setDefense(double defense) {
        this.defense = defense;
    }

    public double getMagicDefense() {
        return magicDefense;
    }

    protected void setMagicDefense(double magicDefense) {
        this.magicDefense = magicDefense;
    }

    public double getPhysicalPEN() {
        return physicalPEN;
    }

    protected void setPhysicalPEN(double physicalPEN) {
        this.physicalPEN = physicalPEN;
    }

    public double getMagicPEN() {
        return magicPEN;
    }

    protected void setMagicPEN(double magicPEN) {
        this.magicPEN = magicPEN;
    }
}
