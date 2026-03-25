package me.emafire003.dev.ohmymeteors.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class MeteorFlashScaleParticleOptions implements ParticleOptions {
    public static final float MIN_SCALE = 0.01F;
    public static final float MAX_SCALE = 50.0F;
    public static final StreamCodec<RegistryFriendlyByteBuf, MeteorFlashScaleParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            MeteorFlashScaleParticleOptions::getScale,
            MeteorFlashScaleParticleOptions::new
    );
    public static final Codec<Float> SCALE = Codec.FLOAT
            .validate(
                    float_ -> float_ >= MIN_SCALE && float_ <= MAX_SCALE ? DataResult.success(float_) : DataResult.error(() -> "Value must be within range [" + MIN_SCALE + ";" + MAX_SCALE +"]: " + float_)
            );

    public static final MapCodec<MeteorFlashScaleParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            SCALE.fieldOf("scale").forGetter(MeteorFlashScaleParticleOptions::getScale)
                    )
                    .apply(instance, MeteorFlashScaleParticleOptions::new)
    );

    private final float scale;

    public MeteorFlashScaleParticleOptions(float scale) {

        this.scale = Mth.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    public float getScale() {
        return this.scale;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return OMMParticles.METEOR_FLASH;
    }
}
