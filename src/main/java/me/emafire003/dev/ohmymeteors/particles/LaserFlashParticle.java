package me.emafire003.dev.ohmymeteors.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class LaserFlashParticle extends SpriteBillboardParticle {
    LaserFlashParticle(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
        this.maxAge = 4;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.setAlpha(0.6F - (this.age + tickDelta - 1.0F) * 0.25F * 0.5F);
        super.buildGeometry(vertexConsumer, camera, tickDelta);
    }

    @Override
    public float getSize(float tickDelta) {
        return 2.1F * MathHelper.sin((this.age + tickDelta - 1.0F) * 0.25F * (float) Math.PI);
    }


    @Environment(EnvType.CLIENT)
    public static class LaserFlashFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public LaserFlashFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            LaserFlashParticle flash = new LaserFlashParticle(clientWorld, d, e, f);
            flash.setSprite(this.spriteProvider);
            return flash;
        }
    }

}
