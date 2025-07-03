package me.emafire003.dev.ohmymeteors.blocks;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlockEntity;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlockEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;

public class OMMBlocks {

    public static final Block BASIC_METEOR_LASER = registerBlock("basic_meteor_laser",
            new BasicMeteorLaserBlock(AbstractBlock.Settings.create()
                    .strength(1.9f)
                    .luminance(value -> 1) //Makes a little bit of light
                    .sounds(BlockSoundGroup.COPPER)
                    .requiresTool()
            ), ItemGroups.REDSTONE, Items.REDSTONE_LAMP);

    public static final BlockEntityType<BasicMeteorLaserBlockEntity> BASIC_METEOR_LASER_BLOCK_ENTITY =
            register("basic_meteor_laser", BasicMeteorLaserBlockEntity::new, BASIC_METEOR_LASER);

    public static final Block ADVANCED_METEOR_LASER = registerBlock("advanced_meteor_laser",
            new AdvancedMeteorLaserBlock(AbstractBlock.Settings.create()
                    .strength(2f)
                    .luminance(value -> 2) //Makes a little bit moreof light
                    .sounds(BlockSoundGroup.COPPER)
                    .requiresTool()
            ), ItemGroups.REDSTONE, Items.REDSTONE_LAMP);

    public static final BlockEntityType<AdvancedMeteorLaserBlockEntity> ADVANCED_METEOR_LASER_BLOCK_ENTITY =
            register("advanced_meteor_laser", AdvancedMeteorLaserBlockEntity::new, ADVANCED_METEOR_LASER);


    public static final Block METEORIC_ROCK = registerBlock("meteoric_rock",
            new MeteoricRockBlock(AbstractBlock.Settings.create().requiresTool().strength(4F).solid()),
            //new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_IRON_ORE)),
            ItemGroups.NATURAL, Items.SMOOTH_BASALT);


    private static Block registerBlock(String name, Block block, RegistryKey<ItemGroup> tab, Item add_after) {
        Block the_block = Registry.register(Registries.BLOCK, OhMyMeteors.getIdentifier(name), block);
        Item the_item = Registry.register(Registries.ITEM, OhMyMeteors.getIdentifier(name), new BlockItem(block, new Item.Settings()));
        ItemGroupEvents.modifyEntriesEvent(tab).register(content -> content.addAfter(add_after, the_item));
        return the_block;
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, OhMyMeteors.getIdentifier(name),

                FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build()
        );
    }

    public static void registerBlocks(){

    }
}