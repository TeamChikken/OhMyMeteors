package me.emafire003.dev.ohmymeteors.entities.client.idkstuff;

import me.emafire003.dev.ohmymeteors.entities.MeteorCatEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MeteorCatCollarFeatureRenderer extends FeatureRenderer<MeteorCatEntity, MeteorCatEntityModelold<MeteorCatEntity>> {
    private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/cat/cat_collar.png");
    private final MeteorCatEntityModelold<MeteorCatEntity> model;

    public MeteorCatCollarFeatureRenderer(FeatureRendererContext<MeteorCatEntity, MeteorCatEntityModelold<MeteorCatEntity>> context, EntityModelLoader loader) {
        super(context);
        this.model = new MeteorCatEntityModelold<>(loader.getModelPart(EntityModelLayers.CAT_COLLAR));
    }

    public void render(
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, MeteorCatEntity catEntity, float f, float g, float h, float j, float k, float l
    ) {
        if (catEntity.isTamed()) {
            int m = catEntity.getCollarColor().getEntityColor();
            render(this.getContextModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, catEntity, f, g, j, k, l, h, m);
        }
    }
}


