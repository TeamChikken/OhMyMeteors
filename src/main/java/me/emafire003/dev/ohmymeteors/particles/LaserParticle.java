package me.emafire003.dev.ohmymeteors.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class LaserParticle extends SingleQuadParticle {
    
    LaserParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, TextureAtlasSprite sprite) {
        super(clientWorld, d, e, f, g, h, i, sprite);
        float j = this.random.nextFloat() * 0.1F + 0.2F;
        this.rCol = j;
        this.gCol = j;
        this.bCol = j;
        this.setSize(0.02F, 0.02F);
        this.quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.5F);
        this.xd *= 0.02F;
        this.yd *= 0.02F;
        this.zd *= 0.02F;
        this.lifetime = (int)(20.0 / (Math.random() * 0.8 + 0.2));
    }/*(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e,f,g, h, i, sprite);
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
    }*/

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

   /* @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }*/

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
        this.setLocationFromBoundingbox();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.99;
            this.yd *= 0.99;
            this.zd *= 0.99;
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

/*    @Environment(EnvType.CLIENT)
    public static class EggCrackFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public EggCrackFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            LaserParticle suspendParticle = new LaserParticle(clientWorld, d, e, f, g, h, i);
            suspendParticle.setSprite(this.spriteProvider);
            suspendParticle.setColor(1.0F, 1.0F, 1.0F);
            return suspendParticle;
        }
    }*/

    @Environment(EnvType.CLIENT)
    public static class EggCrackFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public EggCrackFactory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(
                SimpleParticleType simpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, RandomSource random
        ) {
            LaserParticle suspendParticle = new LaserParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.get(random));
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
