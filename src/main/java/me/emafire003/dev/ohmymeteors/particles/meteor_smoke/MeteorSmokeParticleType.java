package me.emafire003.dev.ohmymeteors.particles.meteor_smoke;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class MeteorSmokeParticleType extends ParticleType<MeteorSmokeScaledOptions> {

    public MeteorSmokeParticleType(boolean pOverrideLimiter, ParticleOptions.Deserializer<MeteorSmokeScaledOptions> pDeserializer) {
        super(pOverrideLimiter, pDeserializer);
    }

    @Override
    public Codec<MeteorSmokeScaledOptions> codec() {
        return MeteorSmokeScaledOptions.CODEC;
    }


    public ParticleOptions.Deserializer<MeteorSmokeScaledOptions> getDeserializer() {
        return MeteorSmokeScaledOptions.DESERIALIZER;
    }
}