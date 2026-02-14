package me.emafire003.dev.ohmymeteors.particles;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class OMMParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, OhMyMeteors.MOD_ID);


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
