package me.emafire003.dev.ohmymeteors.sounds;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class OMMSounds {


    public static SoundEvent LASER_FIRE = registerSoundEvent("laser_fire");
    public static SoundEvent LASER_AREA_ON = registerSoundEvent("laser_area_on");
    public static SoundEvent LASER_AREA_OFF = registerSoundEvent("laser_area_off");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = OhMyMeteors.getIdentifier(name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void registerSounds(){
        OhMyMeteors.LOGGER.debug("Registering OhMyMeteors sound effects...");
    }
}
