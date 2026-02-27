package me.emafire003.dev.ohmymeteors.util;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import me.emafire003.dev.ohmymeteors.util.scheduler.SchedulerUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static Tuple<Vec3, Vec3> getDownwardsMeteorPosAndVelocity(Vec3 originPos, ServerLevel world, int min_spawn_d, int max_spawn_d, double spawn_height){
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
        Vec3 vel = new Vec3((world.getRandom().nextFloat()/2)*invert_x, -1.0f*(world.getRandom().nextFloat()+ Config.DOWNWARDS_SPEED_MODIFIER), (world.getRandom().nextFloat()/2)*invert_z);

        return new Tuple<>(pos, vel);

    }

    /**
     * Gets a meteor object to be spawned in, with a velocity oriented downwards and a spawn position already set up
     * */
    public static MeteorProjectileEntity getDownwardsMeteor(Vec3 originPos, ServerLevel world, int min_spawn_d, int max_spawn_d, double spawn_height, int min_size, int max_size, boolean homing){
        MeteorProjectileEntity meteor = new MeteorProjectileEntity(world);

        Tuple<Vec3, Vec3> pos_vel = getDownwardsMeteorPosAndVelocity(originPos, world, min_spawn_d, max_spawn_d, spawn_height);

        meteor.setPosRaw(pos_vel.getA().x, pos_vel.getA().y, pos_vel.getA().z);

        meteor.setSize(world.getRandom().nextIntBetweenInclusive(Math.max(0, min_size), Math.min(50, max_size)));

        meteor.setDeltaMovement(pos_vel.getB());

        if(homing){
            //TODO maybe just go with 1,1,1 as velocity multiplier
            meteor.setDeltaMovement(originPos.subtract(meteor.position()).normalize().multiply(meteor.getDeltaMovement().x(), meteor.getDeltaMovement().y()*-1, meteor.getDeltaMovement().z()));
        }

        return meteor;
    }

    /**Used when spawning an orderd meteor shower where most meteors share the same general direction as the previous one
     * <p>
     * Returns a new meteor object with a general direction similar to the specified one, but slightly different origin and velocity
     * The spawn distances are for the new spread, so keep the generally low*/
    public static MeteorProjectileEntity getDownwardsMeteorSameDirection(Vec3 prev_origin, Vec3 prev_vel, ServerLevel world, int min_spawn_d, int max_spawn_d, double spawn_height, int min_size, int max_size, boolean homing){
        MeteorProjectileEntity meteor = new MeteorProjectileEntity(world);

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
                Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE, Config.METEOR_SPAWN_HEIGHT, Config.NATURAL_METEOR_MIN_SIZE, Config.NATURAL_METEOR_MAX_SIZE, Config.HOMING_METEORS);

        meteor.setSilenced(silenced);

        String message;

        if(Config.SPAWN_HUGE_METEORS){
            if(world.getRandom().nextIntBetweenInclusive(0, Config.HUGE_METEOR_CHANCE) == 0){
                meteor = getDownwardsMeteor(p.position(), world.getLevel(),
                        Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE, Config.METEOR_SPAWN_HEIGHT, Config.MAX_BIG_METEOR_SIZE, Config.HUGE_METEOR_SIZE_LIMIT, Config.HOMING_METEORS);

                message = "message.ohmymeteors.meteor_spawned.huge";
            } else {
                //world mess is because it needs a final variable btw
                message = "message.ohmymeteors.meteor_spawned";
            }
        } else {
            message = "message.ohmymeteors.meteor_spawned";
        }

        if(Config.ANNOUNCE_METEOR_SPAWN && !meteor.isSilenced()){
            if(Config.ANNOUNCE_LOCATION){
                String meteorPos = meteor.blockPosition().getX() + " x, " + meteor.blockPosition().getZ() + " z!";
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message+".localized", meteorPos).withStyle(ChatFormatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }else{
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message).withStyle(ChatFormatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }
        }

        world.addFreshEntity(meteor);
    }

    /**Spawns a meteor shower where all meteors spawn at the same time in random directions around the point of origin
     * Also check out {@link #spawnMeteorShowerDelayed(ServerLevel, Player)} and {@link #spawnMeteorShowerDelayedDirection(ServerLevel, Player)}*/
    public static void spawnMeteorShowerInstant(ServerLevel world, Player p){
        int r;
        if(Config.MAX_METEORS_IN_SHOWER < Config.MIN_METEORS_IN_SHOWER){
            r = world.getRandom().nextIntBetweenInclusive(Math.min(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER), Math.max(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER));
            OhMyMeteors.LOGGER.warn("The Minimum number of meteors in the meteor shower in the config file is lower than the Maximum!");
        }else{
            r = world.getRandom().nextIntBetweenInclusive(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER);
        }
        for(int i = 0; i < r; i++){
            spawnMeteor(world, p, true);
        }
        String message = "message.ohmymeteors.meteor_shower_spawned";
        if(Config.ANNOUNCE_METEOR_SPAWN){
            if(Config.ANNOUNCE_LOCATION){
                String pos = p.blockPosition().getX() + " x, " + p.blockPosition().getZ() + " z!";
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message+".localized", pos).withStyle(ChatFormatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }else{
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message).withStyle(ChatFormatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }
        }
    }

    /** Spawns a meteor shower with meteors going in random directions but with a slight delay between them
     * unlike {@link #spawnMeteorShowerInstant(ServerLevel, Player)} where all meteors spawn at the same time.
     * Using {@link #spawnMeteorShowerDelayedDirection(ServerLevel, Player)} will also have them follow the same general direction*/
    public static void spawnMeteorShowerDelayed(ServerLevel world, Player p){
        //int total = world.getRandom().nextIntBetweenInclusive(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER);
        int total;
        if(Config.MAX_METEORS_IN_SHOWER < Config.MIN_METEORS_IN_SHOWER){
            total = world.getRandom().nextIntBetweenInclusive(Math.min(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER), Math.max(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER));
            OhMyMeteors.LOGGER.warn("The Minimum number of meteors in the meteor shower in the config file is lower than the Maximum!");
        }else{
            total = world.getRandom().nextIntBetweenInclusive(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER);
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
            if(ticks == Math.abs(Config.METEOR_SHOWER_DELAY_TICKS)+random_spawn_delay.get()+last_delay.get()){
                if(spawned_meteors.get() >= total){
                    return false;
                }
                spawnMeteor(world, p, true);
                spawned_meteors.getAndIncrement();
                last_delay.set(last_delay.get() + Math.abs(Config.METEOR_SHOWER_DELAY_TICKS) + random_spawn_delay.get());
                random_spawn_delay.set(world.getRandom().nextIntBetweenInclusive(-10, +10));
            }
            return true;
        });

        String message = "message.ohmymeteors.meteor_shower_spawned";
        if(Config.ANNOUNCE_METEOR_SPAWN){
            if(Config.ANNOUNCE_LOCATION){
                String pos = p.blockPosition().getX() + " x, " + p.blockPosition().getZ() + " z!";
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message+".localized", pos).withStyle(ChatFormatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }else{
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message).withStyle(ChatFormatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }
        }
    }

    /**Spawns meteor showers that generally go in the same direction each delayed by a bit*/
    public static void spawnMeteorShowerDelayedDirection(ServerLevel world, Player p){
        int total;
        if(Config.MAX_METEORS_IN_SHOWER < Config.MIN_METEORS_IN_SHOWER){
            total = world.getRandom().nextIntBetweenInclusive(Math.min(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER), Math.max(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER));
            OhMyMeteors.LOGGER.warn("The Minimum number of meteors in the meteor shower in the config file is lower than the Maximum!");
        }else{
            total = world.getRandom().nextIntBetweenInclusive(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER);
        }
        //AtomicInteger ticks = new AtomicInteger();
        AtomicInteger last_delay = new AtomicInteger();
        AtomicInteger spawned_meteors = new AtomicInteger();
        AtomicInteger random_spawn_delay = new AtomicInteger(world.getRandom().nextIntBetweenInclusive(-10, +10));

        Tuple<Vec3, Vec3> prev = getDownwardsMeteorPosAndVelocity(p.position(), world.getLevel(),
                Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE, Config.METEOR_SPAWN_HEIGHT);

        AtomicInteger limit_a = new AtomicInteger(world.getRandom().nextIntBetweenInclusive(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE));
        AtomicInteger limit_b = new AtomicInteger(world.getRandom().nextIntBetweenInclusive(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE));

        world.addFreshEntity(getDownwardsMeteorSameDirection(prev.getA(), prev.getB(), world,
                Math.min(limit_b.get(), limit_a.get()), Math.max(limit_b.get(), limit_a.get()), Config.METEOR_SPAWN_HEIGHT, Config.NATURAL_METEOR_MIN_SIZE, Config.NATURAL_METEOR_MAX_SIZE, Config.HOMING_METEORS));
        spawned_meteors.getAndIncrement();

        SchedulerUtils.runEveryTick((server, ticks) -> {
            if(spawned_meteors.get() >= total){
                return false;
            }
            if(ticks == Math.abs(Config.METEOR_SHOWER_DELAY_TICKS)+random_spawn_delay.get()+ last_delay.get()){
                if(spawned_meteors.get() >= total){
                    return false;
                }
                limit_a.set(world.getRandom().nextIntBetweenInclusive(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE));
                limit_b.set(world.getRandom().nextIntBetweenInclusive(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE));

                MeteorProjectileEntity meteor = getDownwardsMeteorSameDirection(prev.getA(), prev.getB(), world,
                        Math.min(limit_b.get(), limit_a.get()), Math.max(limit_b.get(), limit_a.get()), Config.METEOR_SPAWN_HEIGHT, Config.NATURAL_METEOR_MIN_SIZE, Config.NATURAL_METEOR_MAX_SIZE, Config.HOMING_METEORS);
                meteor.setSilenced(true);
                world.addFreshEntity(meteor);
                spawned_meteors.getAndIncrement();
                last_delay.set(last_delay.get() + Math.abs(Config.METEOR_SHOWER_DELAY_TICKS) + random_spawn_delay.get());
                random_spawn_delay.set(world.getRandom().nextIntBetweenInclusive(-10, +10));
            }
            return true;
        });
        String message = "message.ohmymeteors.meteor_shower_spawned";
        if(Config.ANNOUNCE_METEOR_SPAWN){
            if(Config.ANNOUNCE_LOCATION){
                String pos = p.blockPosition().getX() + " x, " + p.blockPosition().getZ() + " z!";
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message+".localized", pos).withStyle(ChatFormatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }else{
                world.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable(message).withStyle(ChatFormatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
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
        Config.SPAWN_DIMENSIONS.forEach(dim -> {
                    if(dimension_ok.get()){{
                        return;
                    }}
                    if(dim.equals(current_dim.unwrapKey().get().location().toString())){
                        dimension_ok.set(true);
                    }
                }
        );

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
        if(Config.BIOME_LIST_MODE){
            return Config.BIOME_SPAWN_LIST.contains(current_biome.unwrapKey().get().location().toString());
        }else{
            return !Config.BIOME_SPAWN_LIST.contains(current_biome.unwrapKey().get().location().toString());
        }
    }

}
