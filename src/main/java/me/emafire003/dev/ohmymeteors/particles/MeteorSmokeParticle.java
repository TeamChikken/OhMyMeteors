package me.emafire003.dev.ohmymeteors.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class MeteorSmokeParticle extends TextureSheetParticle {
    MeteorSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, boolean signal) {
        super(level, x, y, z);
        this.scale(3.0F);
        this.setSize(0.25F, 0.25F);
        if (signal) {
            this.lifetime = this.random.nextInt(50) + 280;
        } else {
            this.lifetime = this.random.nextInt(50) + 80;
        }

        this.gravity = 3.0E-6F;
        this.xd = xSpeed;
        this.yd = ySpeed + this.random.nextFloat() / 500.0F;
        this.zd = zSpeed;
    }

    private static final Vec3 startColor =  new Vec3(145, 42, 4); //912004// new Vec3(150, 37,6); //"962506";//new Vec3(232,79,18);//e84f12
    private static final Vec3 endColor =  new Vec3(242,219,92); //f2db5c // new Vec3(204, 65, 12); //cca50c //new Vec3(201, 134, 62); //c9863e

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            this.xd = this.xd + this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
            this.zd = this.zd + this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
            this.yd = this.yd - this.gravity;

            this.move(this.xd, this.yd, this.zd);
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }

            this.rCol = (float) Mth.lerp((float) this.age /this.lifetime*2, startColor.x()/255, endColor.x()/255);
            this.gCol = (float) Mth.lerp((float) this.age /this.lifetime*2, startColor.y()/255, endColor.y()/255);
            this.bCol = (float) Mth.lerp((float) this.age /this.lifetime*2, startColor.z()/255, endColor.z()/255);

        } else {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class CosyProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public CosyProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            MeteorSmokeParticle MeteorSmokeParticle = new MeteorSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, false);
            MeteorSmokeParticle.setAlpha(0.93F);
            MeteorSmokeParticle.pickSprite(this.sprites);
            return MeteorSmokeParticle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class SignalProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SignalProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            MeteorSmokeParticle MeteorSmokeParticle = new MeteorSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, true);
            MeteorSmokeParticle.setAlpha(0.95F);
            MeteorSmokeParticle.pickSprite(this.sprites);
            return MeteorSmokeParticle;
        }
    }
}
