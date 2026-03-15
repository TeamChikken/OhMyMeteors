package me.emafire003.dev.ohmymeteors.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public interface PlayerJoinEvent {
    Event<PlayerJoinEvent> EVENT = EventFactory.createArrayBacked(PlayerJoinEvent.class, (listeners) -> (Player player) -> {
        for (PlayerJoinEvent listener : listeners) {
           listener.playerJoin(player);
        }
    });

    void playerJoin(Player player);
}
