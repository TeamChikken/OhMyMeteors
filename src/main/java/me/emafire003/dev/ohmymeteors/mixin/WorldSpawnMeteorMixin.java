package me.emafire003.dev.ohmymeteors.mixin;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.util.MeteorUtils;
import me.emafire003.dev.ohmymeteors.util.datasaver.SimpleDataSaverUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
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

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.CONFIG;


@Debug(export = true)
//(there would be a way to like list all of the loaded chunks but it seems a bit impractical when we can just target a random online player)
@Mixin(ServerLevel.class)
public abstract class WorldSpawnMeteorMixin extends Level implements WorldGenLevel {


    protected WorldSpawnMeteorMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
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
        if(CONFIG.meteorSpawning.meteor_spawn_chance < 0){
            return;
        }

        boolean increasedSpawnChance = false;

        /// If the option to increase the chance is enabled, increase the chance accordingly
        if(CONFIG.meteorSpawning.increase_spawn_chance_with_time){
            if(!(CONFIG.meteorSpawning.increase_spawn_chance_limit <= Math.abs(CONFIG.meteorSpawning.increase_spawn_chance_by)*SimpleDataSaverUtils.DATA.getChanceIncrements())){
                if(SimpleDataSaverUtils.DATA.getLastIncreaseTickTime() == 0 ||
                        ( ((levelData.getGameTime() % 2147483647L) != SimpleDataSaverUtils.DATA.getLastIncreaseTickTime()) &&
                                ((levelData.getGameTime() % 2147483647L)-SimpleDataSaverUtils.DATA.getLastIncreaseTickTime())%CONFIG.meteorSpawning.increase_spawn_chance_every == 0)){
                    SimpleDataSaverUtils.DATA.setChanceIncrements(SimpleDataSaverUtils.DATA.getChanceIncrements()+1);
                    SimpleDataSaverUtils.DATA.setLastIncreaseTickTime(levelData.getGameTime() % 2147483647L);
                    SimpleDataSaverUtils.saveFile();
                    increasedSpawnChance = true;
                }
            }
        }

        /// If the option to spawn meteors only after some time is enabled, check how much time has passed
        if(CONFIG.meteorSpawning.start_spawning_after_delay){
            //If the gametime isn't longer than the number of specified days, return early and don't spawn meteors
            if(!(levelData.getGameTime() % 2147483647L >= CONFIG.meteorSpawning.start_spawning_after_days * 24000L)){
                return;
            }
            //Announcements stuff
            if(!SimpleDataSaverUtils.DATA.timeTriggered){
                SimpleDataSaverUtils.DATA.setTimeTriggered(true);
                SimpleDataSaverUtils.saveFile();
                if(CONFIG.notificationSection.announce_meteor_spawn_start){
                    players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_spawn_start").withStyle(ChatFormatting.RED)),
                            CONFIG.notificationSection.actionbar_announcements));
                }
            }

        }

        /// If the option to spawn meteors only after a dimension has been entered, check if it has
        if(CONFIG.meteorSpawning.start_spawning_after_dimension){
            AtomicBoolean found = new AtomicBoolean(false);
            CONFIG.meteorSpawning.start_spawning_after_dimension_list.forEach( dim -> {
                if(found.get() || SimpleDataSaverUtils.DATA.enteredDimensions.contains(dim)){
                    found.set(true);
                    return;
                }

            });
            //If the dimension(s) specified in the config have not been entered yet, retrun without spawning.
            if(!found.get()){
                return;
            }else{
                //Announcements stuff
                if(!SimpleDataSaverUtils.DATA.dimensionTriggered){
                    SimpleDataSaverUtils.DATA.setDimensionTriggered(true);
                    SimpleDataSaverUtils.saveFile();
                    if(CONFIG.notificationSection.announce_meteor_spawn_start){
                        players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_spawn_start").withStyle(ChatFormatting.RED)),
                                CONFIG.notificationSection.actionbar_announcements));
                    }
                }
            }
        }

        /// Returns early if we are in cooldown time
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
        if(CONFIG.meteorSpawning.dimension_chances.containsKey(current_dim.unwrapKey().get().location().toString())){
            chance = CONFIG.meteorSpawning.dimension_chances.get(current_dim.unwrapKey().get().location().toString());
        }

        //If the biome is in the map the chance gets overridden again
        if(CONFIG.meteorSpawning.biome_chances.containsKey(current_biome.unwrapKey().get().location().toString())){
            chance = CONFIG.meteorSpawning.biome_chances.get(current_biome.unwrapKey().get().location().toString());
        }

        if(CONFIG.meteorSpawning.modify_spawn_chance_at_night && this.isNight()){
            chance = CONFIG.meteorSpawning.meteor_night_spawn_chance;
            if(CONFIG.meteorSpawning.dimension_night_chances.containsKey(current_dim.unwrapKey().get().location().toString())){
                chance = CONFIG.meteorSpawning.dimension_night_chances.get(current_dim.unwrapKey().get().location().toString());
            }
            if(CONFIG.meteorSpawning.biome_night_chances.containsKey(current_biome.unwrapKey().get().location().toString())){
                chance = CONFIG.meteorSpawning.biome_night_chances.get(current_biome.unwrapKey().get().location().toString());
            }
        }

        if(CONFIG.meteorSpawning.increase_spawn_chance_with_time){
            if(CONFIG.meteorSpawning.increase_spawn_chance_limit >= Math.abs(CONFIG.meteorSpawning.increase_spawn_chance_by)*SimpleDataSaverUtils.DATA.getChanceIncrements()){
                //Minus so it's an increase in chance. 1 in 100 is more likely than 1 in 110
                chance = chance-CONFIG.meteorSpawning.increase_spawn_chance_by*SimpleDataSaverUtils.DATA.getChanceIncrements();
                if(chance < 1){
                    chance = 1;
                }
            }
            if(CONFIG.notificationSection.announce_meteor_spawn_increments && increasedSpawnChance){
                int finalChance = chance;
                players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_chance_increments", finalChance).withStyle(ChatFormatting.RED)),
                        CONFIG.notificationSection.actionbar_announcements));
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


}
