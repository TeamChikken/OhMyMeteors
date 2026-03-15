package me.emafire003.dev.ohmymeteors.particles.meteor_smoke;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import me.emafire003.dev.ohmymeteors.particles.meteor_flash.FlashScaleParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class MeteorSmokeScaledOptions extends FlashScaleParticleOptions {

    public static ParticleOptions.Deserializer<MeteorSmokeScaledOptions> DESERIALIZER = new ParticleOptions.Deserializer<MeteorSmokeScaledOptions>() {
        public MeteorSmokeScaledOptions fromCommand(ParticleType<MeteorSmokeScaledOptions> particleType, StringReader
                stringReader) throws CommandSyntaxException {
            //TODO should not be needed //stringReader.expect(' ');
            float f = stringReader.readFloat();
            return new MeteorSmokeScaledOptions(f);
        }

        public MeteorSmokeScaledOptions fromNetwork(ParticleType<MeteorSmokeScaledOptions> particleType, FriendlyByteBuf
                friendlyByteBuf) {
            return new MeteorSmokeScaledOptions(friendlyByteBuf.readFloat());
        }
    };

    public MeteorSmokeScaledOptions(float scale) {
        super(scale);
    }

    public MeteorSmokeScaledOptions() {
        super(3f);
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return OMMParticles.METEOR_SMOKE_COSY;
    }
}
