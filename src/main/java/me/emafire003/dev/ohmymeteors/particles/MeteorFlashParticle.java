package me.emafire003.dev.ohmymeteors.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class MeteorFlashParticle<T extends MeteorFlashScaleParticleOptions> extends TextureSheetParticle {

    private final MeteorFlashScaleParticleOptions options;

    MeteorFlashParticle(ClientLevel clientWorld, double d, double e, double f, MeteorFlashScaleParticleOptions options) {
        super(clientWorld, d, e, f);
        this.lifetime = 4;
        this.options = options;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.setAlpha(0.6F - (this.age + tickDelta - 1.0F) * 0.25F * 0.5F);
        super.render(vertexConsumer, camera, tickDelta);
    }

    @Override
    public float getQuadSize(float tickDelta) {
        //TODO find a way to set the value when spawning the particle
        //ParticleTypes
        return options.getScale() * Mth.sin((this.age + tickDelta - 1.0F) * 0.25F * (float) Math.PI);
        //return 2.1F * Mth.sin((this.age + tickDelta - 1.0F) * 0.25F * (float) Math.PI);
    }


    @Environment(EnvType.CLIENT)
    public static class LaserFlashFactory implements ParticleProvider<MeteorFlashScaleParticleOptions> {
        private final SpriteSet spriteProvider;

        public LaserFlashFactory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(MeteorFlashScaleParticleOptions type, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            MeteorFlashParticle<?> flash = new MeteorFlashParticle<>(clientWorld, d, e, f, type);
            flash.pickSprite(this.spriteProvider);
            return flash;
        }
    }

}
