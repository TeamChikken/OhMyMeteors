package me.emafire003.dev.ohmymeteors;

import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorCatEntityRenderer;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityModel;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityRenderer;
import me.emafire003.dev.ohmymeteors.particles.LaserFlashParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticleSmall;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import me.emafire003.dev.ohmymeteors.particles.meteor_flash.MeteorFlashParticle;
import me.emafire003.dev.ohmymeteors.particles.meteor_smoke.MeteorSmokeParticle;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@Mod(value = OhMyMeteors.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = OhMyMeteors.MOD_ID, value = Dist.CLIENT)
public class OhMyMeteorsClient{

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        registerEntityStuff();
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(OMMParticles.LASER_PARTICLE.get(), LaserParticle.EggCrackFactory::new);
        event.registerSpriteSet(OMMParticles.LASER_PARTICLE_SMALL.get(), LaserParticleSmall.EggCrackFactory::new);
        event.registerSpriteSet(OMMParticles.LASER_FLASH_PARTICLE.get(), LaserFlashParticle.LaserFlashFactory::new);
        event.registerSpriteSet(OMMParticles.METEOR_SMOKE_COSY.get(), MeteorSmokeParticle.CosyProvider::new);
        event.registerSpriteSet(OMMParticles.METEOR_FLASH.get(), MeteorFlashParticle.FlashFactory::new);
    }

    public static void registerEntityStuff(){
        EntityRenderers.register(OMMEntities.METEOR_PROJECTILE_ENTITY.get(), MeteorProjectileEntityRenderer::new);
        EntityRenderers.register(OMMEntities.METEOR_KITTY_CAT.get(), MeteorCatEntityRenderer::new);

    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(MeteorProjectileEntityModel.METEOR, MeteorProjectileEntityModel::getTexturedModelData);
    }

}
