package me.emafire003.dev.ohmymeteors.util;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ExplosionUtils {

    public static SphereExplosion createExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, World.ExplosionSourceType explosionSourceType) {
        return createExplosion(
                world,
                entity,
                SphereExplosion.createDamageSource(world, entity),
                null,
                x,
                y,
                z,
                power,
                false,
                explosionSourceType,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }

    /**
     * Creates an explosion.
     *
     * @see #createExplosion(World, Entity, DamageSource, ExplosionBehavior, double, double, double, float, boolean, World.ExplosionSourceType)
     */
    public static SphereExplosion createExplosion(
            World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType
    ) {
        return createExplosion(
                world,
                entity,
                SphereExplosion.createDamageSource(world, entity),
                null,
                x,
                y,
                z,
                power,
                createFire,
                explosionSourceType,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }

    /**
     * Creates an explosion.
     *
     * @see #createExplosion(World, Entity, DamageSource, ExplosionBehavior, double, double, double, float, boolean, World.ExplosionSourceType)
     */
    public static SphereExplosion createExplosion(
            World world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            Vec3d pos,
            float power,
            boolean createFire,
            World.ExplosionSourceType explosionSourceType
    ) {
        return createExplosion(
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
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }

    public static SphereExplosion createExplosion(
            World world,
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
        return createExplosion(
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
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }

    /**
     * Creates an explosion.
     *
     * @param createFire whether the explosion should create fire
     * @param entity the entity that exploded (like TNT) or {@code null} to indicate no entity exploded
     * @param damageSource the custom damage source, or {@code null} to use the default
     * @param behavior the explosion behavior, or {@code null} to use the default
     */
    public static SphereExplosion createExplosion(
            World world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            World.ExplosionSourceType explosionSourceType,
            ParticleEffect particle,
            ParticleEffect emitterParticle,
            RegistryEntry<SoundEvent> soundEvent
    ) {
        return createExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, explosionSourceType, true, particle, emitterParticle, soundEvent);
    }

    public static SphereExplosion createExplosion(
            World world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            World.ExplosionSourceType explosionSourceType,
            boolean particles,
            ParticleEffect particle,
            ParticleEffect emitterParticle,
            RegistryEntry<SoundEvent> soundEvent
    ) {
        SphereExplosion.DestructionType destructionType = switch (explosionSourceType) {
            case NONE -> SphereExplosion.DestructionType.KEEP;
            case BLOCK -> getDestructionType(GameRules.BLOCK_EXPLOSION_DROP_DECAY, world);
            case MOB -> world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                    ? getDestructionType(GameRules.MOB_EXPLOSION_DROP_DECAY, world)
                    : SphereExplosion.DestructionType.KEEP;
            case TNT -> getDestructionType(GameRules.TNT_EXPLOSION_DROP_DECAY, world);
            case TRIGGER -> SphereExplosion.DestructionType.TRIGGER_BLOCK;
        };
        SphereExplosion explosion = new SphereExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType, particle, emitterParticle, soundEvent);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(particles);
        return explosion;
    }

    private static SphereExplosion.DestructionType getDestructionType(GameRules.Key<GameRules.BooleanRule> gameRuleKey, World world) {
        return world.getGameRules().getBoolean(gameRuleKey) ? SphereExplosion.DestructionType.DESTROY_WITH_DECAY : SphereExplosion.DestructionType.DESTROY;
    }
}
