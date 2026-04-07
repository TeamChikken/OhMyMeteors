package me.emafire003.dev.ohmymeteors.particles.meteor_flash;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class FlashScaleParticleOptions implements ParticleOptions {
    public static final float MIN_SCALE = 0.01F;
    public static final float MAX_SCALE = 50.0F;
    public static final StreamCodec<RegistryFriendlyByteBuf, FlashScaleParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            FlashScaleParticleOptions::getScale,
            FlashScaleParticleOptions::new
    );
    public static final Codec<Float> SCALE = Codec.FLOAT
            .validate(
                    float_ -> float_ >= MIN_SCALE && float_ <= MAX_SCALE ? DataResult.success(float_) : DataResult.error(() -> "Value must be within range [" + MIN_SCALE + ";" + MAX_SCALE +"]: " + float_)
            );

    public static final MapCodec<FlashScaleParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            SCALE.fieldOf("scale").forGetter(FlashScaleParticleOptions::getScale)
                    )
                    .apply(instance, FlashScaleParticleOptions::new)
    );

    private final float scale;

    public FlashScaleParticleOptions(float scale) {

        this.scale = Mth.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    public float getScale() {
        return this.scale;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return OMMParticles.METEOR_FLASH.get();
    }
}
