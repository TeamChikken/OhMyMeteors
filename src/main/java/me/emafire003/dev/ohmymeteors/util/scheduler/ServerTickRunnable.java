package me.emafire003.dev.ohmymeteors.util.scheduler;

import net.minecraft.server.MinecraftServer;

public interface ServerTickRunnable {
    boolean run(MinecraftServer server, int ticks);
}
