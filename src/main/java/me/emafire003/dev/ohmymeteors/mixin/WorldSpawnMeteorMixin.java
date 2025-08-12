package me.emafire003.dev.ohmymeteors.mixin;

import me.emafire003.dev.ohmymeteors.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity.spawnMeteor;

//(there would be a way to like list all of the loaded chunks but it seems a bit impractical when we can just target a random online player)
@Mixin(ServerWorld.class)
public abstract class WorldSpawnMeteorMixin extends World implements StructureWorldAccess {

    @Shadow @Nullable public abstract ServerPlayerEntity getRandomAlivePlayer();

    @Shadow public abstract boolean spawnEntity(Entity entity);

    @Shadow public abstract ChunkManager getChunkManager();

    @Shadow public abstract ServerWorld toServerWorld();

    @Shadow public abstract List<ServerPlayerEntity> getPlayers();

    @Unique
    int meteorCooldown = 0;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tickSpawnMeteor(BooleanSupplier shouldKeepTicking, CallbackInfo ci){

        if(Config.SHOULD_COOLDOWN_BETWEEN_METEORS && meteorCooldown > 0){
            meteorCooldown = meteorCooldown - 1;
            return; //Hey. this return is important. I totally haven't discovered i forgot to put it here because like 200 meteors spawned in the span of a second in my face. Not at all.
        }

        AtomicBoolean dimension_ok = new AtomicBoolean(false);
        //TODO check if it's a problem performance wise?
        PlayerEntity p = this.getRandomAlivePlayer();
        if(p == null){
            return;
        }

        RegistryEntry<DimensionType> current_dim = p.getWorld().getDimensionEntry();

        //Checks all the dimensions specified in the config file. As soon as it finds one, sets dimension ok to true
        //and then stops checking
        //todo this will check every time. If i add per-dimension chances it's ok.
        Config.SPAWN_DIMENSIONS.forEach(dim -> {
            if(dimension_ok.get()){{
                return;
            }}
            if(dim.equals(current_dim.getIdAsString())){
                dimension_ok.set(true);
            }
        }
        );

        //If dimension_ok is still false, it means  the meteor can't spawn here so set it to true
        if(!dimension_ok.get()){
            return;
        }


        int chance = Config.METEOR_SPAWN_CHANCE;

        //If the meteor is in the map, it will override the chance thing
        if(Config.DIMENSION_CHANCES.containsKey(current_dim.getIdAsString())){
            chance = Config.DIMENSION_CHANCES.get(current_dim.getIdAsString());
        }

        if(Config.MODIFY_SPAWN_CHANCE_AT_NIGHT && this.isNight()){
            chance = Config.METEOR_NIGHT_SPAWN_CHANCE;
            if(Config.DIMENSION_NIGHT_CHANCES.containsKey(current_dim.getIdAsString())){
                chance = Config.DIMENSION_NIGHT_CHANCES.get(current_dim.getIdAsString());
            }
        }


        if(this.getRandom().nextBetween(0, chance) == 0){
            spawnMeteor(((ServerWorld) (Object) this), p);
            if(Config.SHOULD_COOLDOWN_BETWEEN_METEORS){
                meteorCooldown = 20*Config.MIN_METEOR_COOLDOWN_TIME;
            }
        }
    }

    protected WorldSpawnMeteorMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }
}
