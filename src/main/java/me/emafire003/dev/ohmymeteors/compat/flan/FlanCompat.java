package me.emafire003.dev.ohmymeteors.compat.flan;

import io.github.flemmli97.flan.Flan;
import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.fabric.FlanFabric;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class FlanCompat {

    public static Identifier CAN_SPAWN = Identifier.of(Flan.MODID, "allow_meteor_spawn");

    public static boolean canSpawnHere(ServerPlayerEntity player, BlockPos pos){
        try{
            return ClaimHandler.canInteract(player, pos, CAN_SPAWN);
        }catch (NullPointerException e){
            //It means there is no chunk
            return true;
        }
    }

    public static void registerFlan(){

    }

}
