package me.emafire003.dev.ohmymeteors.entities.client;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.world.entity.animal.feline.Cat;

public class MeteorCatEntityRenderer extends CatRenderer {
    public MeteorCatEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(Cat catEntity, CatRenderState catEntityRenderState, float f) {
        super.extractRenderState(catEntity, catEntityRenderState, f);
        catEntityRenderState.texture = OhMyMeteors.getIdentifier("textures/entity/meteor_cat.png");
        catEntityRenderState.isCrouching = catEntity.isCrouching();
        catEntityRenderState.isSprinting = catEntity.isSprinting();
        catEntityRenderState.isSitting = catEntity.isInSittingPose();
        catEntityRenderState.lieDownAmount = catEntity.getLieDownAmount(f);
        catEntityRenderState.lieDownAmountTail = catEntity.getLieDownAmountTail(f);
        catEntityRenderState.relaxStateOneAmount = catEntity.getRelaxStateOneAmount(f);
        catEntityRenderState.isLyingOnTopOfSleepingPlayer = catEntity.isLyingOnTopOfSleepingPlayer();
        catEntityRenderState.collarColor = catEntity.isTame() ? catEntity.getCollarColor() : null;
    }
}
