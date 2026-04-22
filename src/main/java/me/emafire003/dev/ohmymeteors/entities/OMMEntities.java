package me.emafire003.dev.ohmymeteors.entities;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.Config;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class OMMEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, OhMyMeteors.MOD_ID);

    public static final Supplier<EntityType<MeteorProjectileEntity>> METEOR_PROJECTILE_ENTITY =
            ENTITY_TYPES.register(
            "meteor_projectile", () ->
            EntityType.Builder.<MeteorProjectileEntity>of(MeteorProjectileEntity::new, MobCategory.MISC)
                    .clientTrackingRange(getMeteorTrackingDistance())
                    .sized(0.9F, 0.9F).build("meteor_projectile"));

    public static final Supplier<EntityType<MeteorCatEntity>> METEOR_KITTY_CAT = ENTITY_TYPES.register(
            "meteor_cat", () ->
            EntityType.Builder.<MeteorCatEntity>of(MeteorCatEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 0.9F).build("meteor_cat"));

    public static int getMeteorTrackingDistance(){
        return OhMyMeteors.CONFIG.visualsSection.meteor_render_distance;
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        eventBus.register(OMMEntities.class);
    }

    @SubscribeEvent // on the mod event bus
    public static void createDefaultAttributes(EntityAttributeCreationEvent event) {
        event.put(
                // Your entity type.
                OMMEntities.METEOR_KITTY_CAT.get(),
                MeteorCatEntity.createCatAttributes().build()

        );
    }
}
