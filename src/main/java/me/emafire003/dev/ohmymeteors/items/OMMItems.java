package me.emafire003.dev.ohmymeteors.items;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;

public class OMMItems {

    public static final Item METEORIC_CHUNK = registerItem("meteoric_chunk",
            new MeteoricChunk(new Item.Properties().stacksTo(64)
                    .setId(ResourceKey.create(Registries.ITEM, OhMyMeteors.getIdentifier("meteoric_chunk")))
            ),
            CreativeModeTabs.INGREDIENTS, Items.BLAZE_POWDER);

    public static final Item METEORIC_ALLOY = registerItem("meteoric_alloy",
            new Item(new Item.Properties().stacksTo(64)
                    .setId(ResourceKey.create(Registries.ITEM, OhMyMeteors.getIdentifier("meteoric_alloy")))
            ),
            CreativeModeTabs.INGREDIENTS, METEORIC_CHUNK);

    public static final Item FOCUSING_LENSES = registerItem("focusing_lenses",
            new Item(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, OhMyMeteors.getIdentifier("focusing_lenses")))
                    .stacksTo(16)),
            CreativeModeTabs.INGREDIENTS, METEORIC_CHUNK);

    public static final Item METEOR_CAT_SPAWN_EGG = registerItem(
            "meteor_cat_spawn_egg",
            new SpawnEggItem(new Item.Properties().spawnEgg(OMMEntities.METEOR_KITTY_CAT)
                    .setId(ResourceKey.create(Registries.ITEM, OhMyMeteors.getIdentifier("meteor_cat_spawn_egg")))),
                    CreativeModeTabs.SPAWN_EGGS, Items.CAT_SPAWN_EGG
    );

    private static Item registerItem(String name, Item item, ResourceKey<CreativeModeTab> group, Item add_after){
        CreativeModeTabEvents.modifyOutputEvent(group).register(content -> content.insertAfter(add_after, item));
        return Registry.register(BuiltInRegistries.ITEM, OhMyMeteors.getIdentifier(name), item);
    }

    public static void registerItems(){
        OhMyMeteors.LOGGER.debug("Registering items...");
    }
}
