package me.emafire003.dev.ohmymeteors.events;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.MeteorCatEntity;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OhMyMeteors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OMMModBusWhatever {
    //TODO verify that this works
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(OMMEntities.METEOR_KITTY_CAT.get(), MeteorCatEntity.createCatAttributes().build());
    }
}
