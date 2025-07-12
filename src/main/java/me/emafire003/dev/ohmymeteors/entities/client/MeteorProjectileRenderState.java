package me.emafire003.dev.ohmymeteors.entities.client;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.AnimationState;

public class MeteorProjectileRenderState extends EntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public int size = 1;
}
