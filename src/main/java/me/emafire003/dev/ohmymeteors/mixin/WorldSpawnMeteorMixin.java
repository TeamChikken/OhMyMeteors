package me.emafire003.dev.ohmymeteors.mixin;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
import java.util.function.BooleanSupplier;

//(there would be a way to like list all of the loaded chunks but it seems a bit impractical when we can just target a random online player)
@Mixin(ServerWorld.class)
public abstract class WorldSpawnMeteorMixin extends World implements StructureWorldAccess {

    protected WorldSpawnMeteorMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
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


        int chance = Config.METEOR_SPAWN_CHANCE;

        if(Config.MODIFY_SPAWN_CHANCE_AT_NIGHT && this.isNight()){
            chance = Config.METEOR_NIGHT_SPAWN_CHANCE;
        }

        if(this.getRandom().nextBetween(0, chance) == 0){
            PlayerEntity p = this.getRandomAlivePlayer();

            if(p == null){
                //for some reason it won't detect that there is player online sometimes
                return;
            }
            MeteorProjectileEntity meteor = MeteorProjectileEntity.getDownwardsMeteor(p.getPos(), this.toServerWorld(),
                    Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE, Config.METEOR_SPAWN_HEIGHT, Config.NATURAL_METEOR_MIN_SIZE, Config.NATURAL_METEOR_MAX_SIZE, Config.HOMING_METEORS);

            String message;

            if(Config.SPAWN_HUGE_METEORS){
                if(this.getRandom().nextBetween(0, Config.HUGE_METEOR_CHANCE) == 0){
                    meteor = MeteorProjectileEntity.getDownwardsMeteor(p.getPos(), this.toServerWorld(),
                            Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE, Config.METEOR_SPAWN_HEIGHT, Config.MAX_BIG_METEOR_SIZE, Config.HUGE_METEOR_SIZE_LIMIT, Config.HOMING_METEORS);

                    message = "message.ohmymeteors.meteor_spawned.huge";
                } else {
                    //this mess is because it needs a final variable btw
                    message = "message.ohmymeteors.meteor_spawned";
                }
            } else {
                message = "message.ohmymeteors.meteor_spawned";
            }

            if(Config.ANNOUNCE_METEOR_SPAWN){
                this.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }


            this.spawnEntity(meteor);

            if(Config.SHOULD_COOLDOWN_BETWEEN_METEORS){
                meteorCooldown = 20*Config.MIN_METEOR_COOLDOWN_TIME;
            }
        }
    }

}
