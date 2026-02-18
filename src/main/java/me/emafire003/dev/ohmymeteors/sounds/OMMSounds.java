package me.emafire003.dev.ohmymeteors.sounds;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

public class OMMSounds {


    public static SoundEvent LASER_FIRE = registerSoundEvent("laser_fire");
    public static SoundEvent LASER_AREA_ON = registerSoundEvent("laser_area_on");
    public static SoundEvent LASER_AREA_OFF = registerSoundEvent("laser_area_off");

    private static SoundEvent registerSoundEvent(String name) {
        ResourceLocation id = OhMyMeteors.getIdentifier(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerSounds(){
        OhMyMeteors.LOGGER.debug("Registering OhMyMeteors sound effects...");
    }
}