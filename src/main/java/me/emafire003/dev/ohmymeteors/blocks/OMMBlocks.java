package me.emafire003.dev.ohmymeteors.blocks;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlockEntity;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlockEntity;
import me.emafire003.dev.ohmymeteors.items.OMMItems;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class OMMBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "ohmymeteors" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OhMyMeteors.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, OhMyMeteors.MOD_ID);


    public static final RegistryObject<Block> BASIC_METEOR_LASER = registerBlock("basic_meteor_laser",
            () -> new BasicMeteorLaserBlock(BlockBehaviour.Properties.of()
                    .strength(1.9f)
                    .lightLevel(value -> 1) //Makes a little bit of light
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
            ));

    public static final Supplier<BlockEntityType<BasicMeteorLaserBlockEntity>> BASIC_METEOR_LASER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("basic_meteor_laser_be", () -> BlockEntityType.Builder.of(
                    BasicMeteorLaserBlockEntity::new, BASIC_METEOR_LASER.get()
            ).build(null));

    public static final RegistryObject<Block> ADVANCED_METEOR_LASER = registerBlock("advanced_meteor_laser",
            () -> new AdvancedMeteorLaserBlock(BlockBehaviour.Properties.of()
                    .strength(2f)
                    .lightLevel(value -> 2) //Makes a little bit moreof light
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
            ));

    public static final Supplier<BlockEntityType<AdvancedMeteorLaserBlockEntity>> ADVANCED_METEOR_LASER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("advanced_meteor_laser", () -> BlockEntityType.Builder.of(
                    AdvancedMeteorLaserBlockEntity::new, ADVANCED_METEOR_LASER.get()
            ).build(null));

    public static final BlockEntityType<AdvancedMeteorLaserBlockEntity> ADVANCED_METEOR_LASER_BLOCK_ENTITY =
            register("advanced_meteor_laser", AdvancedMeteorLaserBlockEntity::new, ADVANCED_METEOR_LASER);


    //TODO fix
    public static final Block METEORIC_ROCK = registerBlock("meteoric_rock",
            new MeteoricRockBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops()
                    .emissiveRendering((blockState, getter, pos) -> {
                        switch (blockState.getValue(OMMProperties.ROCK_TEMPERATURE)){
                            case MID -> {
                                return true;
                            }case HOT -> {
                                return true;
                            }default -> {
                                return false;
                            }
                        }
                    })
                    .lightLevel((blockState) -> {
                        switch (blockState.getValue(OMMProperties.ROCK_TEMPERATURE)){
                            case MID -> {
                                return 2;
                            }case HOT -> {
                                return 3;
                            }default -> {
                                return 0;
                            }
                        }
                    })
            .strength(4F).forceSolidOn()),

            //new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE_IRON_ORE)),
            CreativeModeTabs.NATURAL_BLOCKS, Items.SMOOTH_BASALT);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return OMMItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }

    /*
    private static Block registerBlock(String name, Block block, ResourceKey<CreativeModeTab> tab, Item add_after) {

        // Creates a new Block with the id "ohmymeteors:example_block", combining the namespace and path
        public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
        // Creates a new BlockItem with the id "ohmymeteors:example_block", combining the namespace and path
        public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

        Block the_block = BLOCKS.registerBlock(name, );
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

    }*/
}
