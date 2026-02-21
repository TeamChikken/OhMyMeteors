package me.emafire003.dev.ohmymeteors.items;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class OMMItemTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OhMyMeteors.MOD_ID);

    public static final Supplier<CreativeModeTab> OMM_TAB = CREATIVE_MODE_TAB.register("ohmymeteors_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(OMMBlocks.METEORIC_ROCK.get()))
                    .title(Component.translatable("creativetab.ohmymeteors.ohmymeteors_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(OMMBlocks.METEORIC_ROCK.get());
                        output.accept(OMMItems.METEORIC_CHUNK.get());
                        output.accept(OMMItems.METEORIC_ALLOY.get());
                        output.accept(OMMBlocks.BASIC_METEOR_LASER.get());
                        output.accept(OMMBlocks.ADVANCED_METEOR_LASER.get());
                        output.accept(OMMItems.FOCUSING_LENSES.get());
                        output.accept(OMMItems.METEOR_CAT_SPAWN_EGG.get());


                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
