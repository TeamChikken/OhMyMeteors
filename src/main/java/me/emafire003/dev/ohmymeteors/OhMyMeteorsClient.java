package me.emafire003.dev.ohmymeteors;

import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorCatEntityRenderer;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityModel;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityRenderer;
import me.emafire003.dev.ohmymeteors.particles.LaserFlashParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticleSmall;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = OhMyMeteors.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@Mod.EventBusSubscriber(modid = OhMyMeteors.MOD_ID, value = Dist.CLIENT)
public class OhMyMeteorsClient{


    /*public void registerParticles(){
        ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_PARTICLE, LaserParticle.EggCrackFactory::new);
        ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_PARTICLE_SMALL, LaserParticleSmall.EggCrackFactory::new);
        ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_FLASH_PARTICLE, LaserFlashParticle.LaserFlashFactory::new);
    }*/

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        registerEntityStuff();
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(OMMParticles.LASER_PARTICLE.get(), LaserParticle.EggCrackFactory::new);
        event.registerSpriteSet(OMMParticles.LASER_PARTICLE_SMALL.get(), LaserParticleSmall.EggCrackFactory::new);
        event.registerSpriteSet(OMMParticles.LASER_FLASH_PARTICLE.get(), LaserFlashParticle.LaserFlashFactory::new);
    }

    public static void registerEntityStuff(){
        EntityRenderers.register(OMMEntities.METEOR_PROJECTILE_ENTITY.get(), MeteorProjectileEntityRenderer::new);
        EntityRenderers.register(OMMEntities.METEOR_KITTY_CAT.get(), MeteorCatEntityRenderer::new);

        /*EntityModelLayerRegistry.registerModelLayer(MeteorProjectileEntityModel.METEOR, MeteorProjectileEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(OMMEntities.METEOR_PROJECTILE_ENTITY, MeteorProjectileEntityRenderer::new);
        //EntityModelLayerRegistry.registerModelLayer(MeteorCatEntityModel., MeteorProjectileEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(OMMEntities.METEOR_KITTY_CAT, MeteorCatEntityRenderer::new);*/

    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(MeteorProjectileEntityModel.METEOR, MeteorProjectileEntityModel::getTexturedModelData);
    }

    /*
    public static void registerBlockStuff(){
        BlockRenderLayerMap.INSTANCE.putBlock(OMMBlocks.BASIC_METEOR_LASER, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(OMMBlocks.ADVANCED_METEOR_LASER, RenderType.translucent());
    }*/

}
