package me.emafire003.dev.ohmymeteors.entities.client;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class MeteorCatHelmetFeatureModel extends EntityModel<Entity> {
    private final ModelPart helmet;

    public static final EntityModelLayer METEOR_CAT_HELMET = new EntityModelLayer(OhMyMeteors.getIdentifier("meteor_cat_helmet"), "helmet");

    public MeteorCatHelmetFeatureModel(ModelPart root) {
        this.helmet = root.getChild("helmet");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData helmet = modelPartData.addChild("helmet", ModelPartBuilder.create().uv(38, 19).cuboid(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 15.0F, -8.0F));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        helmet.render(matrices, vertexConsumer, light, overlay, color);
    }
}
