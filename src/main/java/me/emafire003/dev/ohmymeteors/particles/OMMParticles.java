package me.emafire003.dev.ohmymeteors.particles;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class OMMParticles {

    public static final SimpleParticleType LASER_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType LASER_PARTICLE_SMALL = FabricParticleTypes.simple();
    public static final SimpleParticleType LASER_FLASH_PARTICLE = FabricParticleTypes.simple();
    public static final ParticleType<MeteorSmokeScaledOptions> METEOR_SMOKE_COSY = FabricParticleTypes.complex(MeteorSmokeScaledOptions.DESERIALIZER);
    //public static final ParticleType<SmokeScaleParticleOptions> METEOR_SMOKE_SIGNAL = FabricParticleTypes.complex(SmokeScaleParticleOptions.CODEC, SmokeScaleParticleOptions.STREAM_CODEC);
    public static final ParticleType<FlashScaleParticleOptions> METEOR_FLASH = FabricParticleTypes.complex(FlashScaleParticleOptions.DESERIALIZER);

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, OhMyMeteors.MOD_ID);


    public static final Supplier<SimpleParticleType> LASER_PARTICLE = PARTICLE_TYPES.register(
            "laser_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> LASER_PARTICLE_SMALL = PARTICLE_TYPES.register(
            "laser_particle_small", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> LASER_FLASH_PARTICLE = PARTICLE_TYPES.register(
            "laser_flash_particle", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
