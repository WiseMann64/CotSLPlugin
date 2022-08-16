package me.wisemann64.soulland.system.util;

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

    public static String titleCase(Object val) {
        return titleCase(val.toString());
    }

    public static String titleCase(String val) {
        StringBuilder r = new StringBuilder();
        boolean up = true;
        for (char c : val.toCharArray()) {
            if (c == '_') {
                r.append(" ");
                up = true;
                continue;
            }
            if (up) {
                r.append(String.valueOf(c).toUpperCase());
                up = false;
            } else r.append(String.valueOf(c).toLowerCase());
        }
        return r.toString();
    }
}
