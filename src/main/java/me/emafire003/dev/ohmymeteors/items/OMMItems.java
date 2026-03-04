package me.emafire003.dev.ohmymeteors.items;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.Registry;

public class OMMItems {

    public static final Item METEORIC_CHUNK = registerItem("meteoric_chunk",
            new MeteoricChunk(new FabricItemSettings().rarity(Rarity.COMMON).stacksTo(64).tab(CreativeModeTab.TAB_MISC)));

    public static final Item METEORIC_ALLOY = registerItem("meteoric_alloy",
            new Item(new FabricItemSettings().rarity(Rarity.COMMON).stacksTo(64).tab(CreativeModeTab.TAB_MISC)));

    public static final Item FOCUSING_LENSES = registerItem("focusing_lenses",
            new Item( new FabricItemSettings().rarity(Rarity.COMMON).stacksTo(16).tab(CreativeModeTab.TAB_MISC)));

    public static final Item METEOR_CAT_SPAWN_EGG = registerItem(
            "meteor_cat_spawn_egg",
            new Item( new FabricItemSettings().rarity(Rarity.COMMON).tab(CreativeModeTab.TAB_MISC)));


    private static Item registerItem(String name, Item item){
        return Registry.register(Registry.ITEM, OhMyMeteors.getIdentifier(name), item);
    }

    public static void registerItems(){
        OhMyMeteors.LOGGER.debug("Registering items...");
    }
}
