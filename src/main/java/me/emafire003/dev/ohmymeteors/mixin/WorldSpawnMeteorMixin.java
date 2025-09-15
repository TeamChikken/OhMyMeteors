package me.emafire003.dev.ohmymeteors.mixin;

import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
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
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity.spawnMeteor;

//(there would be a way to like list all of the loaded chunks but it seems a bit impractical when we can just target a random online player)
@Mixin(ServerWorld.class)
public abstract class WorldSpawnMeteorMixin extends World implements StructureWorldAccess {

    protected WorldSpawnMeteorMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

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

        //TODO check if it's a problem performance wise?
        ServerPlayerEntity p = this.getRandomAlivePlayer();
        if(p == null){
            return;
        }

        /// As for spawning, region overrides biome overrides dimension

        RegistryEntry<DimensionType> current_dim = p.getWorld().getDimensionEntry();

        if(!MeteorProjectileEntity.canSpawnInDimension(current_dim)){
            return;
        }

        RegistryEntry<Biome> current_biome = p.getWorld().getBiome(p.getBlockPos());

        if(!MeteorProjectileEntity.canSpawnInBiome(current_biome)){
            return;
        }

        /// For the chance, biome > dim > global
        /// Aka the biome chance overrides the dimension chance which in turn overrides the global parameter
        int chance = Config.METEOR_SPAWN_CHANCE;

        //If the current dimension is in the map, it will override the chance thing
        if(Config.DIMENSION_CHANCES.containsKey(current_dim.getKey().get().getValue().toString())){
            chance = Config.DIMENSION_CHANCES.get(current_dim.getKey().get().getValue().toString());
        }

        //If the biome is in the map the chance gets overridden again
        if(Config.BIOME_CHANCES.containsKey(current_biome.getKey().get().getValue().toString())){
            chance = Config.BIOME_CHANCES.get(current_biome.getKey().get().getValue().toString());
        }

        if(Config.MODIFY_SPAWN_CHANCE_AT_NIGHT && this.isNight()){
            chance = Config.METEOR_NIGHT_SPAWN_CHANCE;
            if(Config.DIMENSION_NIGHT_CHANCES.containsKey(current_dim.getKey().get().getValue().toString())){
                chance = Config.DIMENSION_NIGHT_CHANCES.get(current_dim.getKey().get().getValue().toString());
            }
            if(Config.BIOME_NIGHT_CHANCES.containsKey(current_biome.getKey().get().getValue().toString())){
                chance = Config.BIOME_NIGHT_CHANCES.get(current_biome.getKey().get().getValue().toString());
            }
        }

        if(this.getRandom().nextBetween(0, chance) == 0){
            spawnMeteor(((ServerWorld) (Object) this), p);
            if(Config.SHOULD_COOLDOWN_BETWEEN_METEORS){
                meteorCooldown = 20*Config.MIN_METEOR_COOLDOWN_TIME;
            }
        }
    }


    @Unique
    public boolean checkDimension(RegistryEntry<DimensionType> current_dim){
        //Checks all the dimensions specified in the config file. As soon as it finds one, sets dimension ok to true
        //and then stops checking
        return Config.SPAWN_DIMENSIONS.contains(current_dim.getKey().get().getValue().toString());
    }

    @Unique
    public boolean checkBiome(RegistryEntry<Biome> current_biome){
        //Checks all the dimensions specified in the config file. As soon as it finds one, sets dimension ok to true
        //and then stops checking

        //If true means whitelist aka it HAS to be present
        //if false means in MUST NOT be present
        if(Config.BIOME_LIST_MODE){
            return Config.BIOME_SPAWN_LIST.contains(current_biome.getKey().get().getValue().toString());
        }else{
            return !Config.BIOME_SPAWN_LIST.contains(current_biome.getKey().get().getValue().toString());
        }
    }
}
