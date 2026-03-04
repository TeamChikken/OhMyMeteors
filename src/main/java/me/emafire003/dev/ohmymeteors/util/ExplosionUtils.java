package me.emafire003.dev.ohmymeteors.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


@SuppressWarnings("unused")
public class ExplosionUtils {

    public static final WeightedList<ExplosionParticleInfo> EXPLOSION_BLOCK_PARTICLES = WeightedList.<ExplosionParticleInfo>builder()
            .add(new ExplosionParticleInfo(ParticleTypes.POOF, 0.5F, 1.0F))
            .add(new ExplosionParticleInfo(ParticleTypes.SMOKE, 1.0F, 1.0F))
            .build();

    public static void createExplosion(ServerLevel world, @Nullable Entity entity, double x, double y, double z, float power, Level.ExplosionInteraction explosionSourceType) {
        createExplosion(
                world,
                entity,
                Explosion.getDefaultDamageSource(world, entity),
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
                SoundEvents.GENERIC_EXPLODE
        );
    }

    public static void createExplosion(ServerLevel world,
                                       @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Level.ExplosionInteraction explosionSourceType
    ) {
        createExplosion(
                world,
                entity,
                Explosion.getDefaultDamageSource(world, entity),
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
                SoundEvents.GENERIC_EXPLODE
        );
    }

    public static void createExplosion(
            ServerLevel world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator behavior,
            Vec3 pos,
            float power,
            boolean createFire,
            Level.ExplosionInteraction explosionSourceType
    ) {
        createExplosion(
                world,
                entity,
                damageSource,
                behavior,
                pos.x(),
                pos.y(),
                pos.z(),
                power,
                createFire,
                explosionSourceType,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                EXPLOSION_BLOCK_PARTICLES,
                SoundEvents.GENERIC_EXPLODE
        );
    }

    public static void createExplosion(
            ServerLevel world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            Level.ExplosionInteraction explosionSourceType
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
                SoundEvents.GENERIC_EXPLODE
        );
    }

    
    public static void createExplosion(
            ServerLevel world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            Level.ExplosionInteraction explosionSourceType,
            ParticleOptions smallParticle,
            ParticleOptions largeParticle,
            WeightedList<ExplosionParticleInfo> blockParticles,
            Holder<SoundEvent> soundEvent
    ) {

        Explosion.BlockInteraction destructionType;
        switch (explosionSourceType) {
            case NONE -> destructionType = Explosion.BlockInteraction.KEEP;
            case BLOCK -> destructionType = getDestructionType(world, GameRules.BLOCK_EXPLOSION_DROP_DECAY);
            case MOB -> destructionType = world.getGameRules().get(GameRules.MOB_GRIEFING) ? getDestructionType(world, GameRules.MOB_EXPLOSION_DROP_DECAY) : Explosion.BlockInteraction.KEEP;
            case TNT -> destructionType = getDestructionType(world, GameRules.TNT_EXPLOSION_DROP_DECAY);
            case TRIGGER -> destructionType = Explosion.BlockInteraction.TRIGGER_BLOCK;
            default -> throw new MatchException(null, null);
        }

        Vec3 vec3d = new Vec3(x, y, z);
        SphereExplosion explosionImpl = new SphereExplosion(world, entity, damageSource, behavior, vec3d, power, createFire, destructionType);
        int i = explosionImpl.explode();
        ParticleOptions particleEffect = explosionImpl.isSmall() ? smallParticle : largeParticle;

        for(ServerPlayer serverPlayerEntity : world.players()) {
            if (serverPlayerEntity.distanceToSqr(vec3d) < (double)4096.0F) {
                Optional<Vec3> optional = Optional.ofNullable((Vec3)explosionImpl.getKnockbackByPlayer().get(serverPlayerEntity));
                serverPlayerEntity.connection.send(new ClientboundExplodePacket(vec3d, power, i, optional, particleEffect, soundEvent, blockParticles));
            }
        }
    }

    private static Explosion.BlockInteraction getDestructionType(ServerLevel world, GameRule<Boolean> decayRule) {
        return world.getGameRules().get(decayRule) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
    }

}
