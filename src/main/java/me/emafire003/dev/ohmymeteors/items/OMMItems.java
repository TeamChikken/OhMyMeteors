package me.emafire003.dev.ohmymeteors.items;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class OMMItems {

    public static final Item METEORIC_CHUNK = registerItem("meteoric_chunk",
            new MeteoricChunk(new Item.Settings().maxCount(64)),
            ItemGroups.INGREDIENTS, Items.BLAZE_POWDER);

    public static final Item METEORIC_ALLOY = registerItem("meteoric_alloy",
            new Item(new Item.Settings().maxCount(64)),
            ItemGroups.INGREDIENTS, METEORIC_CHUNK);

    public static final Item FOCUSING_LENSES = registerItem("focusing_lenses",
            new Item(new Item.Settings().maxCount(16)),
            ItemGroups.INGREDIENTS, METEORIC_CHUNK);


    private static Item registerItem(String name, Item item, RegistryKey<ItemGroup> group, Item add_after){
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> content.addAfter(add_after, item));
        return Registry.register(Registries.ITEM, OhMyMeteors.getIdentifier(name), item);
    }

    public static void registerItems(){
        OhMyMeteors.LOGGER.debug("Registering items...");
    }
}