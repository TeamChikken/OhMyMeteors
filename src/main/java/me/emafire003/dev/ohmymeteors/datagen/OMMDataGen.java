package me.emafire003.dev.ohmymeteors.datagen;


import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OhMyMeteors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OMMDataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var out = gen.getPackOutput();

        gen.addProvider(event.includeClient(), new EnUsConfigGenerator(out));
    }
}