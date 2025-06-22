package me.emafire003.dev.ohmymeteors.mixin;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
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

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;


//TODO maybe also add that when a new chunk is loaded or generated there is a chance to spawn a meteor.
//(there would be a way to like list all of the loaded chunks but it seems a bit impractical when we can just target a random online player)
@Mixin(ServerWorld.class)
public abstract class WorldSpawnMeteorMixin extends World implements StructureWorldAccess {

    protected WorldSpawnMeteorMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Shadow @Nullable public abstract ServerPlayerEntity getRandomAlivePlayer();

    @Shadow public abstract boolean spawnEntity(Entity entity);

    @Shadow public abstract ChunkManager getChunkManager();

    @Unique
    int meteorCooldown = 0;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tickSpawnMeteor(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        //TODO remove when finished testing
        if(OhMyMeteors.debugSpawningOff){
            return;
        }



        if(Config.SHOULD_COOLDOWN_BETWEEN_METEORS && meteorCooldown > 0){
            meteorCooldown = meteorCooldown - 1;
            return; //Hey. this return is important. I totally haven't discovered i forgot to put it here because like 200 meteors spawned in the span of a second in my face. Not at all.
        }


        int chance = Config.METEOR_SPAWN_CHANCE;

        if(Config.MODIFY_SPAWN_CHANCE_AT_NIGHT && this.isNight()){
            chance = Config.METEOR_NIGHT_SPAWN_CHANCE;
        }

        if(this.getRandom().nextBetween(0, chance) == 0){
            PlayerEntity p = this.getRandomAlivePlayer();

            if(p == null){
                //for some reason it won't detetct that there is player online sometimes
                //TODO maybe use loaded chunk instead somehow?
                return;
            }

            Vec3d playerPos = p.getPos();
            //TODO add possibility to track directly the player aka send the meteord towards them
            MeteorProjectileEntity meteor = new MeteorProjectileEntity(this);

            //The invert is to also have a chance at having negative coordinates, otherwise they would always be positive
            int invert_x = 1;
            if(this.getRandom().nextBoolean()){
                invert_x = -1;
            }

            int invert_y = 1;
            if(this.getRandom().nextBoolean()){
                invert_y = -1;
            }

            meteor.setPos(playerPos.getX()+this.getRandom().nextBetween(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE)*invert_x,
                    Config.METEOR_SPAWN_HEIGHT,
                    playerPos.getZ()+this.getRandom().nextBetween(Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE)*invert_y
            );

            invert_x = 1;
            if(this.getRandom().nextBoolean()){
                invert_x = -1;
            }

            invert_y = 1;
            if(this.getRandom().nextBoolean()){
                invert_y = -1;
            }

            meteor.setSize(this.getRandom().nextBetween(Math.max(0, Config.NATURAL_METEOR_MIN_SIZE), Math.min(50, Config.NATURAL_METEOR_MAX_SIZE)));

            //TODO this is VERY WIP and only works if the player is rather far down from where the meteor spawns in. Like i might delete this instead
            if(Config.HOMING_METEORS){
                Vec3d vec3d = meteor.getRotationVec(1.0F);
                double f = p.getX() - (meteor.getX() + vec3d.x * 4.0);
                double g = p.getBodyY(0.5) - (0.5 + meteor.getBodyY(0.5));
                double h = p.getZ() - (meteor.getZ() + vec3d.z * 4.0);
                Vec3d vec3d2 = new Vec3d(f, g, h);
                meteor.setVelocity(vec3d2.multiply(1f, 0.01f, 1f));
            }else{
                meteor.setVelocity((this.getRandom().nextFloat()/2)*invert_x, -1.0f+this.getRandom().nextFloat(), (this.getRandom().nextFloat()/2)*invert_y);

            }

            //TODO debug remove
            if (true){
                //p.teleport(meteor.getX(), p.getY(), meteor.getZ(), false);
                p.sendMessage(Text.literal("spawning meteor at: " + meteor.getPos() + " whit size: " + meteor.getSize()));
            }

            this.spawnEntity(meteor);

            if(Config.SHOULD_COOLDOWN_BETWEEN_METEORS){
                meteorCooldown = 20*Config.MIN_METEOR_COOLDOWN_TIME;
            }
        }
    }
}
