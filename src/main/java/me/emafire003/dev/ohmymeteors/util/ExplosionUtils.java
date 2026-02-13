package me.emafire003.dev.ohmymeteors.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ExplosionUtils {


    public static SphereExplosion createExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, World.ExplosionSourceType explosionSourceType) {
        return createExplosion(world, entity, null, null, x, y, z, power, false, explosionSourceType);
    }

    public static SphereExplosion createExplosion(
            World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType
    ) {
        return createExplosion(world, entity, null, null, x, y, z, power, createFire, explosionSourceType);
    }

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
        return createExplosion(world, entity, damageSource, behavior, pos.getX(), pos.getY(), pos.getZ(), power, createFire, explosionSourceType);
    }

    /**
     * Creates an explosion.
     *
     * @param behavior the explosion behavior, or {@code null} to use the default
     * @param damageSource the custom damage source, or {@code null} to use the default
     * ({@link net.minecraft.entity.damage.DamageSources#explosion(Explosion)})
     * @param entity the entity that exploded (like TNT) or {@code null} to indicate no entity exploded
     * @param createFire whether the explosion should create fire
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
            World.ExplosionSourceType explosionSourceType
    ) {
        return createExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, explosionSourceType, true);
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
            boolean particles
    ) {
        SphereExplosion.DestructionType destructionType = switch (explosionSourceType) {
            case NONE -> SphereExplosion.DestructionType.KEEP;
            case BLOCK -> getDestructionType(GameRules.BLOCK_EXPLOSION_DROP_DECAY, world);
            case MOB -> world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                    ? getDestructionType(GameRules.MOB_EXPLOSION_DROP_DECAY, world)
                    : SphereExplosion.DestructionType.KEEP;
            case TNT -> getDestructionType(GameRules.TNT_EXPLOSION_DROP_DECAY, world);
        };
        SphereExplosion explosion = new SphereExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(particles);
        return explosion;
    }

    private static SphereExplosion.DestructionType getDestructionType(GameRules.Key<GameRules.BooleanRule> gameRuleKey, World world) {
        return world.getGameRules().getBoolean(gameRuleKey) ? SphereExplosion.DestructionType.DESTROY_WITH_DECAY : SphereExplosion.DestructionType.DESTROY;
    }

}
