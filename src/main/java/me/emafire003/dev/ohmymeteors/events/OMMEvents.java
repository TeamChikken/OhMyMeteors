package me.emafire003.dev.ohmymeteors.events;

import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OMMEvents {

    @SubscribeEvent
    public void meteorSpawnEvent(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof MeteorProjectileEntity){
            BasicMeteorLaserBlock.awakeLasers();
            AdvancedMeteorLaserBlock.awakeLasers();
        }
    }
}
