package me.emafire003.dev.ohmymeteors.entities.client;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

public class MeteorProjectileEntityRenderer<T  extends MeteorProjectileEntity> extends EntityRenderer<T, MeteorProjectileRenderState> {
    protected MeteorProjectileEntityModel<?> model;

    public static final Identifier TEXTURE_NORMAL = OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png");

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

        /*
        TODO MAKE this work
       RenderType renderType = this.model.renderType(TEXTURE_NORMAL);
        switch (OhMyMeteors.CONFIG.visualsSection.meteor_texture_mode){
            case HOT -> renderType =
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png"));
            case MID -> renderType =
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png"));
            case DYNAMIC_HEIGHT -> renderType = textureByHeight(state);
            case DYNAMIC_DISTANCE -> renderType = textureByDistance(state);
            case DYNAMIC_AUTO -> {
                if(Math.abs(state.velocity.x()) > 0.85 || Math.abs(state.velocity.z()) > 0.85){
                    renderType = textureByDistance(state);
                }else{
                    renderType = textureByHeight(state);
                }
            }
            default -> renderType =
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png"));

        }
        List<RenderType> list = ItemRenderer.getFoilRenderTypes(renderType, false, false);

         */

        submitNodeCollector.order(0)
                .submitModel(this.model,
                        state,
                        poseStack,
                        TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        /*TODO this could be used to render meteors as enchanted. May be useful later?
        submitNodeCollector.order(1)
                .submitModel(
                        this.model,
                        state,
                        poseStack,
                        ItemFeatureRenderer.getFoilRenderType(this.model.renderType(TEXTURE), false),
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        state.outlineColor,
                        null
                );*/

/*
        submitNodeCollector.order(0)
                .submitModel(
                        this.model,
                        state,
                        poseStack,
                        ItemFeatureRenderer.getFoilRenderType(this.model.renderType(TEXTURE), true),
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        state.outlineColor,
                        null
                );*/
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

    public RenderType textureByHeight(MeteorProjectileRenderState state){
        RenderType renderType;
        if(state.y < state.moltenPos){
            renderType =
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png"));
        }else if(state.y < state.midPos){
            renderType =
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png"));
        }else{
            renderType =
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png"));
        }
        return renderType;
    }

    //TODO add travel time option as well? Maybe?
    public RenderType textureByDistance(MeteorProjectileRenderState state){
        RenderType renderType;
        if(state.travelledBlocks > OhMyMeteors.CONFIG.visualsSection.texture_change_distance_hot+OhMyMeteors.CONFIG.visualsSection.texture_change_distance_mid){
            renderType =
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png"));
        }else if(state.travelledBlocks > OhMyMeteors.CONFIG.visualsSection.texture_change_distance_mid){
            renderType =
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png"));
        }else{
            renderType =
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png"));
        }
        return renderType;
    }

    @Override
    public MeteorProjectileRenderState createRenderState() {
        return new MeteorProjectileRenderState();
    }

    public void extractRenderState(T meteorEntity, MeteorProjectileRenderState meteorState, float f) {
        super.extractRenderState(meteorEntity, meteorState, f);
        meteorState.size = meteorEntity.getSize();
        meteorState.rotationState.copyFrom(meteorEntity.rotationState);
        meteorState.velocity = meteorEntity.getDeltaMovement();

        meteorState.groundLevel = meteorEntity.groundLevel;
        meteorState.lastPos = meteorEntity.lastPos;
        meteorState.midPos = meteorEntity.midPos;
        meteorState.moltenPos = meteorEntity.moltenPos;
    }
}