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

    public static final Identifier TEXTURE = OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png");

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
        List<RenderType> list = ItemRenderer.getFoilRenderTypes(this.model.renderType(TEXTURE), false, false);

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
        VertexConsumer vertexconsumer;

        switch (OhMyMeteors.CONFIG.visualsSection.meteor_texture_mode){
            case HOT -> vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png")), false, false);
            case MID -> vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png")), false, false);
            case DYNAMIC_HEIGHT -> vertexconsumer = textureByHeight(entity, vertexConsumers);
            case DYNAMIC_DISTANCE -> vertexconsumer = textureByDistance(entity, vertexConsumers);
            case DYNAMIC_AUTO -> {
                if(Math.abs(entity.getDeltaMovement().x()) > 0.85 || Math.abs(entity.getDeltaMovement().z()) > 0.85){
                    vertexconsumer = textureByDistance(entity, vertexConsumers);
                }else{
                    vertexconsumer = textureByHeight(entity, vertexConsumers);
                }
            }
            default -> vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png")), false, false);

        }

        matrices.translate(0, -entity.getDimensions(entity.getPose()).height()/1.5, 0);

        * */


        matrices.popPose();
        super.submit(state, matrices, queue, cameraState);

    }

    public VertexConsumer textureByHeight(T entity, MultiBufferSource vertexConsumers){
        VertexConsumer vertexconsumer;
        if(entity.position().y() < entity.moltenPos){
            vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png")), false, false);
        }else if(entity.position().y() < entity.midPos){
            vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png")), false, false);
        }else{
            vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png")), false, false);
        }
        return vertexconsumer;
    }

    //TODO add travel time option as well? Maybe?
    public VertexConsumer textureByDistance(T entity, MultiBufferSource vertexConsumers){
        VertexConsumer vertexconsumer;
        if(entity.travelledBlocks > OhMyMeteors.CONFIG.visualsSection.texture_change_distance_hot+OhMyMeteors.CONFIG.visualsSection.texture_change_distance_mid){
            vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_hot_static.png")), false, false);
        }else if(entity.travelledBlocks > OhMyMeteors.CONFIG.visualsSection.texture_change_distance_mid){
            vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png")), false, false);
        }else{
            vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                    this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png")), false, false);
        }
        return vertexconsumer;
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