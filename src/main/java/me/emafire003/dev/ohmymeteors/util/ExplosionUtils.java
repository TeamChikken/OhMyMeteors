package me.emafire003.dev.ohmymeteors.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


@SuppressWarnings("unused")
public class ExplosionUtils {

    public static final Pool<BlockParticleEffect> EXPLOSION_BLOCK_PARTICLES = Pool.<BlockParticleEffect>builder()
            .add(new BlockParticleEffect(ParticleTypes.POOF, 0.5F, 1.0F))
            .add(new BlockParticleEffect(ParticleTypes.SMOKE, 1.0F, 1.0F))
            .build();

    public static void createExplosion(ServerWorld world, @Nullable Entity entity, double x, double y, double z, float power, World.ExplosionSourceType explosionSourceType) {
        createExplosion(
                world,
                entity,
                Explosion.createDamageSource(world, entity),
                null,
                x,
                y,
                z,
                power,
                false,
                explosionSourceType,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                EXPLOSION_BLOCK_PARTICLES,
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }

    public static void createExplosion(ServerWorld world,
            @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType
    ) {
        createExplosion(
                world,
                entity,
                Explosion.createDamageSource(world, entity),
                null,
                x,
                y,
                z,
                power,
                createFire,
                explosionSourceType,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                EXPLOSION_BLOCK_PARTICLES,
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }

    public static void createExplosion(
            ServerWorld world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            Vec3d pos,
            float power,
            boolean createFire,
            World.ExplosionSourceType explosionSourceType
    ) {
        createExplosion(
                world,
                entity,
                damageSource,
                behavior,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                power,
                createFire,
                explosionSourceType,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                EXPLOSION_BLOCK_PARTICLES,
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }

    public static void createExplosion(
            ServerWorld world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            World.ExplosionSourceType explosionSourceType
    ) {
        createExplosion(
                world,
                entity,
                damageSource,
                behavior,
                x,
                y,
                z,
                power,
                createFire,
                explosionSourceType,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                EXPLOSION_BLOCK_PARTICLES,
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }
    
    public static void createExplosion(
            ServerWorld world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            World.ExplosionSourceType explosionSourceType,
            ParticleEffect smallParticle,
            ParticleEffect largeParticle,
            Pool<BlockParticleEffect> blockParticles,
            RegistryEntry<SoundEvent> soundEvent
    ) {
        Explosion.DestructionType destructionType = switch (explosionSourceType) {
            case NONE -> Explosion.DestructionType.KEEP;
            case BLOCK -> getDestructionType(world, GameRules.BLOCK_EXPLOSION_DROP_DECAY);
            case MOB -> world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                    ? getDestructionType(world, GameRules.MOB_EXPLOSION_DROP_DECAY)
                    : Explosion.DestructionType.KEEP;
            case TNT -> getDestructionType(world, GameRules.TNT_EXPLOSION_DROP_DECAY);
            case TRIGGER -> Explosion.DestructionType.TRIGGER_BLOCK;
        };
        Vec3d vec3d = new Vec3d(x, y, z);
        SphereExplosion explosionImpl = new SphereExplosion(world, entity, damageSource, behavior, vec3d, power, createFire, destructionType);
        int i = explosionImpl.explode();
        ParticleEffect particleEffect = explosionImpl.isSmall() ? smallParticle : largeParticle;

        for (ServerPlayerEntity serverPlayerEntity : world.getPlayers()) {
            if (serverPlayerEntity.squaredDistanceTo(vec3d) < 4096.0) {
                Optional<Vec3d> optional = Optional.ofNullable(explosionImpl.getKnockbackByPlayer().get(serverPlayerEntity));
                serverPlayerEntity.networkHandler.sendPacket(new ExplosionS2CPacket(vec3d, power, i, optional, particleEffect, soundEvent, blockParticles));
            }
        }
    }

    private static Explosion.DestructionType getDestructionType(ServerWorld world, GameRules.Key<GameRules.BooleanRule> decayRule) {
        return world.getGameRules().getBoolean(decayRule) ? Explosion.DestructionType.DESTROY_WITH_DECAY : Explosion.DestructionType.DESTROY;
    }

}
