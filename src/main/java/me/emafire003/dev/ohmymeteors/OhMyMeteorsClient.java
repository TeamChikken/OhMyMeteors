package me.emafire003.dev.ohmymeteors;

import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorCatEntityRenderer;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityModel;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityRenderer;
import me.emafire003.dev.ohmymeteors.particles.LaserFlashParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticleSmall;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;

public class OhMyMeteorsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        registerEntityStuff();
        registerBlockStuff();
        registerParticles();
    }

    public void registerParticles(){
        ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_PARTICLE, LaserParticle.EggCrackFactory::new);
        ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_PARTICLE_SMALL, LaserParticleSmall.EggCrackFactory::new);
        ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_FLASH_PARTICLE, LaserFlashParticle.LaserFlashFactory::new);

    }

    public static void registerEntityStuff(){
        EntityModelLayerRegistry.registerModelLayer(MeteorProjectileEntityModel.METEOR, MeteorProjectileEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(OMMEntities.METEOR_PROJECTILE_ENTITY, MeteorProjectileEntityRenderer::new);
        //TODO this may be needed
        //EntityModelLayerRegistry.registerModelLayer(MeteorCatEntityModel., MeteorProjectileEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(OMMEntities.METEOR_KITTY_CAT, MeteorCatEntityRenderer::new);

    }

    public static void registerBlockStuff(){
        BlockRenderLayerMap.INSTANCE.putBlock(OMMBlocks.BASIC_METEOR_LASER, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(OMMBlocks.ADVANCED_METEOR_LASER, RenderType.translucent());
    }

}
