package me.emafire003.dev.ohmymeteors.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerSessionAccessor {
    @Accessor("storageSource")
    LevelStorageSource.LevelStorageAccess ohmymeteors$getStorageSource();
}
