package me.emafire003.dev.ohmymeteors.mixin;

import me.emafire003.dev.ohmymeteors.util.MeteorUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.fml.ModList;
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

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.CONFIG;


//(there would be a way to like list all of the loaded chunks but it seems a bit impractical when we can just target a random online player)
@Mixin(ServerLevel.class)
public abstract class WorldSpawnMeteorMixin extends Level implements WorldGenLevel {

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
        if(CONFIG.meteorSpawning.meteor_spawn_chance < 0 || tickRateManager().isFrozen()){
            return;
        }

        if(CONFIG.meteorSpawning.should_cooldown_between_meteors && meteorCooldown > 0){
            meteorCooldown = meteorCooldown - 1;
            return; //Hey. this return is important. I totally haven't discovered i forgot to put it here because like 200 meteors spawned in the span of a second in my face. Not at all.
        }

        //TODO check if it's a problem performance wise?
        ServerPlayer p = this.getRandomPlayer();
        if(p == null){
            return;
        }

        /// As for spawning, region overrides biome overrides dimension
        Holder<DimensionType> current_dim = p.level().dimensionTypeRegistration();
        Holder<Biome> current_biome = p.level().getBiome(p.blockPosition());
        if(!MeteorUtils.canMeteorSpawn(p, current_dim, current_biome)){
            return;
        }

        /// For the chance, biome > dim > global
        /// Aka the biome chance overrides the dimension chance which in turn overrides the global parameter
        int chance = CONFIG.meteorSpawning.meteor_spawn_chance;

        //If chance is negative, it means that no natural meteor should spawn

        //If the current dimension is in the map, it will override the chance thing
        if(CONFIG.meteorSpawning.dimension_chances.containsKey(current_dim.getRegisteredName())){
            chance = CONFIG.meteorSpawning.dimension_chances.get(current_dim.getRegisteredName());
        }

        //If the biome is in the map the chance gets overridden again
        if(CONFIG.meteorSpawning.biome_chances.containsKey(current_biome.getRegisteredName())){
            chance = CONFIG.meteorSpawning.biome_chances.get(current_biome.getRegisteredName());
        }

        if(CONFIG.meteorSpawning.modify_spawn_chance_at_night && this.isNight()){
            chance = CONFIG.meteorSpawning.meteor_night_spawn_chance;
            if(CONFIG.meteorSpawning.dimension_night_chances.containsKey(current_dim.getRegisteredName())){
                chance = CONFIG.meteorSpawning.dimension_night_chances.get(current_dim.getRegisteredName());
            }
            if(CONFIG.meteorSpawning.biome_night_chances.containsKey(current_biome.getRegisteredName())){
                chance = CONFIG.meteorSpawning.biome_night_chances.get(current_biome.getRegisteredName());
            }
        }

        if(this.getRandom().nextIntBetweenInclusive(0, chance) == 0){
            if(CONFIG.meteorShowerSection.meteor_showers_enabled){
                if(this.getRandom().nextIntBetweenInclusive(0, CONFIG.meteorShowerSection.meteor_shower_chance) == 0){
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

            if(CONFIG.meteorSpawning.should_cooldown_between_meteors){
                meteorCooldown = 20*CONFIG.meteorSpawning.min_meteor_cooldown_time;
            }
        }
    }

    protected WorldSpawnMeteorMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }


}
