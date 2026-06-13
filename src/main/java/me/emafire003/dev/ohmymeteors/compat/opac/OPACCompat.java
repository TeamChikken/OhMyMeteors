package me.emafire003.dev.ohmymeteors.compat.opac;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.player.config.api.v2.PlayerConfigOptions;

public class OPACCompat {

    public static boolean canSpawnHere(ServerLevel level, BlockPos pos){
        OpenPACServerAPI api = OpenPACServerAPI.get(level.getServer());
        IPlayerChunkClaimAPI claim = api.getServerClaimsManager().get(level.dimension().location(), pos);
        return api.getChunkProtection().getConfig(claim).isOptionAllowed(PlayerConfigOptions.CLAIM_EXCEPTION_ENTITIES_BY_EXPLOSIONS);
    }
}
