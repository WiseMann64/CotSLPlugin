package me.wisemann64.soulland.items;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemModifiable {

    String generateName();
    List<String> generateLore();
    void readData(@NotNull ConfigurationSection config);

}
