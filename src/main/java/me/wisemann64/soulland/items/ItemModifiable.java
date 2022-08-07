package me.wisemann64.soulland.items;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemModifiable {

    String generateName();
    List<String> generateLore();
    void readData(@NotNull ConfigurationSection config);
    boolean hasSlot();
    void setSlotted(boolean slot);
    String getPowerStone();
    void setPowerStone(String powerStone);
    int getUpgradeLevel();
    void setUpgradeLevel(int upgrade);
    int getMaxUpgradeLevel();
}
