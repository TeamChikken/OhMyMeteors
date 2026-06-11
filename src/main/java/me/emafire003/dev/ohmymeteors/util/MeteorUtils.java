package me.emafire003.dev.ohmymeteors.util;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.compat.flan.FlanCompat;
import me.emafire003.dev.ohmymeteors.compat.opac.OPACCompat;
import me.emafire003.dev.ohmymeteors.compat.yawp.YawpCompat;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.util.scheduler.SchedulerUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.CONFIG;

public class MeteorUtils {

    /// A list of currently active meteors out and about around the world. Used for the fog effect
    private static final List<UUID> ALIVE_METEORS = new ArrayList<>();

    public static List<UUID> getAliveMeteors() {
        return ALIVE_METEORS;
    }

    /**Must be called client side*/
    public static void addAliveMeteor(UUID id){
        ALIVE_METEORS.add(id);
        SchedulerUtils.runLater(20*60, (server -> ALIVE_METEORS.remove(id)));
    }

    /**Must be called client side*/
    public static void removeAliveMeteor(UUID id){
        ALIVE_METEORS.remove(id);
    }


    /**Used to get a random meteor position and velocity oriented downwards
     *
     * @return a Pair, where the first value is the Position and teh second one the Velocity*/
    public static Tuple<Vec3, Vec3> getDownwardsMeteorPosAndVelocity(Vec3 originPos, ServerLevel world, int min_spawn_d, int max_spawn_d, double spawn_height){
        //The invert is to also have a chance at having negative coordinates, otherwise they would always be positive
        int invert_x = 1;
        if(world.getRandom().nextBoolean()){
            invert_x = -1;
        }

        int invert_z = 1;
        if(world.getRandom().nextBoolean()){
            invert_z = -1;
        }

        Vec3 pos = new Vec3(originPos.x()+world.getRandom().nextIntBetweenInclusive(min_spawn_d, max_spawn_d)*invert_x,
                spawn_height,
                originPos.z()+world.getRandom().nextIntBetweenInclusive(min_spawn_d, max_spawn_d)*invert_z
        );

        invert_x = 1;
        if(world.getRandom().nextBoolean()){
            invert_x = -1;
        }

        invert_z = 1;
        if(world.getRandom().nextBoolean()){
            invert_z = -1;
        }
        Vec3 vel = new Vec3((world.getRandom().nextFloat()/CONFIG.meteorSpawning.meteor_dispersion_factor)*invert_x, -1.0f*(world.getRandom().nextFloat()+ CONFIG.meteorBehaviourSection.downwards_speed_modifier)*CONFIG.meteorBehaviourSection.downwards_speed_multiplier, (world.getRandom().nextFloat()/CONFIG.meteorSpawning.meteor_dispersion_factor)*invert_z);

        return new Tuple<>(pos, vel);

    }

    /**
     * Gets a meteor object to be spawned in, with a velocity oriented downwards and a spawn position already set up
     * */
    public static MeteorProjectileEntity getDownwardsMeteor(Vec3 originPos, ServerLevel world, int min_spawn_d, int max_spawn_d, double spawn_height, int min_size, int max_size, boolean homing){
        MeteorProjectileEntity meteor = new MeteorProjectileEntity(OMMEntities.METEOR_PROJECTILE_ENTITY.get(), world);

        Tuple<Vec3, Vec3> pos_vel = getDownwardsMeteorPosAndVelocity(originPos, world, min_spawn_d, max_spawn_d, spawn_height);

        meteor.setPosRaw(pos_vel.getA().x, pos_vel.getA().y, pos_vel.getA().z);

        //TODO add variable or config mor max meteor size
        meteor.setSize(world.getRandom().nextIntBetweenInclusive(Math.max(0, min_size), Math.min(50, max_size)));

        if(homing){
            meteor.setDeltaMovement(originPos.subtract(meteor.position()).normalize().multiply(1,1,1).add(0, CONFIG.meteorBehaviourSection.downwards_speed_modifier, 0).scale(CONFIG.meteorBehaviourSection.downwards_speed_multiplier));
        }else{
            meteor.setDeltaMovement(pos_vel.getB());
        }

        return meteor;
    }

