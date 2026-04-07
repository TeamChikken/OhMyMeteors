package me.emafire003.dev.ohmymeteors.particles.meteor_smoke;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class MeteorSmokeParticleType extends ParticleType<MeteorSmokeScaledOptions> {

    public MeteorSmokeParticleType(boolean overrideLimiter) {
        // Pass the deserializer to super.
        super(overrideLimiter);
    }

    @Override
    public @NotNull MapCodec<MeteorSmokeScaledOptions> codec() {
        return MeteorSmokeScaledOptions.CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, MeteorSmokeScaledOptions> streamCodec() {
        return MeteorSmokeScaledOptions.STREAM_CODEC;
    }
}