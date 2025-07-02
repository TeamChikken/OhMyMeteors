package me.emafire003.dev.ohmymeteors.blocks;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlockEntity;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlockEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class OMMBlocks {

    public static final Block BASIC_METEOR_LASER = registerBlock("basic_meteor_laser",
            new BasicMeteorLaserBlock(
                    FabricBlockSettings.of(Material.METAL)
                            .strength(1.9f)
                            .luminance(value -> 1) //Makes a little bit of light
                            .sounds(BlockSoundGroup.COPPER)
                            .solidBlock((state, view, pos) -> true)
                            .requiresTool()), ItemGroup.REDSTONE);
    //Material.METAL

    public static final BlockEntityType<BasicMeteorLaserBlockEntity> BASIC_METEOR_LASER_BLOCK_ENTITY =
            register("basic_meteor_laser", BasicMeteorLaserBlockEntity::new, BASIC_METEOR_LASER);

    public static final Block ADVANCED_METEOR_LASER = registerBlock("advanced_meteor_laser",
            new AdvancedMeteorLaserBlock(FabricBlockSettings.of(Material.METAL)
                    .strength(2f)
                    .luminance(value -> 2) //Makes a little bit moreof light
                    .sounds(BlockSoundGroup.COPPER)
                    .solidBlock((state, view, pos) -> true)
                    .requiresTool()
            ), ItemGroup.REDSTONE);

    public static final BlockEntityType<AdvancedMeteorLaserBlockEntity> ADVANCED_METEOR_LASER_BLOCK_ENTITY =
            register("advanced_meteor_laser", AdvancedMeteorLaserBlockEntity::new, ADVANCED_METEOR_LASER);


    public static final Block METEORIC_ROCK = registerBlock("meteoric_rock",
            new MeteoricRockBlock(FabricBlockSettings.of(Material.STONE)
                    .requiresTool().strength(4F)),
            //new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_IRON_ORE)),
            ItemGroup.MATERIALS);

    private static Block registerBlock(String name, Block block, ItemGroup tab) {
        registerBlockItem(name, block, tab);
        return Registry.register(Registry.BLOCK, OhMyMeteors.getIdentifier(name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup tab) {
        return Registry.register(Registry.ITEM, OhMyMeteors.getIdentifier(name),
                new BlockItem(block, new FabricItemSettings().group(tab)));
    }


    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, OhMyMeteors.getIdentifier(name),

                FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build()
        );
    }

    public static void registerBlocks(){

    }
}
