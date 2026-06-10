package me.emafire003.dev.ohmymeteors.entities.client;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

import java.util.List;

public class MeteorProjectileEntityRenderer<T  extends MeteorProjectileEntity> extends EntityRenderer<T, MeteorProjectileRenderState> {
    protected MeteorProjectileEntityModel<?> model;

    public static final Identifier TEXTURE_NORMAL = OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png");

    public MeteorProjectileEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new MeteorProjectileEntityModel<>(ctx.bakeLayer(MeteorProjectileEntityModel.METEOR));
    }

    @Override
    public void submit(MeteorProjectileRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.translate(0, -state.boundingBoxHeight /1.5, 0);
        matrices.scale(state.size, state.size, state.size);
        model.setupAnim(state);

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
        
        for (int i = 0; i < list.size(); i++) {
            queue.order(i)
                    .submitModel(
                            this.model,
                            state,
                            matrices,
                            list.get(i),
                            state.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            state.outlineColor,
                            null
                    );
        }

        /*TODO make this work
        VertexConsumer renderType;

        switch (OhMyMeteors.CONFIG.visualsSection.meteor_texture_mode){
            case HOT -> renderType = 
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png"));
            case MID -> renderType = 
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png"));
            case DYNAMIC_HEIGHT -> renderType = textureByHeight(entity, vertexConsumers);
            case DYNAMIC_DISTANCE -> renderType = textureByDistance(entity, vertexConsumers);
            case DYNAMIC_AUTO -> {
                if(Math.abs(entity.getDeltaMovement().x()) > 0.85 || Math.abs(entity.getDeltaMovement().z()) > 0.85){
                    renderType = textureByDistance(entity, vertexConsumers);
                }else{
                    renderType = textureByHeight(entity, vertexConsumers);
                }
            }
            default -> renderType = 
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png"));

        }

        matrices.translate(0, -entity.getDimensions(entity.getPose()).height()/1.5, 0);

        * */


        matrices.popPose();
        super.submit(state, matrices, queue, cameraState);

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