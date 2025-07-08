package me.emafire003.dev.ohmymeteors.mixin;

import me.emafire003.dev.ohmymeteors.entities.client.MeteorCatEntityModel;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorCatEntityRenderer;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorCatHelmetFeatureRenderer;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatEntityRenderer.class)
public abstract class MeteorCatHelmetRendererMixin extends MobEntityRenderer<CatEntity, CatEntityModel<CatEntity>> {

    public MeteorCatHelmetRendererMixin(EntityRendererFactory.Context context, CatEntityModel<CatEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void injectHelmetFeature(EntityRendererFactory.Context context, CallbackInfo ci){
        if(((CatEntityRenderer)(Object)this) instanceof MeteorCatEntityRenderer){
            this.addFeature(new MeteorCatHelmetFeatureRenderer(this, context.getModelLoader()));
        }
    }
}
