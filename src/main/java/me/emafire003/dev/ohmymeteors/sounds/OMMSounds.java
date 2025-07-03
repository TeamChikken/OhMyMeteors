package me.emafire003.dev.ohmymeteors.sounds;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class OMMSounds {


    public static SoundEvent LASER_FIRE = registerSoundEvent("laser_fire");
    public static SoundEvent LASER_AREA_ON = registerSoundEvent("laser_area_on");
    public static SoundEvent LASER_AREA_OFF = registerSoundEvent("laser_area_off");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = OhMyMeteors.getIdentifier(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds(){
        OhMyMeteors.LOGGER.debug("Registering OhMyMeteors sound effects...");
    }
}