package me.emafire003.dev.ohmymeteors.particles;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.particles.meteor_flash.FlashScaleParticleOptions;
import me.emafire003.dev.ohmymeteors.particles.meteor_flash.MeteorFlashParticleType;
import me.emafire003.dev.ohmymeteors.particles.meteor_smoke.MeteorSmokeParticleType;
import me.emafire003.dev.ohmymeteors.particles.meteor_smoke.MeteorSmokeScaledOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class OMMParticles {


    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, OhMyMeteors.MOD_ID);

    public static final Supplier<SimpleParticleType> LASER_PARTICLE = PARTICLE_TYPES.register(
            "laser_particle", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> LASER_PARTICLE_SMALL = PARTICLE_TYPES.register(
            "laser_particle_small", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> LASER_FLASH_PARTICLE = PARTICLE_TYPES.register(
            "laser_flash_particle", () -> new SimpleParticleType(true));
    public static final Supplier<MeteorSmokeParticleType> METEOR_SMOKE_COSY = PARTICLE_TYPES.register(
            "meteor_smoke_cosy", () -> new MeteorSmokeParticleType(true, MeteorSmokeScaledOptions.DESERIALIZER));
    public static final Supplier<MeteorFlashParticleType> METEOR_FLASH = PARTICLE_TYPES.register(
            "meteor_flash", () -> new MeteorFlashParticleType(true, FlashScaleParticleOptions.DESERIALIZER));


    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
