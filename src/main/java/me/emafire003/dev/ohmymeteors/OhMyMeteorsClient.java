package me.emafire003.dev.ohmymeteors;

import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorCatEntityRenderer;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityModel;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityRenderer;
import me.emafire003.dev.ohmymeteors.entities.client.OMMModelLayers;
import me.emafire003.dev.ohmymeteors.particles.LaserParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticleSmall;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import me.emafire003.dev.ohmymeteors.particles.meteor_flash.MeteorFlashParticle;
import me.emafire003.dev.ohmymeteors.particles.meteor_smoke.MeteorSmokeParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class OhMyMeteorsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        registerEntityStuff();
        //registerBlockStuff();
        registerParticles();
        OMMModelLayers.registerModelLayers();
    }

    public void registerParticles(){
        ParticleProviderRegistry.getInstance().register(OMMParticles.LASER_PARTICLE, LaserParticle.EggCrackFactory::new);
        ParticleProviderRegistry.getInstance().register(OMMParticles.LASER_PARTICLE_SMALL, LaserParticleSmall.EggCrackFactory::new);
        //ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_FLASH_PARTICLE, LaserFlashParticle.LaserFlashFactory::new);
        ParticleProviderRegistry.getInstance().register(OMMParticles.METEOR_SMOKE_COSY, MeteorSmokeParticle.CosyProvider::new);
        ParticleProviderRegistry.getInstance().register(OMMParticles.METEOR_FLASH, MeteorFlashParticle.LaserFlashFactory::new);

    }

    public static void registerEntityStuff(){
        ModelLayerRegistry.registerModelLayer(MeteorProjectileEntityModel.METEOR, MeteorProjectileEntityModel::getTexturedModelData);
        EntityRenderers.register(OMMEntities.METEOR_PROJECTILE_ENTITY, MeteorProjectileEntityRenderer::new);
        EntityRenderers.register(OMMEntities.METEOR_KITTY_CAT, MeteorCatEntityRenderer::new);
    }

}
