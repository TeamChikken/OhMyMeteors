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

        Identifier texture;

        switch (OhMyMeteors.CONFIG.visualsSection.meteor_texture_mode){
            case HOT -> texture =
                    (OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png"));
            case MID -> texture =
                    (OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png"));
            case DYNAMIC_HEIGHT -> texture = textureByHeight(state);
            case DYNAMIC_DISTANCE -> texture = textureByDistance(state);
            case DYNAMIC_AUTO -> {
                if(Math.abs(state.velocity.x()) > 0.85 || Math.abs(state.velocity.z()) > 0.85){
                    texture = textureByDistance(state);
                }else{
                    texture = textureByHeight(state);
                }
            }
            default -> texture =
                    (OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png"));

        }

        submitNodeCollector.order(0)
                .submitModel(this.model,
                        state,
                        poseStack,
                        texture, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        /*TODO this could be used to render meteors as enchanted. May be useful later?
        submitNodeCollector.order(1)
                .submitModel(
                        this.model,
                        state,
                        poseStack,
                        ItemFeatureRenderer.getFoilRenderType((TEXTURE), false),
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
                        ItemFeatureRenderer.getFoilRenderType((TEXTURE), true),
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY,
                        state.outlineColor,
                        null
                );*/
        /*List<RenderType> list = ItemFeatureRenderer.gettFoilBuffer((TEXTURE), false, false);

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

    public Identifier textureByHeight(MeteorProjectileRenderState state){
        Identifier texture;
        if(state.y < state.moltenPos){
            texture =
                    (OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png"));
        }else if(state.y < state.midPos){
            texture =
                    (OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png"));
        }else{
            texture =
                    (OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png"));
        }
        return texture;
    }

    //TODO add travel time option as well? Maybe?
    public Identifier textureByDistance(MeteorProjectileRenderState state){
        Identifier texture;
        if(state.travelledBlocks > OhMyMeteors.CONFIG.visualsSection.texture_change_distance_hot+OhMyMeteors.CONFIG.visualsSection.texture_change_distance_mid){
            texture =
                    (OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png"));
        }else if(state.travelledBlocks > OhMyMeteors.CONFIG.visualsSection.texture_change_distance_mid){
            texture =
                    (OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png"));
        }else{
            texture =
                    (OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png"));
        }
        return texture;
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