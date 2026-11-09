package me.emafire003.dev.ohmymeteors.events;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlock;;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import me.emafire003.dev.ohmymeteors.util.datasaver.SimpleDataSaverUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OMMEvents {

    @SubscribeEvent
    public void meteorSpawnEvent(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof MeteorProjectileEntity){
            BasicMeteorLaserBlock.awakeLasers();
            AdvancedMeteorLaserBlock.awakeLasers();
        }
        if(OhMyMeteors.CONFIG.meteorSpawning.start_spawning_after_dimension_list
                .contains(event.getLevel().dimensionTypeRegistration().toString())
                &&
                event.getEntity() instanceof Player){
            if(!SimpleDataSaverUtils.DATA.enteredDimensions.contains(event.getLevel().dimensionTypeRegistration().toString())){
                SimpleDataSaverUtils.DATA.enteredDimensions.add(event.getLevel().dimensionTypeRegistration().toString());
            }
            SimpleDataSaverUtils.saveFile();
        }

    }


}
