package me.emafire003.dev.ohmymeteors.items;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OMMItems {
    // Create a Deferred Register to hold Items which will all be registered under the "ohmymeteors" namespace
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, OhMyMeteors.MOD_ID);


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

    public static final RegistryObject<Item> METEOR_CAT_SPAWN_EGG = ITEMS.register("meteor_cat_spawn_egg",
            () -> new ForgeSpawnEggItem(OMMEntities.METEOR_KITTY_CAT, 0x472750, 0x180A1E,
                    new Item.Properties()));

    public static final RegistryObject<Item> FOCUSING_LENSES = ITEMS.register("focusing_lenses",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> METEORIC_ALLOY = ITEMS.register("meteoric_alloy",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> METEORIC_CHUNK = ITEMS.register("meteoric_chunk",
            () -> new MeteoricChunk(new Item.Properties().stacksTo(64)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
