package me.emafire003.dev.ohmymeteors.events;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.advanced_laser.AdvancedMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.util.datasaver.SimpleDataSaverUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.entity.player.Player;

public class OMMEvents {

    public static void registerEvents(){
        MeteorSpawnEvent.EVENT.register(meteor -> {
            BasicMeteorLaserBlock.awakeLasers();
            AdvancedMeteorLaserBlock.awakeLasers();
        });

        //TODO technically this should be per-world. I don't think anyone will use it for more worlds tho...
        ServerEntityEvents.ENTITY_LOAD.register( ((entity, serverLevel) -> {
            if(OhMyMeteors.CONFIG.meteorSpawning.start_spawning_after_dimension_list
                    .contains(serverLevel.dimensionTypeRegistration().getRegisteredName())
                    &&
                    entity instanceof Player){
                if(!SimpleDataSaverUtils.DATA.enteredDimensions.contains(serverLevel.dimensionTypeRegistration().getRegisteredName())){
                    SimpleDataSaverUtils.DATA.enteredDimensions.add(serverLevel.dimensionTypeRegistration().getRegisteredName());
                }
                SimpleDataSaverUtils.saveFile();
            }
        }));
    }
}
