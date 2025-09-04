package me.emafire003.dev.ohmymeteors.entities;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.Config;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class OMMEntities {

    public static final EntityType<MeteorProjectileEntity> METEOR_PROJECTILE_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            OhMyMeteors.getIdentifier("meteor_projectile"),
            EntityType.Builder.<MeteorProjectileEntity>create(MeteorProjectileEntity::new, SpawnGroup.MISC)
                    .maxTrackingRange(getMeteorTrackingDistance())
                    .dimensions(0.9F, 0.9F).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, OhMyMeteors.getIdentifier("meteor_projectile"))));


    public static final EntityType<MeteorCatEntity> METEOR_KITTY_CAT = Registry.register(Registries.ENTITY_TYPE,
            OhMyMeteors.getIdentifier("meteor_cat"),
            EntityType.Builder.<MeteorCatEntity>create(MeteorCatEntity::new, SpawnGroup.MISC)
                    .dimensions(0.9F, 0.9F).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, OhMyMeteors.getIdentifier("meteor_cat"))));

    public static int getMeteorTrackingDistance(){
        Config.reloadConfig();
        return Config.METEOR_RENDER_DISTANCE;
    }

    public static void registerEntities(){
        FabricDefaultAttributeRegistry.register(METEOR_KITTY_CAT, MeteorCatEntity.createCatAttributes());
    }
}
