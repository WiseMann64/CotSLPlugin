package me.wisemann64.soulland.system;

import me.wisemann64.soulland.system.mobs.SLMob;
import me.wisemann64.soulland.system.mobs.SLMobModel;

import java.util.*;

public class MobManager {

    private final Map<String, SLMob> trackedMobs = new HashMap<>();
    private final Map<String, SLMobModel> trackedModels = new HashMap<>();

    public Set<UUID> getTrackedMobsUUIDs() {
        Set<UUID> ret = new HashSet<>();
        trackedMobs.values().forEach(v -> ret.add(v.getHandle().getUniqueId()));
        return ret;
    }

    public Set<UUID> getTrackedModelsUUIDs() {
        Set<UUID> ret = new HashSet<>();
        trackedModels.values().forEach(v -> ret.add(v.getHandle().getUniqueId()));
        return ret;
    }

    public String randomId(Map<String,?> target) {
        StringBuilder var;
        do {
            var = new StringBuilder();
            Random r = new Random();
            for (int i = 0 ; i < 8 ; i++) {
                char c = (char) (r.nextInt(26)+97);
                var.append(c);
            }
        } while (target.containsKey(var.toString()));
        return var.toString();
    }

    public String putMobToRegistry(SLMob mob) {
        String index = randomId(trackedMobs);
        this.trackedMobs.put(index,mob);
        return index;
    }

    public String putModelToRegistry(SLMobModel model) {
        String index = randomId(trackedModels);
        this.trackedModels.put(index,model);
        return index;
    }

    public String putMobToRegistry(SLMob mob, String id) {
        if (id == null) return putMobToRegistry(mob);
        if (trackedMobs.containsKey(id)) throw new IllegalArgumentException("Mob with id " + id + " already exists!");
        this.trackedMobs.put(id,mob);
        return id;
    }

    public String putModelToRegistry(SLMobModel model, String id) {
        if (id == null) return putModelToRegistry(model);
        if (trackedModels.containsKey(id)) throw new IllegalArgumentException("Model with id " + id + " already exists!");
        this.trackedModels.put(id,model);
        return id;
    }

    public void empty() {
        new ArrayList<>(trackedMobs.values()).forEach(SLMob::remove);
    }

    public SLMob getMob(UUID u) {
        for (SLMob ma : trackedMobs.values()) if (ma.getHandle().getUniqueId().equals(u)) return ma;
        return null;
    }

    public SLMob getMobId(String id) {
        return trackedMobs.getOrDefault(id,null);
    }

    public void removeMobFromRegistry(String key) {
        SLMob mob = this.trackedMobs.remove(key);
        if (mob != null) System.out.println("Removed mob " + key + " from registry");
    }

    public SLMobModel getModel(UUID u) {
        for (SLMobModel ma : trackedModels.values()) if (ma.getHandle().getUniqueId().equals(u)) return ma;
        return null;
    }

    public SLMobModel getModelId(String id) {
        return trackedModels.getOrDefault(id,null);
    }

    public void removeModelFromRegistry(String key) {
        SLMobModel model = this.trackedModels.remove(key);
        if (model != null) System.out.println("Removed model " + key + " from registry");
    }

    public void tick() {
        Set<String> rem = new HashSet<>();
        trackedMobs.forEach((id,mob) -> {
            if (mob.getHandle().isDead()) rem.add(id);
        });
        rem.forEach(this::removeMobFromRegistry);
        Set<String> remModel = new HashSet<>();
        trackedModels.forEach((key,mod) -> {
            if (mod.getHandle().isDead()) remModel.add(key);
        });
        remModel.forEach(this::removeModelFromRegistry);
    }
}
