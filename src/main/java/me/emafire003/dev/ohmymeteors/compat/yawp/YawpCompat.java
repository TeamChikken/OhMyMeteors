package me.emafire003.dev.ohmymeteors.compat.yawp;

import de.z0rdak.yawp.api.FlagEvaluator;
import de.z0rdak.yawp.api.events.flag.FlagCheckRequest;
import de.z0rdak.yawp.api.events.flag.FlagCheckResult;
import de.z0rdak.yawp.core.flag.FlagState;
import de.z0rdak.yawp.core.flag.RegionFlag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

public class YawpCompat {

    public static boolean canSpawnHere(ServerLevel world, BlockPos pos){
        FlagCheckRequest checkEvent = new FlagCheckRequest(pos, RegionFlag.EXPLOSION_ENTITY, world.dimension());
        FlagCheckResult flagCheckResult = FlagEvaluator.evaluate(checkEvent);
        FlagState flagState = flagCheckResult.getFlagState();
        if(flagState.equals(FlagState.DENIED)){
            return false;
        }else{
            return true;
        }
        //return flagState.equals(FlagState.ALLOWED); //returns true if the flagState is allowed, returns false otherwise. which may be the problem

    }
}