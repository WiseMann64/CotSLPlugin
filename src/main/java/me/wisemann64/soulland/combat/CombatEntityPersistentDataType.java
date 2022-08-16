package me.wisemann64.soulland.combat;

import me.wisemann64.soulland.SoulLand;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CombatEntityPersistentDataType implements PersistentDataType<String,CombatEntity> {
    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<CombatEntity> getComplexType() {
        return CombatEntity.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull CombatEntity combatEntity, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return combatEntity.getHandle().getUniqueId().toString();
    }

    @NotNull
    @Override
    public CombatEntity fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return SoulLand.getCombatEntity(UUID.fromString(s));
    }
}
