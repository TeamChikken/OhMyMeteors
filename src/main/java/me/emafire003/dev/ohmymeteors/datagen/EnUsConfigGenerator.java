package me.emafire003.dev.ohmymeteors.datagen;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.OMMConfigV2;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class EnUsConfigGenerator extends FabricLanguageProvider {

    public EnUsConfigGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder builder) {
        generateModTranslations(builder);
        //call the api method and provide the builder to automatically append the config lang
        //That's it! You can of course put any other language translating here too.
        ConfigApiJava.buildTranslations(OMMConfigV2.class, Identifier.fromNamespaceAndPath(OhMyMeteors.MOD_ID, "ohmymeteors_config"), "en_us", true, builder::add);
        builder.add("ohmymeteors.particle_mode_enum.FANCY", "FANCY");
        builder.add("ohmymeteors.particle_mode_enum.LESS", "LESS");
        builder.add("ohmymeteors.particle_mode_enum.MINIMAL", "MINIMAL");
        builder.add("ohmymeteors.particle_mode_enum.NONE", "NONE");
        builder.add("ohmymeteors.texture_mode_enum.DYNAMIC_HEIGHT", "DYNAMIC_HEIGHT");
        builder.add("ohmymeteors.texture_mode_enum.DYNAMIC_DISTANCE", "DYNAMIC_DISTANCE");
        builder.add("ohmymeteors.texture_mode_enum.DYNAMIC_AUTO", "DYNAMIC_AUTO");
        builder.add("ohmymeteors.texture_mode_enum.HOT", "HOT");
        builder.add("ohmymeteors.texture_mode_enum.MID", "MID");
        builder.add("ohmymeteors.texture_mode_enum.NORMAL", "NORMAL");
        //ohmymeteors.texture_mode_enum
    }

    public void generateModTranslations(TranslationBuilder translationBuilder){
        translationBuilder.add("entity.ohmymeteors.meteor_projectile", "Meteor");
        translationBuilder.add("entity.ohmymeteors.meteor_cat", "Meteor Cat");

        translationBuilder.add("block.ohmymeteors.basic_meteor_laser", "Basic Meteor Laser");
        translationBuilder.add("block.ohmymeteors.advanced_meteor_laser", "Advanced Meteor Laser");
        translationBuilder.add("block.ohmymeteors.meteoric_rock", "Meteoric Rock");

        translationBuilder.add("item.ohmymeteors.meteoric_chunk", "Meteoric Chunk");
        translationBuilder.add("item.ohmymeteors.focusing_lenses", "Focusing Lenses");
        translationBuilder.add("item.ohmymeteors.meteoric_alloy", "Meteoric Alloy");
        translationBuilder.add("item.ohmymeteors.meteor_cat_spawn_egg", "Meteor Cat Spawn Egg");

        translationBuilder.add("message.ohmymeteors.meteor_spawned", "A new meteor has spawned!");
        translationBuilder.add("message.ohmymeteors.meteor_destroyed", "A meteor has been destroyed!");
        translationBuilder.add("message.ohmymeteors.meteor_spawned.huge", "WATCH OUT! A new HUGE meteor has spawned!");
        translationBuilder.add("message.ohmymeteors.meteor_destroyed.huge", "A HUGE meteor has been destroyed!");
        translationBuilder.add("message.ohmymeteors.meteor_shower_spawned", "!!! A METEOR SHOWER HAS BEGUN !!!");

        translationBuilder.add("message.ohmymeteors.meteor_spawned.localized", "A new meteor has spawned around %s!");
        translationBuilder.add("message.ohmymeteors.meteor_impacted.localized", "A meteor has impacted around %s!");
        translationBuilder.add("message.ohmymeteors.meteor_destroyed.localized", "A meteor has been destroyed around %s!");
        translationBuilder.add("message.ohmymeteors.meteor_spawned.huge.localized", "WATCH OUT! A new HUGE meteor has spawned around %s!");
        translationBuilder.add("message.ohmymeteors.meteor_destroyed.huge.localized", "A HUGE meteor has been destroyed around %s!");
        translationBuilder.add("message.ohmymeteors.meteor_shower_spawned.localized", "!!! A METEOR SHOWER HAS BEGUN AROUND %s !!!");

        translationBuilder.add("command.ohmymeteors.custom.reload", "You can use the §b/reload §rcommand to reload the datapack and see the effects of your changes!");
        translationBuilder.add("command.ohmymeteors.custom.special", " classified as a §aspecial §rmeteor!");
        translationBuilder.add("command.ohmymeteors.custom.not_special", " and §cnot§r classified as a §cspecial §rmeteor!");
        translationBuilder.add("command.ohmymeteors.custom.add.success", "Successfully added '%s' to the generated datapack with size '%s'");
        translationBuilder.add("command.ohmymeteors.custom.add.failed", "Failed to add '%s' to the generated datapack!");
        translationBuilder.add("command.ohmymeteors.custom.add.failed.already_present", "The structure is already present in the datapack! Remove or rename it first!");
        translationBuilder.add("command.ohmymeteors.custom.add.failed.origin_not_found", "Failed to add '%s' to the generated datapack, the specified file does not exist!");
        translationBuilder.add("command.ohmymeteors.custom.remove.success", "Structure '%s' successfully removed from the datapack!");
        translationBuilder.add("command.ohmymeteors.custom.remove.failed", "Failed to remove '%s' from the generated datapack! Check the logs!");
        translationBuilder.add("command.ohmymeteors.custom.remove.failed.already_absent", "Structure '%s' is already not in the datapack!");
        translationBuilder.add("command.ohmymeteors.custom.ignoredefaults.on", "Default meteor structures won't spawn anymore! Remember to at least add another meteor structure!");
        translationBuilder.add("command.ohmymeteors.custom.ignoredefaults.off", "Default meteor structures will now spawn alongside custom ones!");
        translationBuilder.add("command.ohmymeteors.custom.ignoredefaults.already_on", "Default meteors structure are already disabled!");
        translationBuilder.add("command.ohmymeteors.custom.ignoredefaults.failed", "Failed to generate the ignoredefualts file, check the logs!");
        translationBuilder.add("command.ohmymeteors.custom.edit.success", "Successfully edited '%s' to now be identified as '%s' with size '%s'");
        translationBuilder.add("command.ohmymeteors.custom.edit.file_not_found", "The file of structure '%s' with size '%s' and special '%s' doesn't exist in the datapack!");
        translationBuilder.add("command.ohmymeteors.custom.edit.dest_file_already_exists", "A file with the name '%s' already exits in the destination (size and special) you have specified!");
        translationBuilder.add("command.ohmymeteors.custom.edit.failed", "Failed to edit '%s', check the logs!");
        translationBuilder.add("command.ohmymeteors.custom.display.special", "specials");
        translationBuilder.add("command.ohmymeteors.custom.display.huge", "Huge meteors");
        translationBuilder.add("command.ohmymeteors.custom.display.big", "Big meteors");
        translationBuilder.add("command.ohmymeteors.custom.display.medium", "Medium meteors");
        translationBuilder.add("command.ohmymeteors.custom.display.small", "Small meteors");
        translationBuilder.add("command.ohmymeteors.custom.display.not_generated", "Datapack not yet generated!");
        translationBuilder.add("command.ohmymeteors.custom.display.ignoredefaults.on", "Default meteors are currently §cdisabled");
        translationBuilder.add("command.ohmymeteors.custom.display.ignoredefaults.off", "Default meteors are currently §aenabled");
        translationBuilder.add("command.ohmymeteors.custom.display.failed", "Failed to list the structures inside the generated datapack, check the logs!");

        translationBuilder.add("audio.ohmymeteors.laser_fire", "Meteor Laser fired!");
        translationBuilder.add("audio.ohmymeteors.laser_area_on", "Showing laser target area!");
        translationBuilder.add("audio.ohmymeteors.laser_area_off", "Stop showing laser target area!");
    }
}
