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

        //TODO add switcher for texture
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid.png")), false, false);

        matrices.translate(0, -entity.getDimensions(entity.getPose()).height()/1.5, 0);

        matrices.scale(entity.getSize(), entity.getSize(), entity.getSize());

        //yay it finally works, it needed the entity tickCount as well :D
        model.setupAnim(entity, 0f, 0f, entity.tickCount + tickDelta, 0f, 0f);

        this.model.renderToBuffer(matrices, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(T entity) {
        //OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png")
        return OhMyMeteors.getIdentifier("textures/block/meteoric_rock_mid.png");
    }
}