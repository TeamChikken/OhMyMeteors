package me.emafire003.dev.ohmymeteors.blocks;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlockEntity;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static me.emafire003.dev.ohmymeteors.items.OMMItems.ITEMS;

public class OMMBlocks {

    // Create a Deferred Register to hold Blocks which will all be registered under the "ohmymeteors" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OhMyMeteors.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, OhMyMeteors.MOD_ID);

    public static final DeferredBlock<Block> BASIC_METEOR_LASER = registerBlock("basic_meteor_laser",
            () -> new BasicMeteorLaserBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, OhMyMeteors.getIdentifier("basic_meteor_laser")))
                    .strength(1.9f)
                    .lightLevel(value -> 1) //Makes a little bit of light
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
            ));

    public static final Supplier<BlockEntityType<BasicMeteorLaserBlockEntity>> BASIC_METEOR_LASER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("basic_meteor_laser_be", () -> new BlockEntityType<>(
                    BasicMeteorLaserBlockEntity::new, BASIC_METEOR_LASER.get()
            ));

    public static final DeferredBlock<Block> ADVANCED_METEOR_LASER = registerBlock("advanced_meteor_laser",
            () -> new AdvancedMeteorLaserBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, OhMyMeteors.getIdentifier("advanced_meteor_laser")))
                    .strength(2f)
                    .lightLevel(value -> 2) //Makes a little bit moreof light
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
            ));

    public static final Supplier<BlockEntityType<AdvancedMeteorLaserBlockEntity>> ADVANCED_METEOR_LASER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("advanced_meteor_laser", () -> new BlockEntityType<>(
                    AdvancedMeteorLaserBlockEntity::new, ADVANCED_METEOR_LASER.get()
            ));

    public static final DeferredBlock<Block> METEORIC_ROCK = registerBlock("meteoric_rock",
            () -> new MeteoricRockBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, OhMyMeteors.getIdentifier("meteoric_rock")))
                    .requiresCorrectToolForDrops()
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
                    .strength(4F).forceSolidOn())
    );


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, OhMyMeteors.getIdentifier(name)))));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }
}
