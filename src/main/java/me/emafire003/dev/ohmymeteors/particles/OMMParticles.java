package me.emafire003.dev.ohmymeteors.particles;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;

public class OMMParticles {

    public static final SimpleParticleType LASER_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType LASER_PARTICLE_SMALL = FabricParticleTypes.simple();
    public static final SimpleParticleType LASER_FLASH_PARTICLE = FabricParticleTypes.simple();

    public static void registerParticles(){
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("laser_particle"),
                LASER_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("laser_particle_small"),
                LASER_PARTICLE_SMALL);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("laser_flash_particle"),
                LASER_FLASH_PARTICLE);
    }
}
