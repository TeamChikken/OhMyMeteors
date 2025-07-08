package me.emafire003.dev.ohmymeteors.entities.client;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.client.render.VertexConsumerProvider;

import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;

public class MeteorCatHelmetFeatureRenderer extends FeatureRenderer<CatEntity, CatEntityModel<CatEntity>> {
    private static final Identifier SKIN = OhMyMeteors.getIdentifier("textures/entity/meteor_cat.png");
    private final CatEntityModel<CatEntity> model;

    public MeteorCatHelmetFeatureRenderer(FeatureRendererContext<CatEntity, CatEntityModel<CatEntity>> context, EntityModelLoader loader) {
        super(context);
        this.model = new CatEntityModel<>(loader.getModelPart(MeteorCatHelmetFeatureModel.METEOR_CAT_HELMET));
    }


    public void render(
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CatEntity catEntity, float f, float g, float h, float j, float k, float l
    ) {
        if (catEntity.isTamed()) {
            int m = catEntity.getCollarColor().getEntityColor();
            render(this.getContextModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, catEntity, f, g, j, k, l, h, m);
        }
    }
}