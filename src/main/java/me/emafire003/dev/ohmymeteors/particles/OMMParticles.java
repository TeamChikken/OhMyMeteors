package me.emafire003.dev.ohmymeteors.particles;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class OMMParticles {

    public static final DefaultParticleType LASER_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType LASER_PARTICLE_SMALL = FabricParticleTypes.simple();
    public static final DefaultParticleType LASER_FLASH_PARTICLE = FabricParticleTypes.simple();

    public static void registerParticles(){
        Registry.register(Registries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("laser_particle"),
                LASER_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("laser_particle_small"),
                LASER_PARTICLE_SMALL);
        Registry.register(Registries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("laser_flash_particle"),
                LASER_FLASH_PARTICLE);
    }
}
