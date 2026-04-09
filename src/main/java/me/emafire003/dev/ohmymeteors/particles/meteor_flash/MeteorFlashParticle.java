package me.emafire003.dev.ohmymeteors.particles.meteor_flash;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;

public class MeteorFlashParticle<T extends FlashScaleParticleOptions> extends SingleQuadParticle {

    private final FlashScaleParticleOptions options;


    MeteorFlashParticle(ClientLevel clientWorld, double d, double e, double f, TextureAtlasSprite textureAtlasSprite, FlashScaleParticleOptions options) {
        super(clientWorld, d, e, f, textureAtlasSprite);
        this.lifetime = 4;
        this.options = options;
    }

    @Override
    public SingleQuadParticle.@NonNull Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void extract(QuadParticleRenderState reusedState, Camera camera, float partialTick) {
        this.setAlpha(0.6F - (this.age + partialTick - 1.0F) * 0.25F * 0.5F);
        super.extract(reusedState, camera, partialTick);
    }

    @Override
    public float getQuadSize(float tickDelta) {
        //TODO find a way to set the value when spawning the particle
        //ParticleTypes
        return options.getScale() * Mth.sin((this.age + tickDelta - 1.0F) * 0.25F * (float) Math.PI);
        //return 2.1F * Mth.sin((this.age + tickDelta - 1.0F) * 0.25F * (float) Math.PI);
    }


    @Environment(EnvType.CLIENT)
    public static class LaserFlashFactory implements ParticleProvider<FlashScaleParticleOptions> {
        private final SpriteSet spriteProvider;

        public LaserFlashFactory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(FlashScaleParticleOptions type, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, RandomSource randomSource) {
            return new MeteorFlashParticle<>(clientWorld, d, e, f, this.spriteProvider.get(randomSource), type);
        }
    }

}
