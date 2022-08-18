package me.wisemann64.soulland.gameplay.objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.GameManager;
import me.wisemann64.soulland.gameplay.GameplayEvent;
import me.wisemann64.soulland.gameplay.cutscene.Cutscene;
import me.wisemann64.soulland.gameplay.cutscene.CutsceneDialogue;
import me.wisemann64.soulland.gameplay.cutscene.Frame;
import me.wisemann64.soulland.gameplay.cutscene.MovingFrame;
import me.wisemann64.soulland.gameplay.objective.Objective;
import me.wisemann64.soulland.gameplay.objective.ObjectiveGoToLocation;
import me.wisemann64.soulland.gameplay.objective.ObjectiveKillMob;
import me.wisemann64.soulland.system.mobs.MobGeneric;
import me.wisemann64.soulland.system.mobs.MobGenericTypes;
import me.wisemann64.soulland.system.players.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;

public class ObjectParser {

    private static final String a = SoulLand.getPlugin().getDataFolder().getPath() + "/";
    private static Gson GSON;

    private static final EnumMap<ObjectSource,JsonObject> objects = new EnumMap<>(ObjectSource.class);

    public ObjectParser() {
        GSON = new Gson();
        for (ObjectSource v : ObjectSource.values()) {
            SoulLand.getPlugin().saveResource(v.getPath(),true);
            try {
                objects.put(v,GSON.fromJson(new JsonReader(new FileReader(v.getFile())),JsonObject.class));
            } catch (IOException e) {
                System.err.println("ObjectParser -> Failed to load " + v.getPath() + "!");
                objects.put(v,null);
            }
        }
    }

    // TODO make start and finish accepts action or event

    private void startAndFinish(JsonObject base, GameplayEvent ev) {
        if (base.has("start") && !base.get("start").isJsonNull()) ev.setStartEventReference(base.get("start").getAsString());
        if (base.has("finish") && !base.get("finish").isJsonNull()) ev.setFinishEventReference(base.get("finish").getAsString());
    }

    private void message(JsonObject base, Objective o) {
        if (base.has("message") && !base.get("message").isJsonNull()) o.setMessage(base.get("message").getAsString());
    }

    public Cutscene getCutscene(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        JsonObject key0 = json.getAsJsonObject(identifier);
        String type = key0.get("type").getAsString();

        if (!"CUTSCENE".equals(type)) return null;
        Cutscene ret = new Cutscene();
        if (key0.has("dialogues")) {
            JsonArray dialogues = key0.get("dialogues").getAsJsonArray();
            dialogues.forEach(e -> ret.addDialogue(new CutsceneDialogue(e.getAsJsonObject().get("at").getAsInt(),e.getAsJsonObject().get("val").getAsString())));
        }
        if (key0.has("frames")) {
            JsonArray frames = key0.get("frames").getAsJsonArray();
            frames.forEach(e -> {
                int duration = e.getAsJsonObject().get("duration").getAsInt();
                World world = Bukkit.getWorld(e.getAsJsonObject().get("world").getAsString());

                List<Double> xyz = new ArrayList<>();
                e.getAsJsonObject().get("xyz").getAsJsonArray().forEach(e1 -> xyz.add(e1.getAsDouble()));

                List<Float> yp = new ArrayList<>();
                e.getAsJsonObject().get("yp").getAsJsonArray().forEach(e1 -> yp.add((float) e1.getAsDouble()));

                Location locFrom = new Location(world,xyz.get(0),xyz.get(1),xyz.get(2),yp.get(0),yp.get(1));

                switch (e.getAsJsonObject().get("type").getAsString()) {
                    case "STATIC" -> ret.addFrame(new Frame(duration,locFrom));
                    case "MOVING" -> {
                        List<Double> to = new ArrayList<>();
                        e.getAsJsonObject().get("to").getAsJsonArray().forEach(e1 -> to.add(e1.getAsDouble()));
                        Location to0 = new Location(world,to.get(0),to.get(1),to.get(2));
                        ret.addFrame(new MovingFrame(duration,locFrom,to0));
                    }
                }
            });
        }
        startAndFinish(key0,ret);
        return ret;
    }

    public Objective getObjective(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        JsonObject obj = json.getAsJsonObject(identifier);
        String type = obj.get("type").getAsString();
        if (!"OBJECTIVE".equals(type)) return null;
        String subtype = obj.get("subtype").getAsString();
        return switch (subtype) {
            case "GO_TO_LOCATION" -> getObjectiveGoToLocation(obj);
            case "KILL_MOB" -> getObjectiveKillMob(obj);
            default -> null;
        };
    }

    public ObjectiveGoToLocation getObjectiveGoToLocation(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        JsonObject obj = json.getAsJsonObject(identifier);
        return getObjectiveGoToLocation(obj);
    }

    public ObjectiveGoToLocation getObjectiveGoToLocation(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"OBJECTIVE".equals(type)) return null;
        if (!"GO_TO_LOCATION".equals(obj.get("subtype").getAsString())) return null;

        JsonObject destination = obj.get("destination").getAsJsonObject();

        World w = Bukkit.getWorld(destination.get("world").getAsString());
        if (w == null) return null;

