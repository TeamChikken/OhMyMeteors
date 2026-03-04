package me.emafire003.dev.ohmymeteors.util;

import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ExplosionUtils {

    public static SphereExplosion createExplosion(Level world, @Nullable Entity entity, double x, double y, double z, float power, Explosion.BlockInteraction destructionType) {
        return createExplosion(world, entity, null, null, x, y, z, power, false, destructionType);
    }

    public static SphereExplosion createExplosion(
            Level world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.BlockInteraction destructionType
    ) {
        return createExplosion(world, entity, null, null, x, y, z, power, createFire, destructionType);
    }

    /**
     * Creates an explosion.
     *
     * @param behavior the explosion behavior, or {@code null} to use the default
     * @param damageSource the custom damage source, or {@code null} to use the default
     * ({@link DamageSource#explosion(Explosion)})
     * @param entity the entity that exploded (like TNT) or {@code null} to indicate no entity exploded
     * @param destructionType the destruction type of the explosion
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
            Explosion.BlockInteraction destructionType
    ) {
        SphereExplosion explosion = new SphereExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.explode();
        explosion.finalizeExplosion(true);
        return explosion;
    }


    public static void createExplosion(Level world, MeteorProjectileEntity entity, DamageSource explosion, ExplosionDamageCalculator safeExplosion, Vec3 pos, int power, boolean createFire, Explosion.BlockInteraction destructionType) {
        createExplosion(world, entity, explosion, safeExplosion, pos.x, pos.y, pos.z, power, createFire, destructionType);
    }
}
