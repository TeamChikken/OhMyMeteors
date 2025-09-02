package me.emafire003.dev.ohmymeteors;

import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.blocks.OMMProperties;
import me.emafire003.dev.ohmymeteors.events.OMMEvents;
import me.emafire003.dev.ohmymeteors.commands.OMMCommands;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.items.OMMItems;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import me.emafire003.dev.ohmymeteors.sounds.OMMSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class OhMyMeteors implements ModInitializer {

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "ohmymeteors";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Path PATH = Path.of(FabricLoader.getInstance().getConfigDir() + "/" + MOD_ID + "/");

	public static String PREFIX = "[Oh My, Meteors!] ";

	public static Identifier getIdentifier(String path){
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Config.FILEPATH = PATH.resolve(OhMyMeteors.MOD_ID + "_config.yml");

		CommandRegistrationCallback.EVENT.register(OMMCommands::registerCommands);

		OMMProperties.registerBlockProperties();
		OMMEntities.registerEntities();
		OMMBlocks.registerBlocks();
		OMMEvents.registerEvents();
		OMMSounds.registerSounds();
		OMMItems.registerItems();
		OMMParticles.registerParticles();
		registerTags();

		//loads the config file on server startup
		ServerLifecycleEvents.SERVER_STARTED.register( minecraftServer -> {
			try{
				Config.reloadConfig();
			}catch (Exception e){
				LOGGER.error("There was an error while loading the config files!");
				e.printStackTrace();
			}
		});
	}

	public static final TagKey<Block> METEOR_BYPASSES = TagKey.of(RegistryKeys.BLOCK, getIdentifier("meteor_bypasses"));
	public static final TagKey<Block> METEOR_BYPASSES_AND_DESTROY = TagKey.of(RegistryKeys.BLOCK, getIdentifier("meteor_bypasses_and_destroy"));

	@SuppressWarnings("unused")
	public static void registerTags(){
		RegistryEntryList.Named<Block> METEOR_BYPASSES_TAG = Registries.BLOCK.getOrCreateEntryList(METEOR_BYPASSES);
		RegistryEntryList.Named<Block> METEOR_BYPASSES_AND_DESTROY_TAG = Registries.BLOCK.getOrCreateEntryList(METEOR_BYPASSES_AND_DESTROY);

	}

}