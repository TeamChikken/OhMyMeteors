package me.emafire003.dev.ohmymeteors;

import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.blocks.OMMProperties;
import me.emafire003.dev.ohmymeteors.compat.flan.FlanCompat;
import me.emafire003.dev.ohmymeteors.events.OMMEvents;
import me.emafire003.dev.ohmymeteors.commands.OMMCommands;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.items.OMMItems;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import me.emafire003.dev.ohmymeteors.sounds.OMMSounds;
import me.emafire003.dev.ohmymeteors.util.OMMTags;
import me.emafire003.dev.ohmymeteors.util.scheduler.SchedulerUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//TODO look at meteorutils todo
//TODO maybe add here or in an addon a buildable siren and a cannon or gun or automatic laser that shoots incoming meteors. Maybe an addon.

public class OhMyMeteors implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "ohmymeteors";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Path PATH = Path.of(FabricLoader.getInstance().getConfigDir() + "/" + MOD_ID + "/");

	public static String PREFIX = "§8[Oh My, Meteors!] §r";

	//Dunno, 14 is the enderpearl thingy sooo
	public static final TicketType METEOR_CHUCK_TICKET = Registry.register(BuiltInRegistries.TICKET_TYPE, OhMyMeteors.MOD_ID+":meteor", new TicketType(5*20, 14));


	public static Identifier getIdentifier(String path){
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Config.FILEPATH = PATH.resolve(OhMyMeteors.MOD_ID + "_config.yml");

		OMMCommands.registerArguments();
		CommandRegistrationCallback.EVENT.register(OMMCommands::registerCommands);

		OMMProperties.registerBlockProperties();
		OMMEntities.registerEntities();
		OMMBlocks.registerBlocks();
		OMMEvents.registerEvents();
		OMMSounds.registerSounds();
		OMMItems.registerItems();
		OMMParticles.registerParticles();
        OMMTags.registerTags();
		if(FabricLoader.getInstance().isModLoaded("flan")){
			FlanCompat.registerFlan();
		}

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((minecraftServer, lifecycledResourceManager, b) -> {
			//yes reloads for each dimension
			//TODO maybe just pick one? Datapacks aren't per-dimension right? But multiverse and stuff exists so idk
			minecraftServer.getAllLevels().forEach(OhMyMeteors::reInitStructures);
		});

		AtomicBoolean shouldWarn = new AtomicBoolean(false);
		//loads the config file on server startup and the scheduler
		ServerLifecycleEvents.SERVER_STARTED.register( minecraftServer -> {
			try{
				SchedulerUtils.registerOnServerTick();
				shouldWarn.set(!Config.reloadConfig());
				//minecraftServer.getWorlds().forEach(OhMyMeteors::reInitStructures);
			}catch (Exception e){
				LOGGER.error("There was an error while loading the config files!");
				e.printStackTrace();
			}
		});

		ServerPlayerEvents.JOIN.register((serverPlayer -> {
			if(serverPlayer.hasPermissions(4) && shouldWarn.get()){
				serverPlayer.sendSystemMessage(Component.literal(PREFIX).append(Component.literal("§cWarning! The config file has been restored to the default settings because something has gone wrong while loading it! A copy of the old file has been created.")));
			}
		}));

	}

	/*public static final TagKey<Block> METEOR_BYPASSES = TagKey.of(RegistryKeys.BLOCK, getIdentifier("meteor_bypasses"));
	public static final TagKey<Block> METEOR_BYPASSES_AND_DESTROY = TagKey.of(RegistryKeys.BLOCK, getIdentifier("meteor_bypasses_and_destroy"));
    public static final TagKey<Block> METEOR_EXPLOSION_SAFE = TagKey.create(Registries.BLOCK, getIdentifier("meteor_explosion_safe"));
*/
	@SuppressWarnings("unused")
	/*public static void registerTags(){
		Registries.BLOCK.iterateEntries(METEOR_BYPASSES).forEach(blockRegistryEntry -> registerTags());
HolderSet.Named<Block> METEOR_BYPASSES_TAG = BuiltInRegistries.BLOCK.getOrCreateTag(METEOR_BYPASSES);
		HolderSet.Named<Block> METEOR_BYPASSES_AND_DESTROY_TAG = BuiltInRegistries.BLOCK.getOrCreateTag(METEOR_BYPASSES_AND_DESTROY);
		HolderSet.Named<Block> METEOR_EXPLOSION_SAFE_TAG = BuiltInRegistries.BLOCK.getOrCreateTag(METEOR_EXPLOSION_SAFE);


		/*RegistryEntryList.Named<Block> METEOR_BYPASSES_AND_DESTROY_TAG = Registries.BLOCK.iterateEntries(METEOR_BYPASSES_AND_DESTROY).forEach(blockRegistryEntry -> registerTags());

	}*/

	public static List<Identifier> METEOR_STRUCTURES = new ArrayList<>();

	public static void reInitStructures(ServerLevel world){
		 METEOR_STRUCTURES = new ArrayList<>(world.getStructureManager().listTemplates().filter(
				identifier -> identifier.getNamespace().equals(OhMyMeteors.MOD_ID)
		).toList());

		 METEOR_STRUCTURES.remove(getIdentifier("error"));

		//this allows to have "ignore_<structure>" to "remove" a default structure with a datapack
		//or "ignoredefault" to have it remove all the structures
		List<Identifier> structures_copy = new ArrayList<>(METEOR_STRUCTURES);

		//the stream is to avoid concurrent modification exception
		structures_copy.forEach(id -> {
			if(id.getPath().contains("ignore_")){
				//If in the root folder, adjust the thingy
				if(id.getPath().startsWith("ignore_")){
					METEOR_STRUCTURES.remove(Identifier.fromNamespaceAndPath(id.getNamespace(),
							id.getPath().replaceAll("ignore_", "").split("_")[0]+"/"+id.getPath().replaceAll("ignore_", "")));
					METEOR_STRUCTURES.remove(id);
				}else{
					//Removes the targeted structure
					METEOR_STRUCTURES.remove(Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath().replaceAll("ignore_", "")));
					METEOR_STRUCTURES.remove(id);//Since this ignore_structure also needs to be removed
				}

			}
			if(id.getPath().contains("ignoredefault") || id.getPath().contains("ignoredefaults")){
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("big/special/big_meteor_cat"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("big/big_meteor_0"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("big/big_meteor_1"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("big/big_meteor_2"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("huge/huge_meteor_0"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("huge/huge_meteor_1"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("huge/huge_meteor_2"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("medium/medium_meteor_0"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("medium/medium_meteor_1"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("medium/medium_meteor_2"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("medium/special/medium_meteor_99"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("small/small_meteor_0"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("small/small_meteor_1"));
				METEOR_STRUCTURES.remove(OhMyMeteors.getIdentifier("small/small_meteor_2"));
				METEOR_STRUCTURES.remove(id);

			}
		});

		if(METEOR_STRUCTURES.isEmpty()){
			METEOR_STRUCTURES.add(OhMyMeteors.getIdentifier("error"));
			OhMyMeteors.LOGGER.error("ERROR! No meteor structures available, you have just removed every default structure! An error meteor structure is all that is going to spawn currently.  Please insert at least one of your custom structures, and reload!");
		}
	}

}