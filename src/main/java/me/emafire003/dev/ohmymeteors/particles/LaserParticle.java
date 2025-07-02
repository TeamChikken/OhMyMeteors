package me.emafire003.dev.ohmymeteors.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class LaserParticle extends SpriteBillboardParticle {
    
    LaserParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        float j = this.random.nextFloat() * 0.1F + 0.2F;
        this.red = j;
        this.green = j;
        this.blue = j;
        this.setBoundingBoxSpacing(0.02F, 0.02F);
        this.scale = this.scale * (this.random.nextFloat() * 0.5F + 0.5F);
        this.velocityX *= 0.02F;
        this.velocityY *= 0.02F;
        this.velocityZ *= 0.02F;
        this.maxAge = 1*20; //aka 1 second
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
        } else {
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            this.velocityX *= 0.99;
            this.velocityY *= 0.99;
            this.velocityZ *= 0.99;
        }
    }

    /*@Environment(EnvType.CLIENT)
    public static class DolphinFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public DolphinFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            LaserParticle suspendParticle = new LaserParticle(clientWorld, d, e, f, g, h, i);
            suspendParticle.setColor(0.3F, 0.5F, 1.0F);
            suspendParticle.setSprite(this.spriteProvider);
            suspendParticle.setAlpha(1.0F - clientWorld.random.nextFloat() * 0.7F);
            suspendParticle.setMaxAge(suspendParticle.getMaxAge() / 2);
            return suspendParticle;
        }
    }*/

    @Environment(EnvType.CLIENT)
    public static class EggCrackFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public EggCrackFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            LaserParticle suspendParticle = new LaserParticle(clientWorld, d, e, f, g, h, i);
            suspendParticle.setSprite(this.spriteProvider);
            suspendParticle.setColor(1.0F, 1.0F, 1.0F);
            return suspendParticle;
        }
    }

    /*@Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            LaserParticle suspendParticle = new LaserParticle(clientWorld, d, e, f, g, h, i);
            suspendParticle.setSprite(this.spriteProvider);
            suspendParticle.setColor(1.0F, 1.0F, 1.0F);
            suspendParticle.setMaxAge(3 + clientWorld.getRandom().nextInt(5));
            return suspendParticle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class HappyVillagerFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public HappyVillagerFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            LaserParticle suspendParticle = new LaserParticle(clientWorld, d, e, f, g, h, i);
            suspendParticle.setSprite(this.spriteProvider);
            suspendParticle.setColor(1.0F, 1.0F, 1.0F);
            return suspendParticle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class MyceliumFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public MyceliumFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            LaserParticle suspendParticle = new LaserParticle(clientWorld, d, e, f, g, h, i);
            suspendParticle.setSprite(this.spriteProvider);
            return suspendParticle;
        }
    }*/
}
