package me.emafire003.dev.ohmymeteors.particles;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;

public class LaserParticleSmall extends LaserParticle{
    LaserParticleSmall(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, TextureAtlasSprite sprite) {
        super(clientWorld, d, e, f, g, h, i, sprite);
        this.quadSize = this.quadSize * (this.random.nextFloat() * 0.2F);
    }
}
