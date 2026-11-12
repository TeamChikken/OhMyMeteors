package me.emafire003.dev.ohmymeteors.datagen;


import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = OhMyMeteors.MOD_ID)
public class OMMDataGen {

    /*@SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(EnUsConfigGenerator(packOutput));
    }*/

    @SubscribeEvent // on the mod event bus
    public static void onGatherData(GatherDataEvent.Client event) {
        // Call event.createDatapackRegistryObjects(...) first if adding datapack objects

        event.createProvider(EnUsConfigGenerator::new);
    }

}
