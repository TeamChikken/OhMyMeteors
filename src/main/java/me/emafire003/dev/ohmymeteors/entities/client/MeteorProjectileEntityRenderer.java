package me.emafire003.dev.ohmymeteors.entities.client;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MeteorProjectileEntityRenderer<T  extends MeteorProjectileEntity> extends EntityRenderer<T> {
    protected MeteorProjectileEntityModel<?> model;

    public MeteorProjectileEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new MeteorProjectileEntityModel<>(ctx.bakeLayer(MeteorProjectileEntityModel.METEOR));
    }


    @Override
    public void render(T entity, float yaw, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light) {

        matrices.pushPose();

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

        matrices.scale(entity.getSize(), entity.getSize(), entity.getSize());

        //yay it finally works, it needed the entity tickCount as well :D
        model.setupAnim(entity, 0f, 0f, entity.tickCount + tickDelta, 0f, 0f);

        this.model.renderToBuffer(matrices, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
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
    public @NotNull ResourceLocation getTextureLocation(T entity) {
        //OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png")
        return OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid_static.png");
    }
}