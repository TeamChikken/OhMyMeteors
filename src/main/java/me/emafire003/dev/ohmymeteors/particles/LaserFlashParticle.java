package me.emafire003.dev.ohmymeteors.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.Camera;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class LaserFlashParticle extends TextureSheetParticle {
    LaserFlashParticle(ClientLevel clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
        this.lifetime = 4;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.setAlpha(0.6F - (this.age + tickDelta - 1.0F) * 0.25F * 0.5F);
        super.render(vertexConsumer, camera, tickDelta);
    }

    @Override
    public float getQuadSize(float tickDelta) {
        return 2.1F * Mth.sin((this.age + tickDelta - 1.0F) * 0.25F * (float) Math.PI);
    }


    public static class LaserFlashFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public LaserFlashFactory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            LaserFlashParticle flash = new LaserFlashParticle(clientWorld, d, e, f);
            flash.pickSprite(this.spriteProvider);
            return flash;
        }
    }

}
