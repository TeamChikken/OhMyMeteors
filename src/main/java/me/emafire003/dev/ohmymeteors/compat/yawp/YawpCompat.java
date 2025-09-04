package me.emafire003.dev.ohmymeteors.compat.yawp;

import de.z0rdak.yawp.api.FlagEvaluator;
import de.z0rdak.yawp.api.events.region.FlagCheckEvent;
import de.z0rdak.yawp.api.events.region.FlagCheckResult;
import de.z0rdak.yawp.core.flag.FlagState;
import de.z0rdak.yawp.core.flag.RegionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class YawpCompat {

    public static boolean canSpawnHere(ServerWorld world, BlockPos pos){
        FlagCheckEvent flagCheckEvent = new FlagCheckEvent(pos, RegionFlag.EXPLOSION_ENTITY, world.getRegistryKey());
        FlagCheckResult flagCheckResult = FlagEvaluator.evaluate(flagCheckEvent);
        FlagState flagState = flagCheckResult.getFlagState();
        if(flagState.equals(FlagState.DENIED)){
            return false;
        }else{
            return true;
        }
        //return flagState.equals(FlagState.ALLOWED); //returns true if the flagState is allowed, returns false otherwise. which may be the problem

    }
}
