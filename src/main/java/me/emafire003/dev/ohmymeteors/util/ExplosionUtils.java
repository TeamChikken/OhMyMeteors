package me.emafire003.dev.ohmymeteors.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ExplosionUtils {


    public static SphereExplosion createExplosion(Level world, @Nullable Entity entity, double x, double y, double z, float power, Level.ExplosionInteraction explosionSourceType) {
        return createExplosion(world, entity, null, null, x, y, z, power, false, explosionSourceType);
    }

    public static SphereExplosion createExplosion(
            Level world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Level.ExplosionInteraction explosionSourceType
    ) {
        return createExplosion(world, entity, null, null, x, y, z, power, createFire, explosionSourceType);
    }

    public static SphereExplosion createExplosion(
            Level world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator behavior,
            Vec3 pos,
            float power,
            boolean createFire,
            Level.ExplosionInteraction explosionSourceType
    ) {
        return createExplosion(world, entity, damageSource, behavior, pos.x(), pos.y(), pos.z(), power, createFire, explosionSourceType);
    }

    /**
     * Creates an explosion.
     *
     * @param behavior the explosion behavior, or {@code null} to use the default
     * @param damageSource the custom damage source, or {@code null} to use the default
     * ({@link net.minecraft.world.damagesource.DamageSources#explosion(Explosion)})
     * @param entity the entity that exploded (like TNT) or {@code null} to indicate no entity exploded
     * @param createFire whether the explosion should create fire
     */
    public static SphereExplosion createExplosion(
            Level world,
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
        return createExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, explosionSourceType, true);
    }

    public static SphereExplosion createExplosion(
            Level world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            Level.ExplosionInteraction explosionSourceType,
            boolean particles
    ) {
        SphereExplosion.BlockInteraction destructionType = switch (explosionSourceType) {
            case NONE -> SphereExplosion.BlockInteraction.KEEP;
            case BLOCK -> getDestructionType(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY, world);
            case MOB -> world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
                    ? getDestructionType(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY, world)
                    : SphereExplosion.BlockInteraction.KEEP;
            case TNT -> getDestructionType(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY, world);
        };
        SphereExplosion explosion = new SphereExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.explode();
        explosion.finalizeExplosion(particles);
        return explosion;
    }

    private static SphereExplosion.BlockInteraction getDestructionType(GameRules.Key<GameRules.BooleanValue> gameRuleKey, Level world) {
        return world.getGameRules().getBoolean(gameRuleKey) ? SphereExplosion.BlockInteraction.DESTROY_WITH_DECAY : SphereExplosion.BlockInteraction.DESTROY;
    }

}