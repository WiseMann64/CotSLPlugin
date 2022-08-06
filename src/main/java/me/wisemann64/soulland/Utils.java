package me.wisemann64.soulland;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static String color(String val) {
        return ChatColor.translateAlternateColorCodes('&',val);
    }

    public static List<String> color(List<String> val) {
        List<String> n = new ArrayList<>();
        val.forEach(o -> n.add(color(o)));
        return n;
    }
}
