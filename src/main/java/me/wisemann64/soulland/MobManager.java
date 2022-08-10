package me.wisemann64.soulland;

import me.wisemann64.soulland.mobs.SLMob;

import java.util.*;

public class MobManager {

    private final Map<Integer, SLMob> trackedMobs = new HashMap<>();

    public Set<UUID> getTrackedMobsUUIDs() {
        Set<UUID> ret = new HashSet<>();
        trackedMobs.values().forEach(v -> ret.add(v.getHandle().getUniqueId()));
        return ret;
    }

    public int putMobToRegistry(SLMob mob) {
        int index = getLeastId();
        this.trackedMobs.put(index,mob);
        return index;
    }

    public int getLeastId() {
        int ret = 0;
        while (trackedMobs.containsKey(ret)) ret++;
        return ret;
    }

    public void empty() {
        new ArrayList<>(trackedMobs.values()).forEach(SLMob::remove);
    }

    public SLMob getMob(UUID u) {
        for (SLMob ma : trackedMobs.values()) if (ma.getHandle().getUniqueId().equals(u)) return ma;
        return null;
    }

    public void removeMobFromRegistry(int key) {
        this.trackedMobs.remove(key);
    }
}
