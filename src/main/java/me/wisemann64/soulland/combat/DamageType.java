package me.wisemann64.soulland.combat;

public enum DamageType {

    PHYSICAL("Physical Damage","&c","&4"),
    MAGIC("Magic Damage","&d","&5"),
    TRUE("True Damage","&f","&7");

    private final String text;
    private final String color;
    private final String alternateColor;

    DamageType(String text, String color, String alternateColor) {
        this.text = text;
        this.color = color;
        this.alternateColor = alternateColor;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }

    public String getAlternateColor() {
        return alternateColor;
    }
}
