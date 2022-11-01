package me.wisemann64.soulland.gameplay.objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.GameManager;
import me.wisemann64.soulland.gameplay.GameplayEvent;
import me.wisemann64.soulland.gameplay.cutscene.*;
import me.wisemann64.soulland.gameplay.objective.*;
import me.wisemann64.soulland.system.mobs.*;
import me.wisemann64.soulland.system.players.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
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

    private static Gson GSON;

    private static final EnumMap<ObjectSource, JsonObject> objects = new EnumMap<>(ObjectSource.class);

    public ObjectParser() {
        GSON = new Gson();
        for (ObjectSource v : ObjectSource.values()) {
            SoulLand.getPlugin().saveResource(v.getPath(), true);
            try {
                objects.put(v, GSON.fromJson(new JsonReader(new FileReader(v.getFile())), JsonObject.class));
            } catch (IOException e) {
                System.err.println("ObjectParser -> Failed to load " + v.getPath() + "!");
                objects.put(v, null);
            }
        }
    }

    private void startAndFinish(JsonObject base, GameplayEvent ev) {
        if (base.has("start")) {
            JsonElement start = base.get("start");
            if (start.isJsonPrimitive()) {
                ev.setStartEventReference(start.getAsString());
            } else {
                ev.setStartEventReference(start.toString());
            }
        }
        if (base.has("finish")) {
            JsonElement finish = base.get("finish");
            if (finish.isJsonPrimitive()) {
                ev.setFinishEventReference(finish.getAsString());
            } else {
                ev.setFinishEventReference(finish.toString());
            }
        }
    }

    public void trigger(JsonObject value, Trigger t) {
        if (value.has("identifier") && value.get("identifier").isJsonPrimitive())
            t.setIdentifier(value.get("identifier").getAsString());
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
            dialogues.forEach(e -> ret.addDialogue(new DialogueLine(e.getAsJsonObject().get("at").getAsInt(), e.getAsJsonObject().get("val").getAsString())));
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

                Location locFrom = new Location(world, xyz.get(0), xyz.get(1), xyz.get(2), yp.get(0), yp.get(1));

                switch (e.getAsJsonObject().get("type").getAsString()) {
                    case "STATIC" -> ret.addFrame(new Frame(duration, locFrom));
                    case "MOVING" -> {
                        List<Double> to = new ArrayList<>();
                        e.getAsJsonObject().get("to").getAsJsonArray().forEach(e1 -> to.add(e1.getAsDouble()));
                        Location to0 = new Location(world, to.get(0), to.get(1), to.get(2));
                        ret.addFrame(new MovingFrame(duration, locFrom, to0));
                    }
                }
            });
        }
        startAndFinish(key0, ret);
        return ret;
    }

    public Objective getObjective(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        return getObjective(json.getAsJsonObject(identifier));
    }

    public Objective getObjective(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"OBJECTIVE".equals(type)) return null;
        JsonElement value = obj.get("value");
        String message = obj.has("message") ? obj.get("message").isJsonPrimitive() ? obj.get("message").getAsString() : null : null;
        if (value instanceof JsonObject valueObj) {
            Trigger valueTrigger = getTrigger(valueObj);
            if (valueTrigger == null) return null;
            return new Objective(valueTrigger).setMessage(message);
        }
        if (value.isJsonPrimitive()) {
            String[] ref = value.getAsString().split("\\.");
            ObjectSource os = ObjectSource.valueOf(ref[0]);
            String ref1 = ref[1];
            Trigger valueTrigger = getTrigger(ref1, os);
            if (valueTrigger == null) return null;
            return new Objective(valueTrigger).setMessage(message);
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
        if (customizer.has("id")) c.setId(customizer.get("id").getAsString());
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
        JsonElement obj = json.get(identifier);
        return obj.isJsonArray() ? getEvent(obj.getAsJsonArray()) : getEvent(obj.getAsJsonObject());
    }

    public Consumer<GameManager> getEvent(@Nullable JsonElement e) {
        if (e == null) return null;
        if (e.isJsonArray()) return getEvent(e.getAsJsonArray());
        if (e.isJsonObject()) return getEvent(e.getAsJsonObject());
        return null;
    }

    public Consumer<GameManager> getEvent(@Nullable JsonArray arr) {
        if (arr == null) return null;
        List<Consumer<GameManager>> list = new ArrayList<>();

        arr.forEach(e -> {
            Consumer<GameManager> action = getAction(e.getAsJsonObject());
            if (action != null) list.add(action);
        });

        return gm -> list.forEach(a -> a.accept(gm));
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
        JsonElement value = obj.get("value");
        switch (ActionTypes.valueOf(action)) {
            case SHOUT -> {
                return gm -> gm.shout(value.getAsString());
            }
            case SET_OBJECTIVE -> {
                RefSource a = parse(value.getAsString());
                Objective o = getObjective(a.ref, a.source);
                return o == null ? null : gm -> gm.setObjective(o);
            }
            case TELEPORT -> {
                JsonObject value1 = value.getAsJsonObject();
                double x = value1.get("x").getAsDouble();
                double y = value1.get("y").getAsDouble();
                double z = value1.get("z").getAsDouble();
                float yaw = (float) value1.get("yaw").getAsDouble();
                float pitch = (float) value1.get("pitch").getAsDouble();
                String world = value1.get("world").getAsString();
                Location l = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                return gm -> gm.action(p -> p.getHandle().teleport(l));
            }
            case FILL_BLOCK -> {
                JsonObject value1 = value.getAsJsonObject();
                int[] xyz = {0, 0, 0};
                int[] wid = {0, 0, 0};

                JsonArray corner = value1.getAsJsonArray("corner");
                xyz[0] = corner.get(0).getAsInt();
                xyz[1] = corner.get(1).getAsInt();
                xyz[2] = corner.get(2).getAsInt();

                JsonArray width = value1.getAsJsonArray("width");
                wid[0] = width.get(0).getAsInt();
                wid[1] = width.get(1).getAsInt();
                wid[2] = width.get(2).getAsInt();

                Material mat = Material.valueOf(value1.get("material").getAsString());
                World w = Bukkit.getWorld(value1.get("world").getAsString());

                return gm -> {
                    for (int x = 0; x <= wid[0]; x++)
                        for (int y = 0; y <= wid[1]; y++)
                            for (int z = 0; z <= wid[2]; z++)
                                w.getBlockAt(xyz[0] + x, xyz[1] + y, xyz[2] + z).setType(mat);
                };
            }
            case PLAY_SEQUENCE -> {
                if (value.isJsonPrimitive()) {
                    String[] ref = value.getAsString().split("\\.");
                    ObjectSource os = ObjectSource.valueOf(ref[0]);
                    String ref1 = ref[1];
                    Sequence s = getSequence(ref1, os);
                    return s::play;
                }
                if (value instanceof JsonObject valueObj) return getSequence(valueObj)::play;
            }
            case PLAY_DIALOGUE -> {
                if (value.isJsonPrimitive()) {
                    String[] ref = value.getAsString().split("\\.");
                    ObjectSource os = ObjectSource.valueOf(ref[0]);
                    String ref1 = ref[1];
                    Dialogue d = getDialogue(ref1, os);
                    return d::play;
                }
                if (value instanceof JsonObject valueObj) return getDialogue(valueObj)::play;
            }
            case ADD_TRIGGER -> {
                if (value.isJsonPrimitive()) {
                    String[] ref = value.getAsString().split("\\.");
                    ObjectSource os = ObjectSource.valueOf(ref[0]);
                    String ref1 = ref[1];
                    Trigger t = getTrigger(ref1, os);
                    return gm -> gm.addTrigger(t);
                }
                if (value instanceof JsonObject valueObj) return gm -> gm.addTrigger(getTrigger(valueObj));
            }
            case SPAWN_MOB -> {
                JsonObject value1 = value.getAsJsonObject();

                JsonArray at = value1.getAsJsonArray("at");
                double[] xyz = {0.0, 0.0, 0.0};
                xyz[0] = at.get(0).getAsDouble();
                xyz[1] = at.get(1).getAsDouble();
                xyz[2] = at.get(2).getAsDouble();

                World w = Bukkit.getWorld(value1.get("world").getAsString());

                JsonElement mob = value1.get("mob");

                MobGeneric.Customizer c = null;

                if (mob.isJsonPrimitive()) {
                    String[] ref = mob.getAsString().split("\\.");
                    ObjectSource os = ObjectSource.valueOf(ref[0]);
                    String ref1 = ref[1];
                    c = getMobGenericCustomizer(ref1, os);
                }
                if (mob instanceof JsonObject mobObj) c = getMobGenericCustomizer(mobObj);

                if (c == null) return null;
                MobGeneric.Customizer finalC = c;
                return gm -> new MobGeneric(w, finalC).spawn(new Location(w, xyz[0], xyz[1], xyz[2]));
            }
            case DESPAWN_MOB -> {
                if (value.isJsonPrimitive()) {
                    String id = value.getAsString();
                    return gm -> {
                        SLMob mob = SoulLand.getMobManager().getMobId(id);
                        if (mob != null) mob.quickRemove();
                    };
                } else {
                    JsonArray value1 = value.getAsJsonArray();
                    List<Consumer<GameManager>> ret = new ArrayList<>();
                    value1.forEach(ele -> ret.add(gm -> {
                        SLMob mob = SoulLand.getMobManager().getMobId(ele.getAsString());
                        if (mob != null) mob.quickRemove();
                    }));
                    return gm -> ret.forEach(c -> c.accept(gm));
                }
            }
            case TELEPORT_MOB -> {
                JsonObject value1 = value.getAsJsonObject();

                String id = value1.get("id").getAsString();

                JsonArray to = value1.getAsJsonArray("to");
                double[] xyz = {0.0, 0.0, 0.0};
                xyz[0] = to.get(0).getAsDouble();
                xyz[1] = to.get(1).getAsDouble();
                xyz[2] = to.get(2).getAsDouble();

                double[] yawpitch = {0.0, 0.0};
                if (value1.has("yp")) {
                    JsonArray yp = value1.getAsJsonArray("yp");
                    yawpitch[0] = yp.get(0).getAsDouble();
                    yawpitch[1] = yp.get(1).getAsDouble();
                }

                String world = value1.get("world").getAsString();
                World world1 = Bukkit.getWorld(world);

                return gm -> {
                    SLMob mob = SoulLand.getMobManager().getMobId(id);
                    if (mob != null) {
                        mob.getHandle().teleport(new Location(world1, xyz[0], xyz[1], xyz[2], (float) yawpitch[0], (float) yawpitch[1]), PlayerTeleportEvent.TeleportCause.COMMAND);
                    }
                };
            }
            case MOVE_MOB -> {
                JsonObject value1 = value.getAsJsonObject();
                String id = value1.get("id").getAsString();
                Moveset m;
                JsonElement moveset = value1.get("moveset");
                if (moveset.isJsonPrimitive()) {
                    RefSource rs = parse(moveset.getAsString());
                    if (rs == null) return null;
                    m = getMoveset(rs.ref,rs.source);
                } else if (moveset.isJsonObject()) m = getMoveset(moveset.getAsJsonObject());
                else return null;
                return gm -> {
                    SLMob mob = SoulLand.getMobManager().getMobId(id);
                    if (mob != null) mob.applyMoveset(m);
                };
            }
            case SPAWN_MODEL -> {
                JsonObject value1 = value.getAsJsonObject();

                World world = Bukkit.getWorld(value1.get("world").getAsString());
                double[] xyz = new double[3];
                float[] yp = new float[2];

                xyz[0] = value1.getAsJsonArray("at").get(0).getAsDouble();
                xyz[1] = value1.getAsJsonArray("at").get(1).getAsDouble();
                xyz[2] = value1.getAsJsonArray("at").get(2).getAsDouble();

                yp[0] = value1.getAsJsonArray("yp").get(0).getAsFloat();
                yp[1] = value1.getAsJsonArray("yp").get(1).getAsFloat();

                Location location = new Location(world,xyz[0],xyz[1],xyz[2],yp[0],yp[1]);

                JsonElement model = value1.get("model");
                JsonObject model1;
                if (model.isJsonObject()) {
                    model1 = model.getAsJsonObject();
                } else {
                    String[] ref = model.getAsString().split("\\.");
                    ObjectSource os = ObjectSource.valueOf(ref[0]);
                    String ref1 = ref[1];
                    JsonObject json = objects.get(os);
                    model1 = json.getAsJsonObject(ref1);
                }

                String type = model1.get("type").getAsString();
                if (!"MODEL".equals(type)) return null;
                JsonObject value2 = model1.getAsJsonObject("value");
                ModelGenerator.ModelType modelType = ModelGenerator.ModelType.valueOf(value2.get("type").getAsString());
                String id = value2.get("id").getAsString();
                String name = value2.get("name").getAsString();
                boolean nameVisible = value2.get("nameVisible").getAsBoolean();

                List<Consumer<GameManager>> ret = new ArrayList<>();

                ret.add(gm -> {
                    ModelGeneric mg = new ModelGeneric(world, modelType, name, id);
                    mg.spawn(location);
                    mg.getHandle().setCustomNameVisible(nameVisible);
                });

                if (value2.has("extra")) {
                    JsonObject extra = value2.getAsJsonObject("extra");
                    switch (modelType) {
                        case VILLAGER -> {
                            if (extra.has("villagerType")) ret.add(gm -> {
                                SLMobModel mod = SoulLand.getMobManager().getModelId(id);
                                ((Villager) mod.getHandle()).setVillagerType(Villager.Type.valueOf(extra.get("villagerType").getAsString()));
                            });
                            if (extra.has("profession")) ret.add(gm -> {
                                SLMobModel mod = SoulLand.getMobManager().getModelId(id);
                                ((Villager) mod.getHandle()).setProfession(Villager.Profession.valueOf((extra.get("profession").getAsString())));
                            });
                        }
                        case WITHER_SKELETON -> {

                        }
                    }
                }

                if (value2.has("armor")) {
                    JsonArray armor = value2.getAsJsonArray("armor");
                    Material[] armors = {
                            Material.valueOf(armor.get(0).getAsString()),
                            Material.valueOf(armor.get(1).getAsString()),
                            Material.valueOf(armor.get(2).getAsString()),
                            Material.valueOf(armor.get(3).getAsString()),
                    };
                    ret.add(gm -> {
                        SLMobModel mod = SoulLand.getMobManager().getModelId(id);
                        EntityEquipment equipment = mod.getEquipment();
                        equipment.setHelmet(new ItemStack(armors[0]));
                        equipment.setHelmetDropChance(0);
                        equipment.setChestplate(new ItemStack(armors[1]));
                        equipment.setChestplateDropChance(0);
                        equipment.setLeggings(new ItemStack(armors[2]));
                        equipment.setLeggingsDropChance(0);
                        equipment.setBoots(new ItemStack(armors[3]));
                        equipment.setBootsDropChance(0);
                    });
                }

                if (value2.has("hand")) {
                    JsonArray hand = value2.getAsJsonArray("hand");
                    Material[] hands = {
                            Material.valueOf(hand.get(0).getAsString()),
                            Material.valueOf(hand.get(1).getAsString()),
                    };
                    ret.add(gm -> {
                        SLMobModel mod = SoulLand.getMobManager().getModelId(id);
                        EntityEquipment equipment = mod.getEquipment();
                        equipment.setItemInMainHand(new ItemStack(hands[0]));
                        equipment.setItemInMainHandDropChance(0);
                        equipment.setItemInOffHand(new ItemStack(hands[1]));
                        equipment.setItemInOffHandDropChance(0);
                    });
                }

                return gm -> ret.forEach(c -> c.accept(gm));
            }
            case DESPAWN_MODEL -> {
                if (value.isJsonPrimitive()) {
                    String id = value.getAsString();
                    return gm -> {
                        SLMobModel model = SoulLand.getMobManager().getModelId(id);
                        if (model != null) model.remove();
                    };
                } else {
                    JsonArray value1 = value.getAsJsonArray();
                    List<Consumer<GameManager>> ret = new ArrayList<>();
                    value1.forEach(ele -> ret.add(gm -> {
                        SLMobModel model = SoulLand.getMobManager().getModelId(ele.getAsString());
                        if (model != null) model.remove();
                    }));
                    return gm -> ret.forEach(c -> c.accept(gm));
                }
            }
            case MOVE_MODEL -> {
                JsonObject value1 = value.getAsJsonObject();
                String id = value1.get("id").getAsString();
                Moveset m;
                JsonElement moveset = value1.get("moveset");
                if (moveset.isJsonPrimitive()) {
                    RefSource rs = parse(moveset.getAsString());
                    if (rs == null) return null;
                    m = getMoveset(rs.ref,rs.source);
                } else if (moveset.isJsonObject()) m = getMoveset(moveset.getAsJsonObject());
                else return null;
                return gm -> {
                    SLMobModel model = SoulLand.getMobManager().getModelId(id);
                    if (model != null) model.applyMoveset(m);
                };
            }
            case TELEPORT_MODEL -> {
                JsonObject value1 = value.getAsJsonObject();

                String id = value1.get("id").getAsString();

                JsonArray to = value1.getAsJsonArray("to");
                double[] xyz = {0.0, 0.0, 0.0};
                xyz[0] = to.get(0).getAsDouble();
                xyz[1] = to.get(1).getAsDouble();
                xyz[2] = to.get(2).getAsDouble();

                double[] yawpitch = {0.0, 0.0};
                if (value1.has("yp")) {
                    JsonArray yp = value1.getAsJsonArray("yp");
                    yawpitch[0] = yp.get(0).getAsDouble();
                    yawpitch[1] = yp.get(1).getAsDouble();
                }

                String world = value1.get("world").getAsString();
                World world1 = Bukkit.getWorld(world);

                return gm -> {
                    SLMobModel model = SoulLand.getMobManager().getModelId(id);
                    if (model != null) {
                        model.getHandle().teleport(new Location(world1, xyz[0], xyz[1], xyz[2], (float) yawpitch[0], (float) yawpitch[1]), PlayerTeleportEvent.TeleportCause.COMMAND);
                    }
                };
            }
        }
        return null;
    }

    public Consumer<GameManager> getEventOrAction(@Nullable String ref) {
        if (ref == null) return null;
        RefSource rs = parse(ref);
        if (rs != null) {
            Consumer<GameManager> ret = getEvent(rs.ref, rs.source);
            return ret == null ? getAction(rs.ref, rs.source) : ret;
        }
        JsonElement o;
        try {
            o = GSON.fromJson(ref, JsonElement.class);
        } catch (Exception e) {
            return null;
        }
        if (o.isJsonArray()) {
            JsonArray o1 = o.getAsJsonArray();
            List<Consumer<GameManager>> list = new ArrayList<>();
            o1.forEach(e -> {
                Consumer<GameManager> c = getAction(e.getAsJsonObject());
                if (c != null) list.add(c);
            });
            return gm -> list.forEach(c -> c.accept(gm));
        }
        if (o.isJsonObject()) {
            JsonObject o1 = o.getAsJsonObject();
            Consumer<GameManager> ret = getEvent(o1);
            return ret == null ? getAction(o1) : ret;
        }
        return null;
    }

    public Dialogue getDialogue(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        return getDialogue(json.getAsJsonObject(identifier));
    }

    public Dialogue getDialogue(@Nullable JsonObject obj) {
        if (obj == null) return null;
        if (!obj.has("type")) return null;
        if (obj.get("type").isJsonNull()) return null;
        if (!"DIALOGUE".equals(obj.get("type").getAsString())) return null;

        JsonArray dialogues = obj.getAsJsonArray("dialogues");
        Dialogue ret = new Dialogue();
        dialogues.forEach(e -> {
            int at = e.getAsJsonObject().get("at").getAsInt();
            String text = e.getAsJsonObject().get("val").getAsString();
            ret.addDialogue(at, text);
        });
        return ret;
    }

    public Sequence getSequence(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        return getSequence(json.getAsJsonObject(identifier));
    }

    public Sequence getSequence(@Nullable JsonObject obj) {
        if (obj == null) return null;
        if (!obj.has("type")) return null;
        if (obj.get("type").isJsonNull()) return null;
        if (!"SEQUENCE".equals(obj.get("type").getAsString())) return null;

        JsonArray actions = obj.getAsJsonArray("actions");
        Sequence ret = new Sequence();
        actions.forEach(e -> {
            int at = e.getAsJsonObject().get("at").getAsInt();
            JsonElement value = e.getAsJsonObject().get("value");
            Consumer<GameManager> ac = getEvent(value);
            if (ac == null && value.isJsonObject()) ac = getAction(value.getAsJsonObject());
            if (ac != null) ret.addAction(new Action(at, ac));
        });
        return ret;
    }

    private RefSource parse(String ref) {
        String[] reff = ref.split("\\.");
        try {
            return new RefSource(reff[1], ObjectSource.valueOf(reff[0]));
        } catch (Exception e) {
            return null;
        }
    }

    private record RefSource(String ref, ObjectSource source) {
    }

    public Trigger getTrigger(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        return getTrigger(json.getAsJsonObject(identifier));
    }

    public Trigger getTrigger(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"TRIGGER".equals(type)) return null;
        String subtype = obj.get("subtype").getAsString();
        return switch (subtype) {
            case "GO_TO_LOCATION" -> getTriggerGoToLocation(obj);
            case "KILL_MOB" -> getTriggerKillMob(obj);
            default -> null;
        };
    }

    public TriggerGoToLocation getTriggerGoToLocation(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        JsonObject obj = json.getAsJsonObject(identifier);
        return getTriggerGoToLocation(obj);
    }

    public TriggerGoToLocation getTriggerGoToLocation(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"TRIGGER".equals(type)) return null;
        if (!"GO_TO_LOCATION".equals(obj.get("subtype").getAsString())) return null;

        JsonObject value = obj.get("value").getAsJsonObject();

        World w = Bukkit.getWorld(value.get("world").getAsString());
        if (w == null) return null;

        double[] a = GSON.fromJson(value.get("borders").getAsJsonArray().get(0), double[].class);
        double[] b = GSON.fromJson(value.get("borders").getAsJsonArray().get(1), double[].class);
        TriggerGoToLocation g = new TriggerGoToLocation(new BoundingBox(a[0], a[1], a[2], b[0], b[1], b[2]), w);
        startAndFinish(obj, g);
        trigger(value, g);

        return g;
    }

    public TriggerKillMob getTriggerKillMob(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        JsonObject obj = json.getAsJsonObject(identifier);
        return getTriggerKillMob(obj);
    }

    public TriggerKillMob getTriggerKillMob(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"TRIGGER".equals(type)) return null;
        if (!"KILL_MOB".equals(obj.get("subtype").getAsString())) return null;

        JsonObject value = obj.getAsJsonObject("value");
        World w = Bukkit.getWorld(value.get("world").getAsString());
        double[] p = GSON.fromJson(value.get("location"), double[].class);
        Location l = new Location(w, p[0], p[1], p[2], (float) p[3], (float) p[4]);
        JsonElement mob = value.get("mob");
        if (mob.isJsonPrimitive()) {
            String[] ref = mob.getAsString().split("\\.");
            ObjectSource os = ObjectSource.valueOf(ref[0]);
            String ref1 = ref[1];
            TriggerKillMob g = new TriggerKillMob(l, getMobGenericCustomizer(ref1, os));
            startAndFinish(obj, g);
            trigger(value, g);
            return g;
        }
        if (mob instanceof JsonObject mobObj) {
            TriggerKillMob g = new TriggerKillMob(l, getMobGenericCustomizer(mobObj));
            startAndFinish(obj, g);
            trigger(value, g);
            return g;
        }
        return null;
    }

    public Move getMove(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        return getMove(json.getAsJsonObject(identifier));
    }

    public Move getMove(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"MOVE".equals(type)) return null;
        String subtype = obj.get("subtype").getAsString();
        return switch (subtype) {
            case "CARTESIAN" -> getMoveCart(obj);
            case "POLAR" -> getMovePol(obj);
            case "SIN" -> getMoveSin(obj);
            default -> null;
        };
    }

    public MoveCart getMoveCart(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"MOVE".equals(type)) return null;
        if (!"CARTESIAN".equals(obj.get("subtype").getAsString())) return null;

        JsonObject value = obj.get("value").getAsJsonObject();
        int duration = value.get("duration").getAsInt();
        double x = value.get("x").getAsDouble();
        double y = value.get("y").getAsDouble();
        double z = value.get("z").getAsDouble();
        float yaw = value.get("yaw").getAsFloat();
        float pitch = value.get("pitch").getAsFloat();

        boolean ignore = value.has("ignore") && value.get("ignore").getAsBoolean();
        boolean look = value.has("look") && value.get("look").getAsBoolean();

        return new MoveCart(duration, x, y, z, yaw, pitch, ignore, look);
    }

    public MovePol getMovePol(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"MOVE".equals(type)) return null;
        if (!"POLAR".equals(obj.get("subtype").getAsString())) return null;

        JsonObject value = obj.get("value").getAsJsonObject();
        int duration = value.get("duration").getAsInt();
        double velocity = value.get("velocity").getAsDouble();
        float yaw = value.get("yaw").getAsFloat();
        float pitch = value.get("pitch").getAsFloat();

        boolean ignore = value.has("ignore") && value.get("ignore").getAsBoolean();
        boolean look = value.has("look") && value.get("look").getAsBoolean();

        return new MovePol(duration, yaw, pitch, velocity, ignore, look);
    }

    public MoveSin getMoveSin(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"MOVE".equals(type)) return null;
        if (!"SIN".equals(obj.get("subtype").getAsString())) return null;

        JsonObject value = obj.get("value").getAsJsonObject();
        int duration = value.get("duration").getAsInt();
        char direction = value.get("direction").getAsCharacter();
        double amplitude = value.get("amplitude").getAsDouble();
        double period = value.get("period").getAsDouble();
        float yaw = value.get("yaw").getAsFloat();
        float pitch = value.get("pitch").getAsFloat();

        boolean ignore = value.has("ignore") && value.get("ignore").getAsBoolean();
        boolean look = value.has("look") && value.get("look").getAsBoolean();

        return new MoveSin(duration, amplitude, period, direction, yaw, pitch, ignore, look);
    }

    public Moveset getMoveset(String identifier, ObjectSource source) {
        JsonObject json = objects.get(source);
        if (json == null) return null;
        if (!json.has(identifier)) return null;
        return getMoveset(json.getAsJsonObject(identifier));
    }

    public Moveset getMoveset(@NotNull JsonObject obj) {
        String type = obj.get("type").getAsString();
        if (!"MOVE_SET".equals(type)) return null;
        JsonArray value = obj.getAsJsonArray("value");
        Moveset moveset = new Moveset();
        value.forEach(e -> {
            JsonObject e1 = e.getAsJsonObject();
            moveset.addMove(getMove(e1));
        });
        return moveset;
    }
}
