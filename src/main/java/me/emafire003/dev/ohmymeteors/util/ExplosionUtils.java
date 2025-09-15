package me.emafire003.dev.ohmymeteors.util;

import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ExplosionUtils {

    public static SphereExplosion createExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, Explosion.DestructionType destructionType) {
        return createExplosion(world, entity, null, null, x, y, z, power, false, destructionType);
    }

    public static SphereExplosion createExplosion(
            World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType
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
            World world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            Explosion.DestructionType destructionType
    ) {
        SphereExplosion explosion = new SphereExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
        return explosion;
    }


    public static void createExplosion(World world, MeteorProjectileEntity entity, DamageSource explosion, ExplosionBehavior safeExplosion, Vec3d pos, int power, boolean createFire, Explosion.DestructionType destructionType) {
        createExplosion(world, entity, explosion, safeExplosion, pos.x, pos.y, pos.z, power, createFire, destructionType);
    }
}
