package me.emafire003.dev.ohmymeteors.entities;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.Config;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

public class OMMEntities {

    public static final EntityType<MeteorProjectileEntity> METEOR_PROJECTILE_ENTITY = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            OhMyMeteors.getIdentifier("meteor_projectile"),
            EntityType.Builder.<MeteorProjectileEntity>of(MeteorProjectileEntity::new, MobCategory.MISC)
                    .clientTrackingRange(getMeteorTrackingDistance())
                    .sized(0.9F, 0.9F).build(ResourceKey.create(Registries.ENTITY_TYPE, OhMyMeteors.getIdentifier("meteor_projectile"))));


    public static final EntityType<MeteorCatEntity> METEOR_KITTY_CAT = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            OhMyMeteors.getIdentifier("meteor_cat"),
            EntityType.Builder.<MeteorCatEntity>of(MeteorCatEntity::new, MobCategory.MISC)
                    .sized(0.9F, 0.9F).build(ResourceKey.create(Registries.ENTITY_TYPE, OhMyMeteors.getIdentifier("meteor_cat"))));

    public static int getMeteorTrackingDistance(){
        Config.reloadConfig();
        return Config.METEOR_RENDER_DISTANCE;
    }

    public static void registerEntities(){
        FabricDefaultAttributeRegistry.register(METEOR_KITTY_CAT, MeteorCatEntity.createCatAttributes());
    }
}
