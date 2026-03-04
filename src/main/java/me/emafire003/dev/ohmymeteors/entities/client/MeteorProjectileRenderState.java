package me.emafire003.dev.ohmymeteors.entities.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class MeteorProjectileRenderState extends EntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public int size = 1;
}
