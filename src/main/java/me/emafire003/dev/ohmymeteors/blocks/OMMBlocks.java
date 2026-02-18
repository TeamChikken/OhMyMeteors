package me.emafire003.dev.ohmymeteors.blocks;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlockEntity;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlockEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SoundType;

public class OMMBlocks {

    public static final Block BASIC_METEOR_LASER = registerBlock("basic_meteor_laser",
            new BasicMeteorLaserBlock(BlockBehaviour.Properties.of()
                    .strength(1.9f)
                    .lightLevel(value -> 1) //Makes a little bit of light
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
            ), CreativeModeTabs.REDSTONE_BLOCKS, Items.REDSTONE_LAMP);

    public static final BlockEntityType<BasicMeteorLaserBlockEntity> BASIC_METEOR_LASER_BLOCK_ENTITY =
            register("basic_meteor_laser", BasicMeteorLaserBlockEntity::new, BASIC_METEOR_LASER);

    public static final Block ADVANCED_METEOR_LASER = registerBlock("advanced_meteor_laser",
            new AdvancedMeteorLaserBlock(BlockBehaviour.Properties.of()
                    .strength(2f)
                    .lightLevel(value -> 2) //Makes a little bit moreof light
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
            ), CreativeModeTabs.REDSTONE_BLOCKS, Items.REDSTONE_LAMP);

    public static final BlockEntityType<AdvancedMeteorLaserBlockEntity> ADVANCED_METEOR_LASER_BLOCK_ENTITY =
            register("advanced_meteor_laser", AdvancedMeteorLaserBlockEntity::new, ADVANCED_METEOR_LASER);


    public static final Block METEORIC_ROCK = registerBlock("meteoric_rock",
            new MeteoricRockBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4F).forceSolidOn()),
            //new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_IRON_ORE)),
            CreativeModeTabs.NATURAL_BLOCKS, Items.SMOOTH_BASALT);


    private static Block registerBlock(String name, Block block, ResourceKey<CreativeModeTab> tab, Item add_after) {
        Block the_block = Registry.register(BuiltInRegistries.BLOCK, OhMyMeteors.getIdentifier(name), block);
        Item the_item = Registry.register(BuiltInRegistries.ITEM, OhMyMeteors.getIdentifier(name), new BlockItem(block, new net.minecraft.world.item.Item.Properties()));
        ItemGroupEvents.modifyEntriesEvent(tab).register(content -> content.addAfter(add_after, the_item));
        return the_block;
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, OhMyMeteors.getIdentifier(name),

                FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build()
        );
    }

    public static void registerBlocks(){

    }
}