package me.emafire003.dev.ohmymeteors.particles.meteor_smoke;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.emafire003.dev.ohmymeteors.particles.meteor_flash.FlashScaleParticleOptions;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class MeteorSmokeScaledOptions extends FlashScaleParticleOptions {
    public static final StreamCodec<RegistryFriendlyByteBuf, MeteorSmokeScaledOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            MeteorSmokeScaledOptions::getScale,
            MeteorSmokeScaledOptions::new
    );
    public static final Codec<Float> SCALE = Codec.FLOAT
            .validate(
                    float_ -> float_ >= MIN_SCALE && float_ <= MAX_SCALE ? DataResult.success(float_) : DataResult.error(() -> "Value must be within range [" + MIN_SCALE + ";" + MAX_SCALE +"]: " + float_)
            );

    public static final MapCodec<MeteorSmokeScaledOptions> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            SCALE.fieldOf("scale").forGetter(MeteorSmokeScaledOptions::getScale)
                    )
                    .apply(instance, MeteorSmokeScaledOptions::new)
    );

    public MeteorSmokeScaledOptions(float scale) {
        super(scale);
    }

    public MeteorSmokeScaledOptions() {
        super(3f);
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return OMMParticles.METEOR_SMOKE_COSY.get();
    }
}
