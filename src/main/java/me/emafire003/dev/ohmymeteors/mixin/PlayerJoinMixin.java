package me.emafire003.dev.ohmymeteors.mixin;

import me.emafire003.dev.ohmymeteors.events.PlayerJoinEvent;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerJoinMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    public void playerJoinInvoker(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci){
        PlayerJoinEvent.EVENT.invoker().playerJoin(serverPlayer);
    }
}
