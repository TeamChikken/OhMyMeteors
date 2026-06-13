package me.emafire003.dev.ohmymeteors.mixin;

import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.util.MeteorUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
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

//(there would be a way to like list all of the loaded chunks but it seems a bit impractical when we can just target a random online player)
@Mixin(ServerLevel.class)
public abstract class WorldSpawnMeteorMixin extends Level implements WorldGenLevel {

    protected WorldSpawnMeteorMixin(WritableLevelData properties, ResourceKey<Level> registryRef, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Shadow @Nullable public abstract ServerPlayer getRandomPlayer();

    @Shadow public abstract boolean addFreshEntity(Entity entity);

    @Shadow public abstract @NotNull ChunkSource getChunkSource();

    @Shadow public abstract @NotNull ServerLevel getLevel();

    @Shadow public abstract @NotNull List<ServerPlayer> players();

    @Unique
    int meteorCooldown = 0;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tickSpawnMeteor(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        //If chance is negative, it means that no natural meteor should spawn so return early
        if(Config.METEOR_SPAWN_CHANCE < 0){
            return;
        }

        if(Config.SHOULD_COOLDOWN_BETWEEN_METEORS && meteorCooldown > 0){
            meteorCooldown = meteorCooldown - 1;
            return; //Hey. this return is important. I totally haven't discovered i forgot to put it here because like 200 meteors spawned in the span of a second in my face. Not at all.
        }

        //TODO check if it's a problem performance wise?
        ServerPlayer p = this.getRandomPlayer();
        if(p == null){
            return;
        }

        /// As for spawning, region overrides biome overrides dimension
        Holder<DimensionType> current_dim = p.getLevel().dimensionTypeRegistration();
        Holder<Biome> current_biome = p.getLevel().getBiome(p.blockPosition());
        if(!MeteorUtils.canMeteorSpawn(p, current_dim, current_biome)){
            return;
        }


        /// For the chance, biome > dim > global
        /// Aka the biome chance overrides the dimension chance which in turn overrides the global parameter
        int chance = Config.METEOR_SPAWN_CHANCE;

        //If chance is negative, it means that no natural meteor should spawn

        //If the current dimension is in the map, it will override the chance thing
        if(Config.DIMENSION_CHANCES.containsKey(current_dim.unwrapKey().get().location().toString())){
            chance = Config.DIMENSION_CHANCES.get(current_dim.unwrapKey().get().location().toString());
        }

        //If the biome is in the map the chance gets overridden again
        if(Config.BIOME_CHANCES.containsKey(current_biome.unwrapKey().get().location().toString())){
            chance = Config.BIOME_CHANCES.get(current_biome.unwrapKey().get().location().toString());
        }

        if(Config.MODIFY_SPAWN_CHANCE_AT_NIGHT && this.isNight()){
            chance = Config.METEOR_NIGHT_SPAWN_CHANCE;
            if(Config.DIMENSION_NIGHT_CHANCES.containsKey(current_dim.unwrapKey().get().location().toString())){
                chance = Config.DIMENSION_NIGHT_CHANCES.get(current_dim.unwrapKey().get().location().toString());
            }
            if(Config.BIOME_NIGHT_CHANCES.containsKey(current_biome.unwrapKey().get().location().toString())){
                chance = Config.BIOME_NIGHT_CHANCES.get(current_biome.unwrapKey().get().location().toString());
            }
        }

        if(this.getRandom().nextIntBetweenInclusive(0, chance) == 0){
            if(Config.METEOR_SHOWERS_ENABLED){
                if(this.getRandom().nextIntBetweenInclusive(0, Config.METEOR_SHOWER_CHANCE) == 0){
                    int r = this.getRandom().nextIntBetweenInclusive(1, 2);
                    if(r == 1){
                        MeteorUtils.spawnMeteorShowerDelayed(((ServerLevel) (Object) this), p);
                    }else if(r==2){
                        MeteorUtils.spawnMeteorShowerDelayedDirection(((ServerLevel) (Object) this), p);
                    }else{
                        MeteorUtils.spawnMeteorShowerInstant(((ServerLevel) (Object) this), p);
                    }

                }else{
                    MeteorUtils.spawnMeteor(((ServerLevel) (Object) this), p, false);
                }
            }else{
                MeteorUtils.spawnMeteor(((ServerLevel) (Object) this), p, false);
            }

            if(Config.SHOULD_COOLDOWN_BETWEEN_METEORS){
                meteorCooldown = 20*Config.MIN_METEOR_COOLDOWN_TIME;
            }
        }
    }

}
