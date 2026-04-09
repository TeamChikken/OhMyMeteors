package me.emafire003.dev.ohmymeteors;

import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorCatEntityRenderer;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityModel;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityRenderer;
import me.emafire003.dev.ohmymeteors.particles.LaserParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticleSmall;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import me.emafire003.dev.ohmymeteors.particles.meteor_flash.MeteorFlashParticle;
import me.emafire003.dev.ohmymeteors.particles.meteor_smoke.MeteorSmokeParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class OhMyMeteorsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        registerEntityStuff();
        //registerBlockStuff();
        registerParticles();
    }

    public void registerParticles(){
        ParticleProviderRegistry.getInstance().register(OMMParticles.LASER_PARTICLE, LaserParticle.EggCrackFactory::new);
        ParticleProviderRegistry.getInstance().register(OMMParticles.LASER_PARTICLE_SMALL, LaserParticleSmall.EggCrackFactory::new);
        //ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_FLASH_PARTICLE, LaserFlashParticle.LaserFlashFactory::new);
        ParticleProviderRegistry.getInstance().register(OMMParticles.METEOR_SMOKE_COSY, MeteorSmokeParticle.CosyProvider::new);
        //ParticleFactoryRegistry.getInstance().register(OMMParticles.METEOR_SMOKE_SIGNAL, MeteorSmokeParticle.SignalProvider::new);
        ParticleProviderRegistry.getInstance().register(OMMParticles.METEOR_FLASH, MeteorFlashParticle.LaserFlashFactory::new);

    }

    public static void registerEntityStuff(){
        ModelLayerRegistry.registerModelLayer(MeteorProjectileEntityModel.METEOR, MeteorProjectileEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(OMMEntities.METEOR_PROJECTILE_ENTITY, MeteorProjectileEntityRenderer::new);
        //EntityModelLayerRegistry.registerModelLayer(MeteorCatEntityModel., MeteorProjectileEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(OMMEntities.METEOR_KITTY_CAT, MeteorCatEntityRenderer::new);

    }

    //TODO should not be neded anymore
    public static void registerBlockStuff(){
        //ChunkSectionLayerMap.putBlock(OMMBlocks.BASIC_METEOR_LASER, ChunkSectionLayer.TRANSLUCENT);
        //ChunkSectionLayerMap.putBlock(OMMBlocks.ADVANCED_METEOR_LASER, ChunkSectionLayer.TRANSLUCENT);
    }

}
