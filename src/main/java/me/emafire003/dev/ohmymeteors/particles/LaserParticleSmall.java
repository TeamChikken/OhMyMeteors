package me.emafire003.dev.ohmymeteors.particles;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;

public class LaserParticleSmall extends LaserParticle{
    LaserParticleSmall(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e, f, g, h, i, sprite);
        this.scale = this.scale * (this.random.nextFloat() * 0.2F);
    }
}
