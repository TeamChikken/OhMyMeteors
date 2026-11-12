package me.emafire003.dev.ohmymeteors.items;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class OMMItems {
    // Create a Deferred Register to hold Items which will all be registered under the "ohmymeteors" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OhMyMeteors.MOD_ID);

    public static final DeferredItem<SpawnEggItem> METEOR_CAT_SPAWN_EGG  = ITEMS.registerItem("meteor_cat_spawn_egg",
            properties -> new SpawnEggItem(
                    // The properties passed into the lambda.
                    // Using `spawnEgg` to set the DataComponent.
                    // This is done in the lambda to prevent the entity type from resolving before registration.
                    properties
                            .setId(ResourceKey.create(Registries.ITEM, OhMyMeteors.getIdentifier("meteor_cat_spawn_egg")))
                            .spawnEgg(OMMEntities.METEOR_KITTY_CAT.get())
            ));

    public static final DeferredItem<Item> FOCUSING_LENSES = ITEMS.registerItem("focusing_lenses",
            (properties) -> new Item(properties
                    .setId(ResourceKey.create(Registries.ITEM, OhMyMeteors.getIdentifier("focusing_lenses")))
                    .stacksTo(16)));

    public static final DeferredItem<Item> METEORIC_ALLOY = ITEMS.registerItem("meteoric_alloy",
            (properties) -> new Item(properties
                    .setId(ResourceKey.create(Registries.ITEM, OhMyMeteors.getIdentifier("meteoric_alloy")))
                    .stacksTo(64)));

    public static final DeferredItem<Item> METEORIC_CHUNK = ITEMS.registerItem("meteoric_chunk",
            (properties) -> new MeteoricChunk(properties
                    .setId(ResourceKey.create(Registries.ITEM, OhMyMeteors.getIdentifier("meteoric_chunk")))
                    .stacksTo(64)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
