package me.wisemann64.soulland.gameplay.objects;

import me.wisemann64.soulland.SoulLand;

import java.io.File;

public enum ObjectSource {

    $ACTIONS("gameplay/actions.json"),
    DEMO_OBJECTS("gameplay/demo/objects.json"),
    ;

    private final String path;
    private final File file;

    ObjectSource(String path) {
        this.path = path;
        file = new File(SoulLand.getPlugin().getDataFolder().getPath() + "/" + path);
    }

    public String getPath() {
        return path;
    }

    public File getFile() {
        return file;
    }
}
