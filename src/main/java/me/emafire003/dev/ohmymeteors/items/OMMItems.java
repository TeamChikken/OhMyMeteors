package me.emafire003.dev.ohmymeteors.items;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class OMMItems {
    // Create a Deferred Register to hold Items which will all be registered under the "ohmymeteors" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OhMyMeteors.MOD_ID);


    /*public static final Item METEORIC_CHUNK = registerItem("meteoric_chunk",
            new MeteoricChunk(new Item.Properties().stacksTo(64)),
            CreativeModeTabs.INGREDIENTS, Items.BLAZE_POWDER);

    public static final Item METEORIC_ALLOY = registerItem("meteoric_alloy",
            new Item(new Item.Properties().stacksTo(64)),
            CreativeModeTabs.INGREDIENTS, METEORIC_CHUNK);

    public static final Item FOCUSING_LENSES = registerItem("focusing_lenses",
            new Item(new Item.Properties().stacksTo(16)),
            CreativeModeTabs.INGREDIENTS, METEORIC_CHUNK);*/

    /*public static final Item METEOR_CAT_SPAWN_EGG = registerItem(
            "meteor_cat_spawn_egg",
            new SpawnEggItem(OMMEntities.METEOR_KITTY_CAT, 0x472750, 0x180A1E, new Item.Properties()),
                    CreativeModeTabs.SPAWN_EGGS, Items.CAT_SPAWN_EGG
    );*/

    public static final DeferredItem<Item> METEOR_CAT_SPAWN_EGG = ITEMS.register("meteor_cat_spawn_egg",
            () -> new DeferredSpawnEggItem(OMMEntities.METEOR_KITTY_CAT, 0x472750, 0x180A1E,
                    new Item.Properties()));

    public static final DeferredItem<Item> FOCUSING_LENSES = ITEMS.registerItem("focusing_lenses",
            (properties) -> new Item(properties.stacksTo(16)));

    public static final DeferredItem<Item> METEORIC_ALLOY = ITEMS.registerItem("meteoric_alloy",
            (properties) -> new Item(properties.stacksTo(64)));

    public static final DeferredItem<Item> METEORIC_CHUNK = ITEMS.registerItem("meteoric_chunk",
            (properties) -> new MeteoricChunk(properties.stacksTo(64)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
