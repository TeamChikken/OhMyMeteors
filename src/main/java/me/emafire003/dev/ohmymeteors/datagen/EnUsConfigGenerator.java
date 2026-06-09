package me.emafire003.dev.ohmymeteors.datagen;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.OMMConfigV2;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;

//TODO this is useless! Did i mention i hate forge?
public class EnUsConfigGenerator extends LanguageProvider {

    public EnUsConfigGenerator(PackOutput output) {
        super(output, OhMyMeteors.MOD_ID, "en_us");
    }

    public void generateModTranslations(){
        add("entity.ohmymeteors.meteor_projectile", "Meteor");
        add("entity.ohmymeteors.meteor_cat", "Meteor Cat");

        add("block.ohmymeteors.basic_meteor_laser", "Basic Meteor Laser");
        add("block.ohmymeteors.advanced_meteor_laser", "Advanced Meteor Laser");
        add("block.ohmymeteors.meteoric_rock", "Meteoric Rock");

        add("item.ohmymeteors.meteoric_chunk", "Meteoric Chunk");
        add("item.ohmymeteors.focusing_lenses", "Focusing Lenses");
        add("item.ohmymeteors.meteoric_alloy", "Meteoric Alloy");
        add("item.ohmymeteors.meteor_cat_spawn_egg", "Meteor Cat Spawn Egg");

        add("message.ohmymeteors.meteor_spawned", "A new meteor has spawned!");
        add("message.ohmymeteors.meteor_destroyed", "A meteor has been destroyed!");
        add("message.ohmymeteors.meteor_spawned.huge", "WATCH OUT! A new HUGE meteor has spawned!");
        add("message.ohmymeteors.meteor_destroyed.huge", "A HUGE meteor has been destroyed!");
        add("message.ohmymeteors.meteor_shower_spawned", "!!! A METEOR SHOWER HAS BEGUN !!!");

        add("message.ohmymeteors.meteor_spawned.localized", "A new meteor has spawned around %s!");
        add("message.ohmymeteors.meteor_impacted.localized", "A meteor has impacted around %s!");
        add("message.ohmymeteors.meteor_destroyed.localized", "A meteor has been destroyed around %s!");
        add("message.ohmymeteors.meteor_spawned.huge.localized", "WATCH OUT! A new HUGE meteor has spawned around %s!");
        add("message.ohmymeteors.meteor_destroyed.huge.localized", "A HUGE meteor has been destroyed around %s!");
        add("message.ohmymeteors.meteor_shower_spawned.localized", "!!! A METEOR SHOWER HAS BEGUN AROUND %s !!!");

        add("command.ohmymeteors.custom.reload", "You can use the §b/reload §rcommand to reload the datapack and see the effects of your changes!");
        add("command.ohmymeteors.custom.special", " classified as a §aspecial §rmeteor!");
        add("command.ohmymeteors.custom.not_special", " and §cnot§r classified as a §cspecial §rmeteor!");
        add("command.ohmymeteors.custom.add.success", "Successfully added '%s' to the generated datapack with size '%s'");
        add("command.ohmymeteors.custom.add.failed", "Failed to add '%s' to the generated datapack!");
        add("command.ohmymeteors.custom.add.failed.already_present", "The structure is already present in the datapack! Remove or rename it first!");
        add("command.ohmymeteors.custom.add.failed.origin_not_found", "Failed to add '%s' to the generated datapack, the specified file does not exist!");
        add("command.ohmymeteors.custom.remove.success", "Structure '%s' successfully removed from the datapack!");
        add("command.ohmymeteors.custom.remove.failed", "Failed to remove '%s' from the generated datapack! Check the logs!");
        add("command.ohmymeteors.custom.remove.failed.already_absent", "Structure '%s' is already not in the datapack!");
        add("command.ohmymeteors.custom.ignoredefaults.on", "Default meteor structures won't spawn anymore! Remember to at least add another meteor structure!");
        add("command.ohmymeteors.custom.ignoredefaults.off", "Default meteor structures will now spawn alongside custom ones!");
        add("command.ohmymeteors.custom.ignoredefaults.already_on", "Default meteors structure are already disabled!");
        add("command.ohmymeteors.custom.ignoredefaults.failed", "Failed to generate the ignoredefualts file, check the logs!");
        add("command.ohmymeteors.custom.edit.success", "Successfully edited '%s' to now be identified as '%s' with size '%s'");
        add("command.ohmymeteors.custom.edit.file_not_found", "The file of structure '%s' with size '%s' and special '%s' doesn't exist in the datapack!");
        add("command.ohmymeteors.custom.edit.dest_file_already_exists", "A file with the name '%s' already exits in the destination (size and special) you have specified!");
        add("command.ohmymeteors.custom.edit.failed", "Failed to edit '%s', check the logs!");
        add("command.ohmymeteors.custom.display.special", "specials");
        add("command.ohmymeteors.custom.display.huge", "Huge meteors");
        add("command.ohmymeteors.custom.display.big", "Big meteors");
        add("command.ohmymeteors.custom.display.medium", "Medium meteors");
        add("command.ohmymeteors.custom.display.small", "Small meteors");
        add("command.ohmymeteors.custom.display.not_generated", "Datapack not yet generated!");
        add("command.ohmymeteors.custom.display.ignoredefaults.on", "Default meteors are currently §cdisabled");
        add("command.ohmymeteors.custom.display.ignoredefaults.off", "Default meteors are currently §aenabled");
        add("command.ohmymeteors.custom.display.failed", "Failed to list the structures inside the generated datapack, check the logs!");

        add("audio.ohmymeteors.laser_fire", "Meteor Laser fired!");
        add("audio.ohmymeteors.laser_area_on", "Showing laser target area!");
        add("audio.ohmymeteors.laser_area_off", "Stop showing laser target area!");
    }

    @Override
    protected void addTranslations() {
        generateModTranslations();
        ConfigApiJava.buildTranslations(OMMConfigV2.class, ResourceLocation.tryBuild(OhMyMeteors.MOD_ID, "ohmymeteors_config"), "en_us", true, this::add);
        add("ohmymeteors.particle_mode_enum.FANCY", "FANCY");
        add("ohmymeteors.particle_mode_enum.LESS", "LESS");
        add("ohmymeteors.particle_mode_enum.MINIMAL", "MINIMAL");
        add("ohmymeteors.particle_mode_enum.NONE", "NONE");
        add("ohmymeteors.texture_mode_enum.DYNAMIC_HEIGHT", "DYNAMIC_HEIGHT");
        add("ohmymeteors.texture_mode_enum.DYNAMIC_DISTANCE", "DYNAMIC_DISTANCE");
        add("ohmymeteors.texture_mode_enum.DYNAMIC_AUTO", "DYNAMIC_AUTO");
        add("ohmymeteors.texture_mode_enum.HOT", "HOT");
        add("ohmymeteors.texture_mode_enum.MID", "MID");
        add("ohmymeteors.texture_mode_enum.NORMAL", "NORMAL");
    }
}
