package me.emafire003.dev.ohmymeteors.particles;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.particles.meteor_flash.FlashScaleParticleOptions;
import me.emafire003.dev.ohmymeteors.particles.meteor_smoke.MeteorSmokeScaledOptions;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;

public class OMMParticles {

    public static final SimpleParticleType LASER_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType LASER_PARTICLE_SMALL = FabricParticleTypes.simple();
    public static final SimpleParticleType LASER_FLASH_PARTICLE = FabricParticleTypes.simple();
    public static final ParticleType<MeteorSmokeScaledOptions> METEOR_SMOKE_COSY = FabricParticleTypes.complex(MeteorSmokeScaledOptions.CODEC, MeteorSmokeScaledOptions.STREAM_CODEC);
    //public static final ParticleType<SmokeScaleParticleOptions> METEOR_SMOKE_SIGNAL = FabricParticleTypes.complex(SmokeScaleParticleOptions.CODEC, SmokeScaleParticleOptions.STREAM_CODEC);
    public static final ParticleType<FlashScaleParticleOptions> METEOR_FLASH = FabricParticleTypes.complex(FlashScaleParticleOptions.CODEC, FlashScaleParticleOptions.STREAM_CODEC);


    public static void registerParticles(){
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("laser_particle"),
                LASER_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("laser_particle_small"),
                LASER_PARTICLE_SMALL);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("laser_flash_particle"),
                LASER_FLASH_PARTICLE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("meteor_smoke_cosy"),
                METEOR_SMOKE_COSY);
        /*Registry.register(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("meteor_smoke_signal"),
                METEOR_SMOKE_SIGNAL);*/
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.getIdentifier("meteor_flash"),
                METEOR_FLASH);
    }
}
