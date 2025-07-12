package me.emafire003.dev.ohmymeteors.entities.client;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.CatEntityRenderState;
import net.minecraft.entity.passive.CatEntity;

public class MeteorCatEntityRenderer extends CatEntityRenderer {
    public MeteorCatEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void updateRenderState(CatEntity catEntity, CatEntityRenderState catEntityRenderState, float f) {
        super.updateRenderState(catEntity, catEntityRenderState, f);
        catEntityRenderState.texture = OhMyMeteors.getIdentifier("textures/entity/meteor_cat.png");
        catEntityRenderState.inSneakingPose = catEntity.isInSneakingPose();
        catEntityRenderState.sprinting = catEntity.isSprinting();
        catEntityRenderState.inSittingPose = catEntity.isInSittingPose();
        catEntityRenderState.sleepAnimationProgress = catEntity.getSleepAnimationProgress(f);
        catEntityRenderState.tailCurlAnimationProgress = catEntity.getTailCurlAnimationProgress(f);
        catEntityRenderState.headDownAnimationProgress = catEntity.getHeadDownAnimationProgress(f);
        catEntityRenderState.nearSleepingPlayer = catEntity.isNearSleepingPlayer();
        catEntityRenderState.collarColor = catEntity.isTamed() ? catEntity.getCollarColor() : null;
    }
}
