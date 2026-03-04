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

public class MeteorProjectileEntityRenderer extends EntityRenderer<MeteorProjectileEntity, MeteorProjectileRenderState> {
    protected MeteorProjectileEntityModel model;

    public static final Identifier TEXTURE = OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png");

    public MeteorProjectileEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.model = new MeteorProjectileEntityModel(ctx.bakeLayer(MeteorProjectileEntityModel.METEOR));
    }

    @Override
    public void submit(MeteorProjectileRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        matrices.pushPose();

        matrices.translate(0, -state.boundingBoxHeight /1.5, 0);

        matrices.scale(state.size, state.size, state.size);

        /*VertexConsumer vertexconsumer = ItemRenderer.getItemGlintConsumer(MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers(),
                this.model.getLayer(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png")), false, false);
*/

        // this.model.render(matrices, vertexconsumer, state.light, OverlayTexture.DEFAULT_UV);

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


        matrices.popPose();
        super.submit(state, matrices, queue, cameraState);

    }

    /*@Override
    public void render(MeteorProjectileRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);
        matrices.push();
        //TODO this does not work.
        VertexConsumer vertexconsumer = ItemRenderer.getItemGlintConsumer(vertexConsumers,
                this.model.getLayer(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png")), false, false);

        matrices.translate(0, -state.height/1.5, 0);

        matrices.scale(state.size, state.size, state.size);

        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();

    }*/

    @Override
    public MeteorProjectileRenderState createRenderState() {
        return new MeteorProjectileRenderState();
    }

    public void extractRenderState(MeteorProjectileEntity meteorEntity, MeteorProjectileRenderState slimeEntityRenderState, float f) {
        super.extractRenderState(meteorEntity, slimeEntityRenderState, f);
        slimeEntityRenderState.size = meteorEntity.getSize();
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


    /*@Override
    public Identifier getTexture(MeteorProjectileRenderState state) {
        return OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png");
    }*/
}