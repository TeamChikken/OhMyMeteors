package me.emafire003.dev.ohmymeteors.compat.yawp;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

public class YawpCompat {

    public static boolean canSpawnHere(ServerLevel world, BlockPos pos){
        return true;
        /*TODO FIX when updatee
        FlagCheckEvent flagCheckEvent = new FlagCheckEvent(pos, RegionFlag.EXPLOSION_ENTITY, world.dimension());
        FlagCheckResult flagCheckResult = FlagEvaluator.evaluate(flagCheckEvent);
        FlagState flagState = flagCheckResult.getFlagState();
        if(flagState.equals(FlagState.DENIED)){
            return false;
        }else{
            return true;
        }*/
        //return flagState.equals(FlagState.ALLOWED); //returns true if the flagState is allowed, returns false otherwise. which may be the problem

    }
}
