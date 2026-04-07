package me.emafire003.dev.ohmymeteors.particles.meteor_flash;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class MeteorFlashParticleType extends ParticleType<FlashScaleParticleOptions> {

    public MeteorFlashParticleType(boolean overrideLimiter) {
        // Pass the deserializer to super.
        super(overrideLimiter);
    }

    @Override
    public @NotNull MapCodec<FlashScaleParticleOptions> codec() {
        return FlashScaleParticleOptions.CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, FlashScaleParticleOptions> streamCodec() {
        return FlashScaleParticleOptions.STREAM_CODEC;
    }
}