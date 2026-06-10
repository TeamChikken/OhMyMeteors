package me.emafire003.dev.ohmymeteors.entities.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.phys.Vec3;

public class MeteorProjectileRenderState extends EntityRenderState {
    public final AnimationState rotationState = new AnimationState();
    public int size = 1;
    public Vec3 velocity = new Vec3(0,0,0);

    public int groundLevel = -1;
    public int moltenPos = -1;
    public int midPos = -1;
    public int travelledBlocks = 0;
    public BlockPos lastPos;
}
