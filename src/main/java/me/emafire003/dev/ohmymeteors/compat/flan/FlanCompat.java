package me.emafire003.dev.ohmymeteors.compat.flan;
/*
import io.github.flemmli97.flan.Flan;
import io.github.flemmli97.flan.api.ClaimHandler;*/
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class FlanCompat {

    //public static Identifier CAN_SPAWN = Identifier.fromNamespaceAndPath(Flan.MODID, "allow_meteor_spawn");

    public static boolean canSpawnHere(ServerPlayer player, BlockPos pos){
        try{
            //TODO update when availdalble
            // return ClaimHandler.canInteract(player, pos, CAN_SPAWN);
            return true;
        }catch (NullPointerException e){
            //It means there is no chunk
            return true;
        }
    }

    public static void registerFlan(){

    }

}
