package me.wisemann64.soulland.gameplay.lock;

import me.wisemann64.soulland.gameplay.GameManager;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public record DoorLock(Location loc, LockManager.LockType type, @Nullable Predicate<GameManager> prompt, @Nullable String keyId) {
}
