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
        model.setupAnim(state);
        //matrices.translate(0, -state.boundingBoxHeight /1.5, 0); //TODO why did they fucking chance the pivot?
        matrices.scale(state.size, state.size, state.size);


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

    @Override
    public MeteorProjectileRenderState createRenderState() {
        return new MeteorProjectileRenderState();
    }

    public void extractRenderState(T meteorEntity, MeteorProjectileRenderState meteorState, float f) {
        super.extractRenderState(meteorEntity, meteorState, f);
        meteorState.size = meteorEntity.getSize();
        //meteorState.rotationState.copyFrom(meteorEntity.rotationState);
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

    ///  //////////////
    /*
    * @Override
    public void render(T entity, float yaw, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light) {

        matrices.pushPose();

        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(vertexConsumers,
                this.model.renderType(OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png")), false, false);

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
        return OhMyMeteors.getIdentifier("textures/block/meteoric_rock.png");
    }*/
}