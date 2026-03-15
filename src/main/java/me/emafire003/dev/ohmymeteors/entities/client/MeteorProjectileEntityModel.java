package me.emafire003.dev.ohmymeteors.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.client.model.*;
import net.minecraft.client.model.EntityModel;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

import static me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileAnimations.METEOR_ROTATION;

public class MeteorProjectileEntityModel<T extends MeteorProjectileEntity> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart cube_r1;
	private final ModelPart cube_r2;
	public static final ModelLayerLocation METEOR = new ModelLayerLocation(OhMyMeteors.getIdentifier("meteor_projectile"), "root");

	public MeteorProjectileEntityModel(ModelPart root) {
		this.root = root.getChild("root");
		this.cube_r1 = this.root.getChild("cube_r1");
		this.cube_r2 = this.root.getChild("cube_r2");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition root = modelPartData.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(-5.9428F, -6.0976F, -5.8926F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.001F)), PartPose.offset(-0.0572F, 18.0976F, -0.1074F));

		PartDefinition cube_r1 = root.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 0).addBox(3.0F, -12.0F, -1.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.9428F, 0.9024F, -7.8926F, -0.7854F, 0.0F, 0.0F));

		PartDefinition cube_r2 = root.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(-0.1144F, -0.0976F, 0.0069F, 0.0F, -0.7854F, 0.0F));
		return LayerDefinition.create(modelData, 16, 16);
	}

	@Override
	public void setupAnim(MeteorProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animate(entity.rotationState, METEOR_ROTATION, ageInTicks, 1f);
	}
    /*
    * @Override
	public void setupAnim(MeteorProjectileRenderState state) {
		super.setupAnim(state);
	}
	* */

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		main.render(matrices, vertexConsumer, light, overlay, color);
	}

    @Override
    public @NotNull ModelPart root() {
        return root;
    }
}