        double[] a = GSON.fromJson(destination.get("borders").getAsJsonArray().get(0),double[].class);
        double[] b = GSON.fromJson(destination.get("borders").getAsJsonArray().get(1),double[].class);
        ObjectiveGoToLocation g = new ObjectiveGoToLocation(new BoundingBox(a[0],a[1],a[2],b[0],b[1],b[2]),w);
        startAndFinish(obj,g);
        message(obj,g);
        return g;
    }

    public ObjectiveKillMob getObjectiveKillMob(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        JsonObject obj = json.getAsJsonObject(identifier);
        return getObjectiveKillMob(obj);
    }

    public ObjectiveKillMob getObjectiveKillMob(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"OBJECTIVE".equals(type)) return null;
        if (!"KILL_MOB".equals(obj.get("subtype").getAsString())) return null;

        JsonObject mobToKill = obj.getAsJsonObject("mobToKill");
        World w = Bukkit.getWorld(mobToKill.get("world").getAsString());
        double[] p = GSON.fromJson(mobToKill.get("location"),double[].class);
        Location l = new Location(w,p[0],p[1],p[2], (float) p[3], (float) p[4]);
        if (mobToKill.has("mobRef")) {
            String[] ref = mobToKill.get("mobRef").getAsString().split("\\.");
            ObjectSource os = ObjectSource.valueOf(ref[0]);
            String ref1 = ref[1];
            ObjectiveKillMob g = new ObjectiveKillMob(l,getMobGenericCustomizer(ref1,os));
            startAndFinish(obj,g);
            message(obj,g);
            return g;
        }
        if (mobToKill.has("mob")) {
            ObjectiveKillMob g = new ObjectiveKillMob(l, getMobGenericCustomizer(mobToKill.getAsJsonObject("mob")));
            startAndFinish(obj,g);
            message(obj,g);
            return g;
        }
        return null;
    }

    public MobGeneric.Customizer getMobGenericCustomizer(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        return getMobGenericCustomizer(json.getAsJsonObject(identifier));
    }

    public MobGeneric.Customizer getMobGenericCustomizer(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"MOB_CUSTOMIZER".equals(type)) return null;
        JsonObject customizer = obj.getAsJsonObject("customizer");
        MobGeneric.Customizer c = new MobGeneric.Customizer(MobGenericTypes.valueOf(customizer.get("mobType").getAsString()));
        if (customizer.has("name")) c.setName(customizer.get("name").getAsString());
        if (customizer.has("level")) c.setLevel(customizer.get("level").getAsInt());
        if (customizer.has("xp")) c.setXp(customizer.get("xp").getAsInt());
        if (customizer.has("explosionPower")) c.setExplosionPower(customizer.get("explosionPower").getAsDouble());
        if (customizer.has("drops")) customizer.getAsJsonArray("drops").forEach(e -> c.getDrops().add(e.getAsString()));
        if (customizer.has("stats")) {
            JsonObject stats = customizer.getAsJsonObject("stats");
            EnumMap<Stats, Double> s = c.getInitStats();
            if (stats.has("health")) s.put(Stats.HEALTH, stats.get("health").getAsDouble());
            if (stats.has("atk")) s.put(Stats.ATK, stats.get("atk").getAsDouble());
            if (stats.has("def")) s.put(Stats.DEF, stats.get("def").getAsDouble());
            if (stats.has("matk")) s.put(Stats.MATK, stats.get("matk").getAsDouble());
            if (stats.has("mdef")) s.put(Stats.MDEF, stats.get("mdef").getAsDouble());
            if (stats.has("mpen")) s.put(Stats.MPEN, stats.get("mpen").getAsDouble());
            if (stats.has("pen")) s.put(Stats.PEN, stats.get("pen").getAsDouble());
        }
        return c;
    }

    public Consumer<GameManager> getEvent(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        return getEvent(json.getAsJsonObject(identifier));
    }

    public Consumer<GameManager> getEvent(@Nullable JsonObject obj) {
        if (obj == null) return null;
        if (!obj.has("type")) return null;
        if (obj.get("type").isJsonNull()) return null;
        if (!"EVENT".equals(obj.get("type").getAsString())) return null;

        JsonArray events = obj.getAsJsonArray("events");
        List<Consumer<GameManager>> list = new ArrayList<>();

        events.forEach(e -> {
            Consumer<GameManager> action = getAction(e.getAsJsonObject());
            if (action != null) list.add(action);
        });

        return gm -> list.forEach(a -> a.accept(gm));
    }

    public Consumer<GameManager> getAction(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        return getAction(json.getAsJsonObject(identifier));
    }

    public Consumer<GameManager> getAction(@Nullable JsonObject obj) {
        if (obj == null) return null;
        if (!obj.has("action") || !obj.has("value")) return null;
        String action = obj.get("action").getAsString();
        String value = obj.get("value").getAsString();
        switch (ActionTypes.valueOf(action)) {
            case SHOUT -> {
                return gm -> gm.shout(obj.get("value").getAsString());
            }
            case SET_OBJECTIVE -> {
                RefSource a = parse(value);
                Objective o = getObjective(a.ref,a.source);
                return o == null ? null : gm -> gm.setObjective(o);
            }
        }
        return null;
    }

    public Consumer<GameManager> getEventOrAction(@Nullable String ref) {
        if (ref == null) return null;
        String[] reff = ref.split("\\.");
        ObjectSource os = ObjectSource.valueOf(reff[0]);
        String ref1 = reff[1];
        Consumer<GameManager> ret = getEvent(ref1,os);
        if (ret == null) return getAction(ref1,os);
        else return ret;
    }

    private RefSource parse(String ref) {
        String[] reff = ref.split("\\.");
        return new RefSource(reff[1],ObjectSource.valueOf(reff[0]));
    }

    private record RefSource(String ref, ObjectSource source) {
    }

}
