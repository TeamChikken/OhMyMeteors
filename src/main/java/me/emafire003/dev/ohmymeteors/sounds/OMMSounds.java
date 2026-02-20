package me.emafire003.dev.ohmymeteors.sounds;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class OMMSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, OhMyMeteors.MOD_ID);

    public static Supplier<SoundEvent> LASER_FIRE = registerSoundEvent("laser_fire");
    public static Supplier<SoundEvent> LASER_AREA_ON = registerSoundEvent("laser_area_on");
    public static Supplier<SoundEvent> LASER_AREA_OFF = registerSoundEvent("laser_area_off");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(OhMyMeteors.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
