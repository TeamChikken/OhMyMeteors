package me.emafire003.dev.ohmymeteors.util;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MeteorUtils {
    /**Used to get a random meteor position and velocity oriented downwards
     *
     * @return a Pair, where the first value is the Position and teh second one the Velocity*/
    private static Pair<Vec3d, Vec3d> getDownwardsMeteorPosAndVelocity(Vec3d originPos, ServerWorld world, int min_spawn_d, int max_spawn_d, double spawn_height){
        //The invert is to also have a chance at having negative coordinates, otherwise they would always be positive
        int invert_x = 1;
        if(world.getRandom().nextBoolean()){
            invert_x = -1;
        }

        int invert_z = 1;
        if(world.getRandom().nextBoolean()){
            invert_z = -1;
        }

        Vec3d pos = new Vec3d(originPos.getX()+world.getRandom().nextBetween(min_spawn_d, max_spawn_d)*invert_x,
                spawn_height,
                originPos.getZ()+world.getRandom().nextBetween(min_spawn_d, max_spawn_d)*invert_z
        );

        invert_x = 1;
        if(world.getRandom().nextBoolean()){
            invert_x = -1;
        }

        invert_z = 1;
        if(world.getRandom().nextBoolean()){
            invert_z = -1;
        }
        Vec3d vel = new Vec3d((world.getRandom().nextFloat()/2)*invert_x, -1.0f*(world.getRandom().nextFloat()+ Config.DOWNWARDS_SPEED_MODIFIER), (world.getRandom().nextFloat()/2)*invert_z);

        return new Pair<>(pos, vel);

    }

    /**
     * Gets a meteor object to be spawned in, with a velocity oriented downwards and a spawn position already set up
     * */
    public static MeteorProjectileEntity getDownwardsMeteor(Vec3d originPos, ServerWorld world, int min_spawn_d, int max_spawn_d, double spawn_height, int min_size, int max_size, boolean homing){
        MeteorProjectileEntity meteor = new MeteorProjectileEntity(world);

        Pair<Vec3d, Vec3d> pos_vel = getDownwardsMeteorPosAndVelocity(originPos, world, min_spawn_d, max_spawn_d, spawn_height);

        meteor.setPos(pos_vel.getLeft().x, pos_vel.getLeft().y, pos_vel.getLeft().z);

        meteor.setSize(world.getRandom().nextBetween(Math.max(0, min_size), Math.min(50, max_size)));

        meteor.setVelocity(pos_vel.getRight());

        if(homing){
            //TODO maybe just go with 1,1,1 as velocity multiplier
            meteor.setVelocity(originPos.subtract(meteor.getPos()).normalize().multiply(meteor.getVelocity().getX(), meteor.getVelocity().getY()*-1, meteor.getVelocity().getZ()));
        }

        return meteor;
    }

    /**Used when spawning an orderd meteor shower where most meteors share the same general direction as the previous one
     * <p>
     * Returns a new meteor object with a general direction similar to the specified one, but slightly different origin and velocity
     * The spawn distances are for the new spread, so keep the generally low*/
    public static MeteorProjectileEntity getDownwardsMeteorSameDirection(Vec3d prev_origin, Vec3d prev_vel, ServerWorld world, int min_spawn_d, int max_spawn_d, double spawn_height, int min_size, int max_size, boolean homing){
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

        Vec3d pos = new Vec3d(prev_origin.getX()+world.getRandom().nextBetween(min_spawn_d, max_spawn_d)*invert_x,
                spawn_height,
                prev_origin.getZ()+world.getRandom().nextBetween(min_spawn_d, max_spawn_d)*invert_z
        );

        invert_x = 1;
        if(world.getRandom().nextBoolean()){
            invert_x = -1;
        }

        invert_z = 1;
        if(world.getRandom().nextBoolean()){
            invert_z = -1;
        }
        Vec3d vel_modifier = new Vec3d((world.getRandom().nextFloat()/5)*invert_x, -1.0f*(world.getRandom().nextFloat()/5), (world.getRandom().nextFloat()/5)*invert_z);


        meteor.setPos(pos.x, pos.y, pos.z);

        meteor.setSize(world.getRandom().nextBetween(Math.max(0, min_size), Math.min(50, max_size)));

        meteor.setVelocity(vel_modifier.add(prev_vel));

        if(homing){
            meteor.setVelocity(prev_origin.subtract(meteor.getPos()).normalize().multiply(meteor.getVelocity().getX(), meteor.getVelocity().getY()*-1, meteor.getVelocity().getZ()));
        }

        return meteor;
    }

    /**Spawns a meteor around a random alive online player
     *
     * @param world The world in which the meteors are gonna be spawned in
     * @param p The player around which the meteor will spawn
     * @param silenced Weather or not the meteor should be announced in chat*/
    public static void spawnMeteor(ServerWorld world, PlayerEntity p, boolean silenced){

        if(p == null){
            //for some reason it won't detect that there is player online sometimes
            return;
        }
        MeteorProjectileEntity meteor = getDownwardsMeteor(p.getPos(), world.toServerWorld(),
                Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE, Config.METEOR_SPAWN_HEIGHT, Config.NATURAL_METEOR_MIN_SIZE, Config.NATURAL_METEOR_MAX_SIZE, Config.HOMING_METEORS);

        meteor.setSilenced(silenced);

        String message;

        if(Config.SPAWN_HUGE_METEORS){
            if(world.getRandom().nextBetween(0, Config.HUGE_METEOR_CHANCE) == 0){
                meteor = getDownwardsMeteor(p.getPos(), world.toServerWorld(),
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
                String meteorPos = meteor.getBlockPos().getX() + " x, " + meteor.getBlockPos().getZ() + " z!";
                world.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message+".localized", meteorPos).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }else{
                world.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }
        }

        world.spawnEntity(meteor);
    }

    /**Spawns a meteor shower where all meteors spawn at the same time in random directions around the point of origin
     * Also check out {@link #spawnMeteorShowerDelayed(ServerWorld, PlayerEntity)} and {@link #spawnMeteorShowerDelayedDirection(ServerWorld, PlayerEntity)}*/
    public static void spawnMeteorShowerInstant(ServerWorld world, PlayerEntity p){
        int r = world.getRandom().nextBetween(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER);
        for(int i = 0; i < r; i++){
            spawnMeteor(world, p, true);
        }
        String message = "message.ohmymeteors.meteor_shower_spawned";
        if(Config.ANNOUNCE_METEOR_SPAWN){
            if(Config.ANNOUNCE_LOCATION){
                String pos = p.getBlockPos().getX() + " x, " + p.getBlockPos().getZ() + " z!";
                world.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message+".localized", pos).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }else{
                world.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }
        }
    }

    /** Spawns a meteor shower with meteors going in random directions but with a slight delay between them
     * unlike {@link #spawnMeteorShowerInstant(ServerWorld, PlayerEntity)} where all meteors spawn at the same time.
     * Using {@link #spawnMeteorShowerDelayedDirection(ServerWorld, PlayerEntity)} will also have them follow the same general direction*/
    public static void spawnMeteorShowerDelayed(ServerWorld world, PlayerEntity p){
        int total = world.getRandom().nextBetween(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER);
        AtomicInteger ticker = new AtomicInteger();
        AtomicInteger spawned_meteors = new AtomicInteger();
        int base_spawn_delay = 15;
        AtomicInteger random_spawn_delay = new AtomicInteger(world.getRandom().nextBetween(-10, +10));

        spawnMeteor(world, p, true);
        spawned_meteors.getAndIncrement();

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if(ticker.get() == -1 || spawned_meteors.get() >= total){
                return;
            }
            if(ticker.get() == base_spawn_delay+ random_spawn_delay.get()){
                if(spawned_meteors.get() >= total){
                    ticker.set(-1);
                    return;
                }
                spawnMeteor(world, p, true);
                spawned_meteors.getAndIncrement();
                ticker.set(0);
                random_spawn_delay.set(world.getRandom().nextBetween(-10, +10));
            }
            ticker.getAndIncrement();
        });

        String message = "message.ohmymeteors.meteor_shower_spawned";
        if(Config.ANNOUNCE_METEOR_SPAWN){
            if(Config.ANNOUNCE_LOCATION){
                String pos = p.getBlockPos().getX() + " x, " + p.getBlockPos().getZ() + " z!";
                world.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message+".localized", pos).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }else{
                world.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }
        }
    }

    /**Spawns meteor showers that generally go in the same direction each delayed by a bit*/
    public static void spawnMeteorShowerDelayedDirection(ServerWorld world, PlayerEntity p){
        int total = world.getRandom().nextBetween(Config.MIN_METEORS_IN_SHOWER, Config.MAX_METEORS_IN_SHOWER);
        AtomicInteger ticker = new AtomicInteger();
        AtomicInteger spawned_meteors = new AtomicInteger();
        int base_spawn_delay = 15;
        AtomicInteger random_spawn_delay = new AtomicInteger(world.getRandom().nextBetween(-10, +10));

        Pair<Vec3d, Vec3d> prev = getDownwardsMeteorPosAndVelocity(p.getPos(), world.toServerWorld(),
                Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE, Config.METEOR_SPAWN_HEIGHT);

        AtomicInteger limit_a = new AtomicInteger(world.getRandom().nextBetween(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE));
        AtomicInteger limit_b = new AtomicInteger(world.getRandom().nextBetween(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE));

        world.spawnEntity(getDownwardsMeteorSameDirection(prev.getLeft(), prev.getRight(), world,
                Math.min(limit_b.get(), limit_a.get()), Math.max(limit_b.get(), limit_a.get()), Config.METEOR_SPAWN_HEIGHT, Config.NATURAL_METEOR_MIN_SIZE, Config.NATURAL_METEOR_MAX_SIZE, Config.HOMING_METEORS));
        spawned_meteors.getAndIncrement();

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if(ticker.get() == -1 || spawned_meteors.get() >= total){
                return;
            }
            if(ticker.get() == base_spawn_delay+ random_spawn_delay.get()){
                if(spawned_meteors.get() >= total){
                    ticker.set(-1);
                    return;
                }
                limit_a.set(world.getRandom().nextBetween(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE));
                limit_b.set(world.getRandom().nextBetween(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE));

                world.spawnEntity(getDownwardsMeteorSameDirection(prev.getLeft(), prev.getRight(), world,
                        Math.min(limit_b.get(), limit_a.get()), Math.max(limit_b.get(), limit_a.get()), Config.METEOR_SPAWN_HEIGHT, Config.NATURAL_METEOR_MIN_SIZE, Config.NATURAL_METEOR_MAX_SIZE, Config.HOMING_METEORS));
                spawned_meteors.getAndIncrement();
                ticker.set(0);
                random_spawn_delay.set(world.getRandom().nextBetween(-10, +10));
            }
            ticker.getAndIncrement();
        });

        String message = "message.ohmymeteors.meteor_shower_spawned";
        if(Config.ANNOUNCE_METEOR_SPAWN){
            if(Config.ANNOUNCE_LOCATION){
                String pos = p.getBlockPos().getX() + " x, " + p.getBlockPos().getZ() + " z!";
                world.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message+".localized", pos).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }else{
                world.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }
        }
    }

    /**Checks if the meteor can spawn in the given dimension
     *
     * @param current_dim The dimension on which to perform the check
     * @return true if the meteor can spawn in there, false otherwise
     * */
    public static boolean canSpawnInDimension(RegistryEntry<DimensionType> current_dim){
        //Checks all the dimensions specified in the config file. As soon as it finds one, sets dimension ok to true
        //and then stops checking
        AtomicBoolean dimension_ok = new AtomicBoolean(false);
        Config.SPAWN_DIMENSIONS.forEach(dim -> {
                    if(dimension_ok.get()){{
                        return;
                    }}
                    if(dim.equals(current_dim.getIdAsString())){
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
    public static boolean canSpawnInBiome(RegistryEntry<Biome> current_biome){
        //Checks all the dimensions specified in the config file. As soon as it finds one, sets dimension ok to true
        //and then stops checking

        //If true means whitelist aka it HAS to be present
        //if false means in MUST NOT be present
        if(Config.BIOME_LIST_MODE){
            return Config.BIOME_SPAWN_LIST.contains(current_biome.getIdAsString());
        }else{
            return !Config.BIOME_SPAWN_LIST.contains(current_biome.getIdAsString());
        }
    }
}
