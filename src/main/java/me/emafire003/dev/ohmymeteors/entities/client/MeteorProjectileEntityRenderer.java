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

public class MeteorProjectileEntityRenderer extends EntityRenderer<MeteorProjectileEntity> {
    protected MeteorProjectileEntityModel model;

    public MeteorProjectileEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new MeteorProjectileEntityModel(ctx.bakeLayer(MeteorProjectileEntityModel.METEOR));
    }

    @Override
    public void render(MeteorProjectileEntity entity, float yaw, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light) {
        /*
        matrices.push();

        if(!entity.isGrounded()) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw())));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getRenderingRotation() * 5f));
            matrices.translate(0, -1.0f, 0);
        } else {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.groundedOffset.getY()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.groundedOffset.getX()));
            matrices.translate(0, -1.0f, 0);
        }

        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
                this.model.getLayer(Identifier.of(TutorialMod.MOD_ID, "textures/entity/tomahawk/tomahawk.png")), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();*/

        matrices.pushPose();
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png")), false, false);

        matrices.translate(0, -entity.getDimensions(entity.getPose()).height/1.5, 0);

        matrices.scale(entity.getSize(), entity.getSize(), entity.getSize());

        this.model.renderToBuffer(matrices, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        //this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.popPose();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    /*protected void scale(MeteorProjectileEntity entity, MatrixStack matrixStack, float f) {
        float g = 0.999F;
        matrixStack.scale(0.999F, 0.999F, 0.999F);
        matrixStack.translate(0.0F, 0.001F, 0.0F);
        float h = entity.getSize();
        float i = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch) / (h * 0.5F + 1.0F);
        float j = 1.0F / (i + 1.0F);
        matrixStack.scale(j * h, 1.0F / j * h, j * h);
    }*/

    @Override
    public ResourceLocation getTextureLocation(MeteorProjectileEntity entity) {
        return OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png");
    }
}