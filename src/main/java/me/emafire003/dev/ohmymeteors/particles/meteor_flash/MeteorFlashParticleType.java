package me.emafire003.dev.ohmymeteors.particles.meteor_flash;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

public class MeteorFlashParticleType extends ParticleType<FlashScaleParticleOptions> {

    public MeteorFlashParticleType(boolean pOverrideLimiter, ParticleOptions.Deserializer<FlashScaleParticleOptions> pDeserializer) {
        super(pOverrideLimiter, pDeserializer);
    }

    @Override
    public @NotNull Codec<FlashScaleParticleOptions> codec() {
        return FlashScaleParticleOptions.CODEC;
    }

    public ParticleOptions.Deserializer<FlashScaleParticleOptions> getDeserializer() {
        return FlashScaleParticleOptions.DESERIALIZER;
    }

}