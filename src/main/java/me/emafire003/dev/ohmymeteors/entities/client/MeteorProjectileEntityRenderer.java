package me.emafire003.dev.ohmymeteors.entities.client;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

public class MeteorProjectileEntityRenderer<T  extends MeteorProjectileEntity> extends EntityRenderer<T, MeteorProjectileRenderState> {
    protected MeteorProjectileEntityModel<?> model;

    public static final Identifier TEXTURE = OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png");

    public MeteorProjectileEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new MeteorProjectileEntityModel<>(ctx.bakeLayer(MeteorProjectileEntityModel.METEOR));
    }

    @Override
    public void submit(MeteorProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        
        poseStack.pushPose();
        poseStack.translate(0, -state.boundingBoxHeight /1.5, 0);
        poseStack.scale(state.size, state.size, state.size);
        model.setupAnim(state);

        submitNodeCollector.order(0)
                .submitModel(
                        this.model,
                        state,
                        poseStack,
                        ItemFeatureRenderer.getFoilRenderType(this.model.renderType(TEXTURE), false),
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        state.outlineColor,
                        null
                );
        /*List<RenderType> list = ItemFeatureRenderer.gettFoilBuffer(this.model.renderType(TEXTURE), false, false);

        for (int i = 0; i < list.size(); i++) {
            submitNodeCollector.order(i)
                    .submitModel(
                            this.model,
                            state,
                            poseStack,
                            list.get(i),
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            state.outlineColor,
                            null
                    );
        }*/


        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public MeteorProjectileRenderState createRenderState() {
        return new MeteorProjectileRenderState();
    }

    public void extractRenderState(T meteorEntity, MeteorProjectileRenderState meteorState, float f) {
        super.extractRenderState(meteorEntity, meteorState, f);
        meteorState.size = meteorEntity.getSize();
        meteorState.rotationState.copyFrom(meteorEntity.rotationState);
    }
}