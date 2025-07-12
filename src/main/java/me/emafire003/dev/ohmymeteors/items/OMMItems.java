package me.emafire003.dev.ohmymeteors.items;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class OMMItems {

    public static final Item METEORIC_CHUNK = registerItem("meteoric_chunk",
            new MeteoricChunk(new Item.Settings().maxCount(64)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, OhMyMeteors.getIdentifier("meteoric_chunk")))
            ),
            ItemGroups.INGREDIENTS, Items.BLAZE_POWDER);

    public static final Item METEORIC_ALLOY = registerItem("meteoric_alloy",
            new Item(new Item.Settings().maxCount(64)
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, OhMyMeteors.getIdentifier("meteoric_alloy")))
            ),
            ItemGroups.INGREDIENTS, METEORIC_CHUNK);

    public static final Item FOCUSING_LENSES = registerItem("focusing_lenses",
            new Item(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, OhMyMeteors.getIdentifier("focusing_lenses")))
                    .maxCount(16)),
            ItemGroups.INGREDIENTS, METEORIC_CHUNK);

    public static final Item METEOR_CAT_SPAWN_EGG = registerItem(
            "meteor_cat_spawn_egg",
            new SpawnEggItem(OMMEntities.METEOR_KITTY_CAT, 0x472750, 0x180A1E, new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, OhMyMeteors.getIdentifier("meteor_cat_spawn_egg")))),
                    ItemGroups.SPAWN_EGGS, Items.CAT_SPAWN_EGG
    );

    private static Item registerItem(String name, Item item, RegistryKey<ItemGroup> group, Item add_after){
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> content.addAfter(add_after, item));
        return Registry.register(Registries.ITEM, OhMyMeteors.getIdentifier(name), item);
    }

    public static void registerItems(){
        OhMyMeteors.LOGGER.debug("Registering items...");
    }
}