    /**Used when spawning an orderd meteor shower where most meteors share the same general direction as the previous one
     * <p>
     * Returns a new meteor object with a general direction similar to the specified one, but slightly different origin and velocity
     * The spawn distances are for the new spread, so keep the generally low*/
    public static MeteorProjectileEntity getDownwardsMeteorSameDirection(Vec3 prev_origin, Vec3 prev_vel, ServerLevel world, int min_spawn_d, int max_spawn_d, double spawn_height, int min_size, int max_size, boolean homing){
        MeteorProjectileEntity meteor = new MeteorProjectileEntity(OMMEntities.METEOR_PROJECTILE_ENTITY.get(), world);

        //The invert is to also have a chance at having negative coordinates, otherwise they would always be positive
        int invert_x = 1;
        if(world.getRandom().nextBoolean()){
            invert_x = -1;
        }

        int invert_z = 1;
        if(world.getRandom().nextBoolean()){
            invert_z = -1;
        }

        Vec3 pos = new Vec3(prev_origin.x()+world.getRandom().nextIntBetweenInclusive(min_spawn_d, max_spawn_d)*invert_x,
                spawn_height,
                prev_origin.z()+world.getRandom().nextIntBetweenInclusive(min_spawn_d, max_spawn_d)*invert_z
        );

        invert_x = 1;
        if(world.getRandom().nextBoolean()){
            invert_x = -1;
        }

        invert_z = 1;
        if(world.getRandom().nextBoolean()){
            invert_z = -1;
        }
        Vec3 vel_modifier = new Vec3((world.getRandom().nextFloat()/5)*invert_x, -1.0f*(world.getRandom().nextFloat()/5), (world.getRandom().nextFloat()/5)*invert_z);


        meteor.setPosRaw(pos.x, pos.y, pos.z);

        meteor.setSize(world.getRandom().nextIntBetweenInclusive(Math.max(0, min_size), Math.min(50, max_size)));

        meteor.setDeltaMovement(vel_modifier.add(prev_vel));

        if(homing){
            meteor.setDeltaMovement(prev_origin.subtract(meteor.position()).normalize().multiply(meteor.getDeltaMovement().x(), meteor.getDeltaMovement().y()*-1, meteor.getDeltaMovement().z()));
        }

        return meteor;
    }

