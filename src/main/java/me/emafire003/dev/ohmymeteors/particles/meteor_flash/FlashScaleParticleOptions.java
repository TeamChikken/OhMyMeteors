package me.emafire003.dev.ohmymeteors.particles.meteor_flash;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FlashScaleParticleOptions implements ParticleOptions {
    public static final float MIN_SCALE = 0.01F;
    public static final float MAX_SCALE = 50.0F;

    public static Codec<FlashScaleParticleOptions> CODEC = RecordCodecBuilder.create(
            (instance) -> 
                    instance.group(Codec.FLOAT.fieldOf("scale").forGetter((flashParticleOptions) -> flashParticleOptions.scale))
                            .apply(instance, FlashScaleParticleOptions::new));

    public static ParticleOptions.Deserializer<FlashScaleParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<FlashScaleParticleOptions>() {
        public FlashScaleParticleOptions fromCommand(ParticleType<FlashScaleParticleOptions> particleType, StringReader
        stringReader) throws CommandSyntaxException {
            //TODO should not be needed //stringReader.expect(' ');
            float f = stringReader.readFloat();
            return new FlashScaleParticleOptions(f);
        }

        public FlashScaleParticleOptions fromNetwork(ParticleType<FlashScaleParticleOptions> particleType, FriendlyByteBuf
        friendlyByteBuf) {
            return new FlashScaleParticleOptions(friendlyByteBuf.readFloat());
        }
    };

    private final float scale;

    public FlashScaleParticleOptions(float scale) {
        this.scale = Mth.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    public float getScale() {
        return this.scale;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return OMMParticles.METEOR_FLASH;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeFloat(this.scale);
    }

    @Override
    public @NotNull String writeToString() { //"%s %.2f %.2f %.2f %.2f"
        return String.format(Locale.ROOT, "%s", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.scale);
    }

}
