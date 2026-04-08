package me.emafire003.dev.ohmymeteors.particles.meteor_smoke;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

public class MeteorSmokeParticle<T extends MeteorSmokeScaledOptions>  extends SingleQuadParticle {
    private boolean red = true;

    MeteorSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, boolean signal, TextureAtlasSprite sprite, MeteorSmokeScaledOptions options) {
        super(level, x, y, z, sprite);
        this.scale(options.getScale());
        this.setSize(0.25F, 0.25F);
        if (signal) {
            this.lifetime = this.random.nextInt(50) + 280;
        } else {
            this.lifetime = this.random.nextInt(50) + 80;
        }
        if(this.random.nextInt(3) == 2){
            this.red = false;
        }

        this.gravity = 3.0E-6F;
        this.xd = xSpeed;
        this.yd = ySpeed + this.random.nextFloat() / 500.0F;
        this.zd = zSpeed;
    }
    //new Vec3(145, 42, 4)
    private static final Vector3f startRedColor = new Vector3f(137, 28, 1);//891c01// new Vec3(114, 25, 1); //912004// new Vec3(150, 37,6); //"962506";//new Vec3(232,79,18);//e84f12
    private static final Vector3f endRedColor = new Vector3f(204, 65, 12); //cca50c  // new Vec3(242,219,92); //f2db5c  //new Vec3(201, 134, 62); //c9863e

    private static final Vector3f startOrangeColor = new Vector3f(209, 100, 6); //d16406//new Vector3f(127,66,14); //7f420e
    private static final Vector3f endOrangeColor = new Vector3f(191, 149, 24);// bf9518//new Vector3f(226, 21,19); //e2a11d

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

            Vector3f transition;
            if(red){
                transition = new Vector3f(startRedColor).lerp(endRedColor, (float) this.age /this.lifetime);
            }else{
                transition = new Vector3f(startOrangeColor).lerp(endOrangeColor, (float) this.age /this.lifetime);
            }
            this.rCol = transition.x/255; this.gCol = transition.y/255; this.bCol = transition.z/255;

        } else {
            this.remove();
        }
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class CosyProvider implements ParticleProvider<MeteorSmokeScaledOptions> {
        private final SpriteSet sprites;

        public CosyProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(
                MeteorSmokeScaledOptions type, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, RandomSource randomSource
        ) {
            MeteorSmokeParticle<?> campfireSmokeParticle = new MeteorSmokeParticle<>(clientLevel, d, e, f, g, h, i, false, this.sprites.get(randomSource), type);
            campfireSmokeParticle.setAlpha(0.9F);
            return campfireSmokeParticle;
        }
    }
}