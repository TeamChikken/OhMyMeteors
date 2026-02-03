package me.emafire003.dev.ohmymeteors.mixin;


import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerSessionAccessor {
    @Accessor("session")
    LevelStorage.Session ohmymeteors$getSession();
}
