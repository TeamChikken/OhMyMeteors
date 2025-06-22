package me.emafire003.dev.ohmymeteors.entities.client;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.12.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

public class MeteorProjectileEntityModel extends EntityModel<MeteorProjectileEntity> {
	private final ModelPart main;
	public static final EntityModelLayer METEOR = new EntityModelLayer(OhMyMeteors.getIdentifier("meteor_projectile"), "main");

	public MeteorProjectileEntityModel(ModelPart root) {
		this.main = root.getChild("main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData cube_r1 = main.addChild("cube_r1", ModelPartBuilder.create().uv(32, 0).cuboid(3.0F, -12.0F, -1.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(-9.0F, -5.0F, -8.0F, -0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r2 = main.addChild("cube_r2", ModelPartBuilder.create().uv(0, 0).cuboid(3.0F, -12.0F, -1.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.of(-2.0F, 0.0F, -10.0F, 0.0F, -0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}

	@Override
	public void setAngles(MeteorProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}


	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}
}