    /**Spawns a meteor around a random alive online player
     *
     * @param world The world in which the meteors are gonna be spawned in
     * @param p The player around which the meteor will spawn
     * @param silenced Weather or not the meteor should be announced in chat*/
    public static void spawnMeteor(ServerLevel world, Player p, boolean silenced){

        if(p == null){
            //for some reason it won't detect that there is player online sometimes
            return;
        }
        MeteorProjectileEntity meteor = getDownwardsMeteor(p.position(), world.getLevel(),
                CONFIG.meteorSpawning.min_meteor_spawn_distance, CONFIG.meteorSpawning.max_meteor_spawn_distance,
                CONFIG.meteorSpawning.meteor_spawn_height, CONFIG.meteorSpawning.natural_meteor_min_size,
                CONFIG.meteorSpawning.natural_meteor_max_size, CONFIG.meteorBehaviourSection.homing_meteors
        );

        meteor.setSilenced(silenced);

        String message;

        if(CONFIG.meteorSpawning.spawn_huge_meteors){
            if(world.getRandom().nextIntBetweenInclusive(0, CONFIG.meteorSpawning.huge_meteor_chance) == 0){
                meteor = getDownwardsMeteor(p.position(), world.getLevel(),
                        CONFIG.meteorSpawning.min_meteor_spawn_distance, CONFIG.meteorSpawning.max_meteor_spawn_distance,
                        CONFIG.meteorSpawning.meteor_spawn_height, CONFIG.meteorSpawning.max_big_meteor_size,
                        CONFIG.meteorSpawning.huge_meteor_size_limit, CONFIG.meteorBehaviourSection.homing_meteors
                );

                message = "message.ohmymeteors.meteor_spawned.huge";
            } else {
                //world mess is because it needs a final variable btw
                message = "message.ohmymeteors.meteor_spawned";
            }
        } else {
            message = "message.ohmymeteors.meteor_spawned";
        }

        if(CONFIG.notificationSection.announce_meteor_spawn && !meteor.isSilenced()){
            if(CONFIG.notificationSection.announce_location){
                String meteorPos = meteor.blockPosition().getX() + " x, " + meteor.blockPosition().getZ() + " z!";
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message+".localized", meteorPos).withStyle(ChatFormatting.RED)),
                        CONFIG.notificationSection.actionbar_announcements));
            }else{
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message).withStyle(ChatFormatting.RED)),
                        CONFIG.notificationSection.actionbar_announcements));
            }
        }

        world.addFreshEntity(meteor);
    }

    /**Spawns a meteor shower where all meteors spawn at the same time in random directions around the point of origin
     * Also check out {@link #spawnMeteorShowerDelayed(ServerLevel, Player)} and {@link #spawnMeteorShowerDelayedDirection(ServerLevel, Player)}*/
    public static void spawnMeteorShowerInstant(ServerLevel world, Player p){
        int r;
        if(CONFIG.meteorShowerSection.max_meteors_in_shower < CONFIG.meteorShowerSection.min_meteors_in_shower){
            r = world.getRandom().nextIntBetweenInclusive(Math.min(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower), Math.max(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower));
            OhMyMeteors.LOGGER.warn("The Minimum number of meteors in the meteor shower in the config file is lower than the Maximum!");
        }else{
            r = world.getRandom().nextIntBetweenInclusive(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower);
        }
        for(int i = 0; i < r; i++){
            spawnMeteor(world, p, true);
        }
        String message = "message.ohmymeteors.meteor_shower_spawned";
        if(CONFIG.notificationSection.announce_meteor_spawn){
            if(CONFIG.notificationSection.announce_location){
                String pos = p.blockPosition().getX() + " x, " + p.blockPosition().getZ() + " z!";
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message+".localized", pos).withStyle(ChatFormatting.RED)), CONFIG.notificationSection.actionbar_announcements));
            }else{
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message).withStyle(ChatFormatting.RED)), CONFIG.notificationSection.actionbar_announcements));
            }
        }
    }

    /** Spawns a meteor shower with meteors going in random directions but with a slight delay between them
     * unlike {@link #spawnMeteorShowerInstant(ServerLevel, Player)} where all meteors spawn at the same time.
     * Using {@link #spawnMeteorShowerDelayedDirection(ServerLevel, Player)} will also have them follow the same general direction*/
    public static void spawnMeteorShowerDelayed(ServerLevel world, Player p){
        //int total = world.getRandom().nextIntBetweenInclusive(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower);
        int total;
        if(CONFIG.meteorShowerSection.max_meteors_in_shower < CONFIG.meteorShowerSection.min_meteors_in_shower){
            total = world.getRandom().nextIntBetweenInclusive(Math.min(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower), Math.max(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower));
            OhMyMeteors.LOGGER.warn("The Minimum number of meteors in the meteor shower in the config file is lower than the Maximum!");
        }else{
            total = world.getRandom().nextIntBetweenInclusive(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower);
        }
        AtomicInteger spawned_meteors = new AtomicInteger();
        AtomicInteger random_spawn_delay = new AtomicInteger(world.getRandom().nextIntBetweenInclusive(-10, +10));

        spawnMeteor(world, p, true);
        spawned_meteors.getAndIncrement();
        AtomicInteger last_delay = new AtomicInteger();

        SchedulerUtils.runEveryTick((server, ticks) -> {
            if(spawned_meteors.get() >= total){
                return false;
            }
            if(ticks == Math.abs(CONFIG.meteorShowerSection.meteor_shower_delay_ticks)+random_spawn_delay.get()+last_delay.get()){
                if(spawned_meteors.get() >= total){
                    return false;
                }
                spawnMeteor(world, p, true);
                spawned_meteors.getAndIncrement();
                last_delay.set(last_delay.get() + Math.abs(CONFIG.meteorShowerSection.meteor_shower_delay_ticks) + random_spawn_delay.get());
                random_spawn_delay.set(world.getRandom().nextIntBetweenInclusive(-10, +10));
            }
            return true;
        });

        String message = "message.ohmymeteors.meteor_shower_spawned";
        if(CONFIG.notificationSection.announce_meteor_spawn){
            if(CONFIG.notificationSection.announce_location){
                String pos = p.blockPosition().getX() + " x, " + p.blockPosition().getZ() + " z!";
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message+".localized", pos).withStyle(ChatFormatting.RED)), CONFIG.notificationSection.actionbar_announcements));
            }else{
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message).withStyle(ChatFormatting.RED)), CONFIG.notificationSection.actionbar_announcements));
            }
        }
    }

    /**Spawns meteor showers that generally go in the same direction each delayed by a bit*/
    public static void spawnMeteorShowerDelayedDirection(ServerLevel world, Player p){
        int total;
        if(CONFIG.meteorShowerSection.max_meteors_in_shower < CONFIG.meteorShowerSection.min_meteors_in_shower){
            total = world.getRandom().nextIntBetweenInclusive(Math.min(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower), Math.max(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower));
            OhMyMeteors.LOGGER.warn("The Minimum number of meteors in the meteor shower in the config file is lower than the Maximum!");
        }else{
            total = world.getRandom().nextIntBetweenInclusive(CONFIG.meteorShowerSection.min_meteors_in_shower, CONFIG.meteorShowerSection.max_meteors_in_shower);
        }
        //AtomicInteger ticks = new AtomicInteger();
        AtomicInteger last_delay = new AtomicInteger();
        AtomicInteger spawned_meteors = new AtomicInteger();
        AtomicInteger random_spawn_delay = new AtomicInteger(world.getRandom().nextIntBetweenInclusive(-10, +10));

        Tuple<Vec3, Vec3> prev = getDownwardsMeteorPosAndVelocity(p.position(), world.getLevel(),
                CONFIG.meteorSpawning.min_meteor_spawn_distance, CONFIG.meteorSpawning.max_meteor_spawn_distance, CONFIG.meteorSpawning.meteor_spawn_height);

        AtomicInteger limit_a = new AtomicInteger(world.getRandom().nextIntBetweenInclusive(CONFIG.meteorSpawning.min_meteor_spawn_distance, CONFIG.meteorSpawning.max_meteor_spawn_distance));
        AtomicInteger limit_b = new AtomicInteger(world.getRandom().nextIntBetweenInclusive(CONFIG.meteorSpawning.min_meteor_spawn_distance, CONFIG.meteorSpawning.max_meteor_spawn_distance));

        world.addFreshEntity(getDownwardsMeteorSameDirection(prev.getA(), prev.getB(), world,
                Math.min(limit_b.get(), limit_a.get()), Math.max(limit_b.get(), limit_a.get()), CONFIG.meteorSpawning.meteor_spawn_height, CONFIG.meteorSpawning.natural_meteor_min_size, CONFIG.meteorSpawning.natural_meteor_max_size, CONFIG.meteorBehaviourSection.homing_meteors));
        spawned_meteors.getAndIncrement();

        SchedulerUtils.runEveryTick((server, ticks) -> {
            if(spawned_meteors.get() >= total){
                return false;
            }
            if(ticks == Math.abs(CONFIG.meteorShowerSection.meteor_shower_delay_ticks)+random_spawn_delay.get()+ last_delay.get()){
                if(spawned_meteors.get() >= total){
                    return false;
                }
                limit_a.set(world.getRandom().nextIntBetweenInclusive(CONFIG.meteorSpawning.min_meteor_spawn_distance, CONFIG.meteorSpawning.max_meteor_spawn_distance));
                limit_b.set(world.getRandom().nextIntBetweenInclusive(CONFIG.meteorSpawning.min_meteor_spawn_distance, CONFIG.meteorSpawning.max_meteor_spawn_distance));

                MeteorProjectileEntity meteor = getDownwardsMeteorSameDirection(prev.getA(), prev.getB(), world,
                        Math.min(limit_b.get(), limit_a.get()), Math.max(limit_b.get(), limit_a.get()), CONFIG.meteorSpawning.meteor_spawn_height, CONFIG.meteorSpawning.natural_meteor_min_size, CONFIG.meteorSpawning.natural_meteor_max_size, CONFIG.meteorBehaviourSection.homing_meteors);
                meteor.setSilenced(true);
                world.addFreshEntity(meteor);
                spawned_meteors.getAndIncrement();
                last_delay.set(last_delay.get() + Math.abs(CONFIG.meteorShowerSection.meteor_shower_delay_ticks) + random_spawn_delay.get());
                random_spawn_delay.set(world.getRandom().nextIntBetweenInclusive(-10, +10));
            }
            return true;
        });
        String message = "message.ohmymeteors.meteor_shower_spawned";
        if(CONFIG.notificationSection.announce_meteor_spawn){
            if(CONFIG.notificationSection.announce_location){
                String pos = p.blockPosition().getX() + " x, " + p.blockPosition().getZ() + " z!";
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message+".localized", pos).withStyle(ChatFormatting.RED)), CONFIG.notificationSection.actionbar_announcements));
            }else{
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message).withStyle(ChatFormatting.RED)), CONFIG.notificationSection.actionbar_announcements));
            }
        }
    }

    /**Checks if the meteor can spawn in the given dimension
     *
     * @param current_dim The dimension on which to perform the check
     * @return true if the meteor can spawn in there, false otherwise
     * */
    public static boolean canSpawnInDimension(Holder<DimensionType> current_dim){
        //Checks all the dimensions specified in the config file. As soon as it finds one, sets dimension ok to true
        //and then stops checking
        AtomicBoolean dimension_ok = new AtomicBoolean(false);
        CONFIG.meteorSpawning.spawn_dimensions.forEach(dim -> {
                    if(dimension_ok.get()){{
                        return;
                    }}
                    if(dim.equals(current_dim.unwrapKey().get().location().toString())){
                        dimension_ok.set(true);
                    }
                }
        );
        if(!CONFIG.meteorSpawning.dimension_list_mode){
            return !dimension_ok.get();
        }
        return dimension_ok.get();
    }

    /**Checks if the meteor can spawn in the given biome
     *
     * @param current_biome The biome on which to perform the check
     * @return true if the meteor can spawn in there, false otherwise
     * */
    public static boolean canSpawnInBiome(Holder<Biome> current_biome){
        //Checks all the dimensions specified in the config file. As soon as it finds one, sets dimension ok to true
        //and then stops checking

        //If true means whitelist aka it HAS to be present
        //if false means in MUST NOT be present
        if(CONFIG.meteorSpawning.biome_list_mode){
            return CONFIG.meteorSpawning.biome_spawn_list.contains(current_biome.unwrapKey().get().location().toString());
        }else{
            return !CONFIG.meteorSpawning.biome_spawn_list.contains(current_biome.unwrapKey().get().location().toString());
        }
    }


    public static boolean canSpawnInModdedRegion(ServerLevel level, BlockPos pos){
        return canSpawnInModdedRegion(null, level, pos);
    }

    public static boolean canSpawnInModdedRegion(ServerPlayer player, BlockPos pos){
        return canSpawnInModdedRegion(player, (ServerLevel) player.level(), pos);
    }

    /**Checks if the meteor can spawn in the given modded region
     *
     * @param p The player at whose position the meteor is going to spawn
     * @param level The world/level in which the meteor is going to spawn or has spawned
     * @param pos The position to check
     * @return true if the meteor can spawn in there, false otherwise
     * */
    public static boolean canSpawnInModdedRegion(ServerPlayer p, ServerLevel level, BlockPos pos){
        if(ModList.get().isLoaded("flan")){
            if(!FlanCompat.canSpawnHere(p, pos)){
                if(CONFIG.notificationSection.verbose) {
                    OhMyMeteors.LOGGER.warn("A meteor has entered or spawned in a region protected by a Flan claim, it has been discarded!");
                }
                return false;
            }
        }

        if(ModList.get().isLoaded("yawp")){
            //Checks the player pos and the place where the meteor would spawn
            if(!((YawpCompat.canSpawnHere(level, pos)) || YawpCompat.canSpawnHere(level, new BlockPos(pos.getX(), CONFIG.meteorSpawning.meteor_spawn_height, pos.getZ())))) {
                if(CONFIG.notificationSection.verbose){
                    OhMyMeteors.LOGGER.warn("A meteor has entered or spawned in a region protected by a YetAnotherWorldProtector claim, it has been discarded!");
                }
                return false;
            }
        }

        if(ModList.get().isLoaded("openpartiesandclaims")){
            if(!OPACCompat.canSpawnHere(level, pos)){
                if(CONFIG.notificationSection.verbose){
                    OhMyMeteors.LOGGER.warn("A meteor has entered or spawned in a region protected by an OpenPartiesAndClaims claim, it has been discarded!");
                }
                return false;
            }
        }
        return true;
    }

    /** Check if the meteor can spawn in a given location and sends out error messages if verbose is true */
    public static boolean canMeteorSpawn(ServerPlayer p, Holder<DimensionType> current_dim, Holder<Biome> current_biome){
        if(!canSpawnInModdedRegion(p, p.blockPosition())){
            return false;
        }
        if(!canSpawnInDimension(current_dim)){
            return false;
        }
        return canSpawnInBiome(current_biome);
    }

    public static boolean canMeteorSpawn(ServerPlayer p){
        return canMeteorSpawn(p, p.level().dimensionTypeRegistration(), p.level().getBiome(p.blockPosition()));
    }

    public static boolean canMeteorSpawnVerbose(ServerPlayer p, CommandSourceStack source, Holder<DimensionType> current_dim, Holder<Biome> current_biome){
        if(!canSpawnInModdedRegion(p, p.blockPosition())){
            String msg = "The meteor cannot spawn at " + p.blockPosition() + " because of a region flag from Flan or YAWP prevents it in that location! (maybe you added a region in that location?)";
            source.sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.literal("Tried spawning meteor around player '"+p.getDisplayName().getString()+"' but failed.")).append(Component.literal(msg)));
            OhMyMeteors.LOGGER.warn(msg);
            return false;
        }
        if(!canSpawnInDimension(current_dim)){
            String msg = "The meteor cannot spawn in the dimension '" + current_dim.value().toString() + "'. Check your config file for the allowed spawn dimensions!";
            source.sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.literal("Tried spawning meteor around player '"+p.getDisplayName().getString()+"' but failed.")).append(Component.literal(msg)));OhMyMeteors.LOGGER.warn(msg);
            return false;
        }
        if(!canSpawnInBiome(current_biome)){
            String msg = "The meteor cannot spawn in the biome '" + current_biome.value().toString() + "'. Check your config file for the allowed spawn biomes!";
            source.sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.literal("Tried spawning meteor around player '"+p.getDisplayName().getString()+"' but failed.")).append(Component.literal(msg)));
            OhMyMeteors.LOGGER.warn(msg);
            return false;
        }
        return true;
    }

    public static boolean canMeteorSpawnVerbose(ServerPlayer p, CommandSourceStack source){
        return canMeteorSpawnVerbose(p, source, p.level().dimensionTypeRegistration(), p.level().getBiome(p.blockPosition()));
    }
}
