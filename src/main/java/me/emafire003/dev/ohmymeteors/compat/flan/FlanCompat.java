package me.emafire003.dev.ohmymeteors.compat.flan;

import io.github.flemmli97.flan.api.ClaimHandler;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class FlanCompat {

    public static ResourceLocation CAN_SPAWN = ResourceLocation.fromNamespaceAndPath(OhMyMeteors.MOD_ID, "allow_meteor_spawn");

    public static boolean canSpawnHere(ServerPlayer player, BlockPos pos){
        try{
            return ClaimHandler.canInteract(player, pos, CAN_SPAWN);
        }catch (NullPointerException e){
            //It means there is no chunk
            return true;
        }
    }

    public static void registerFlan(){
        CAN_SPAWN = ResourceLocation.fromNamespaceAndPath(OhMyMeteors.MOD_ID, "allow_meteor_spawn");
    }

}
