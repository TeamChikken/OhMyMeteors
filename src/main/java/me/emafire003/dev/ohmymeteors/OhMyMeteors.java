package me.emafire003.dev.ohmymeteors;

import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.blocks.OMMProperties;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorShowerTypeArgumentType;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorSizeClassArgumentType;
import me.emafire003.dev.ohmymeteors.compat.flan.FlanCompat;
import me.emafire003.dev.ohmymeteors.compat.perms.PermissionsChecker;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorCatEntityRenderer;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityModel;
import me.emafire003.dev.ohmymeteors.entities.client.MeteorProjectileEntityRenderer;
import me.emafire003.dev.ohmymeteors.events.OMMEvents;
import me.emafire003.dev.ohmymeteors.commands.OMMCommands;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.items.OMMItemTab;
import me.emafire003.dev.ohmymeteors.items.OMMItems;
import me.emafire003.dev.ohmymeteors.particles.LaserFlashParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticle;
import me.emafire003.dev.ohmymeteors.particles.LaserParticleSmall;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import me.emafire003.dev.ohmymeteors.sounds.OMMSounds;
import me.emafire003.dev.ohmymeteors.util.scheduler.SchedulerUtils;

import net.luckperms.api.LuckPermsProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mod(OhMyMeteors.MOD_ID)
public class OhMyMeteors {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "ohmymeteors";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Path PATH = Path.of(FMLLoader.getGamePath() + "/config/" + MOD_ID + "/");

	public static String PREFIX = "§8[Oh My, Meteors!] §r";

	public static ResourceLocation getIdentifier(String path){
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	// The constructor for the mod class is the first code that is run when your mod is loaded.
	// FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
	public OhMyMeteors(FMLJavaModLoadingContext context) {
		IEventBus eventBus = context.getModEventBus();
		// Register the commonSetup method for modloading
		//modEventBus.addListener(this::commonSetup);

		// Register the Deferred Register to the mod event bus so blocks get registered
		//BLOCKS.register(modEventBus);
		// Register the Deferred Register to the mod event bus so items get registered
		//ITEMS.register(modEventBus);
		// Register the Deferred Register to the mod event bus so tabs get registered
		//CREATIVE_MODE_TABS.register(modEventBus);

		// Register ourselves for server and other game events we are interested in.
		// Note that this is necessary if and only if we want *this* class (OhMyMeteors) to respond directly to events.
		// Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new OMMEvents());
		MinecraftForge.EVENT_BUS.register(SchedulerUtils.class);
		//MinecraftForge.EVENT_BUS.register(OhMyMeteorsClient.class);
		eventBus.addListener(this::registerCustomArgumentType);


		Config.FILEPATH = PATH.resolve(OhMyMeteors.MOD_ID + "_config.yml");

		OMMBlocks.register(eventBus);
		OMMItems.register(eventBus);
		OMMProperties.registerBlockProperties();
		OMMEntities.register(eventBus);
		OMMSounds.register(eventBus);
		OMMItemTab.register(eventBus);
		registerTags();
		OMMParticles.register(eventBus);


		if(ModList.get().isLoaded("flan")){
			FlanCompat.registerFlan();
		}

	}

	private static MinecraftServer serverInstance = null;

	// Loads the config file on server startup as well as the scheduler
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		// Do something when the server starts
		try{
			Config.reloadConfig();
			if(ModList.get().isLoaded("luckperms")){
				PermissionsChecker.luckPerms = LuckPermsProvider.get();
			}
			//This is needed because for SOME REASON the datapack reload event doesn't have a server parameter :/
			serverInstance = event.getServer();
			//minecraftServer.getWorlds().forEach(OhMyMeteors::reInitStructures);
		}catch (Exception e){
			LOGGER.error("There was an error while loading the config files!");
			e.printStackTrace();
		}
	}

	// Wow this looks like a stupid way to do this
	@SubscribeEvent
	public void onDatapackReload(OnDatapackSyncEvent event) {
		//why not just have the ServerLifeCycleEvents.Datapackreload thingy? Bah
		if(event.getPlayer() == null){
			//yes reloads for each dimension
			//WHY THE FUCK IS THERE NOT A SERVER ASSOCIATED TO THIS EVENT?!!!
			//TODO maybe just pick one? Datapacks aren't per-dimension right? But multiverse and stuff exists so idk
			if(serverInstance == null){
				LOGGER.error("Something went very wrong, could not get the server while reloading the datapacks!");
				return;
			}
			serverInstance.getAllLevels().forEach(OhMyMeteors::reInitStructures);
		}
	}

	// registers commands
	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent event) {
		// Do something when the server starts
		try{
			OMMCommands.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
		}catch (Exception e){
			LOGGER.error("There was an error while loading the config files!");
			e.printStackTrace();
		}
	}

	//Register argument types
	private void registerCustomArgumentType(RegisterEvent event) {
		event.register(
				BuiltInRegistries.COMMAND_ARGUMENT_TYPE.key(),
				OhMyMeteors.getIdentifier("meteor_size_class"),
				() -> {
					SingletonArgumentInfo<MeteorSizeClassArgumentType> meteorSizeClassArgumentTypeSingletonArgumentInfo = SingletonArgumentInfo.contextFree(MeteorSizeClassArgumentType::new);
					return ArgumentTypeInfos.registerByClass(MeteorSizeClassArgumentType.class, meteorSizeClassArgumentTypeSingletonArgumentInfo);
				}
		);
		event.register(
				BuiltInRegistries.COMMAND_ARGUMENT_TYPE.key(),
				OhMyMeteors.getIdentifier("meteor_shower_type"),
				() -> {
					SingletonArgumentInfo<MeteorShowerTypeArgumentType> meteorShowerTypeArgumentTypeSingletonArgumentInfo = SingletonArgumentInfo.contextFree(MeteorShowerTypeArgumentType::new);
					return ArgumentTypeInfos.registerByClass(MeteorShowerTypeArgumentType.class, meteorShowerTypeArgumentTypeSingletonArgumentInfo);
				}
		);
	}


	public static final TagKey<Block> METEOR_BYPASSES = TagKey.create(Registries.BLOCK, getIdentifier("meteor_bypasses"));
	public static final TagKey<Block> METEOR_BYPASSES_AND_DESTROY = TagKey.create(Registries.BLOCK, getIdentifier("meteor_bypasses_and_destroy"));
	public static final TagKey<Block> AIR_BLOCKS = TagKey.create(Registries.BLOCK, getIdentifier("air"));


	@SuppressWarnings("unused")
	public static void registerTags(){
		HolderSet.Named<Block> METEOR_BYPASSES_TAG = BuiltInRegistries.BLOCK.getOrCreateTag(METEOR_BYPASSES);
		HolderSet.Named<Block> METEOR_BYPASSES_AND_DESTROY_TAG = BuiltInRegistries.BLOCK.getOrCreateTag(METEOR_BYPASSES_AND_DESTROY);
		HolderSet.Named<Block> AIR_TAG = BuiltInRegistries.BLOCK.getOrCreateTag(AIR_BLOCKS);

	}

	public static List<ResourceLocation> METEOR_STRUCTURES = new ArrayList<>();

	public static void reInitStructures(ServerLevel world){
		 METEOR_STRUCTURES = new ArrayList<>(world.getStructureManager().listTemplates().filter(
				identifier -> identifier.getNamespace().equals(OhMyMeteors.MOD_ID)
		).toList());

		 METEOR_STRUCTURES.remove(getIdentifier("error"));

		//this allows to have "ignore_<structure>" to "remove" a default structure with a datapack
		//or "ignoredefault" to have it remove all the structures
		List<ResourceLocation> structures_copy = new ArrayList<>(METEOR_STRUCTURES);

		//the stream is to avoid concurrent modification exception
		structures_copy.forEach(id -> {
			if(id.getPath().contains("ignore_")){
				//If in the root folder, adjust the thingy
				if(id.getPath().startsWith("ignore_")){
					METEOR_STRUCTURES.remove(ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
							id.getPath().replaceAll("ignore_", "").split("_")[0]+"/"+id.getPath().replaceAll("ignore_", "")));
					METEOR_STRUCTURES.remove(id);
				}else{
					//Removes the targeted structure
					METEOR_STRUCTURES.remove(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath().replaceAll("ignore_", "")));
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

	/////////////////////////////// Client ///////////////////////////////

	// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
	@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {

			EntityRenderers.register(OMMEntities.METEOR_PROJECTILE_ENTITY.get(), MeteorProjectileEntityRenderer::new);
			EntityRenderers.register(OMMEntities.METEOR_KITTY_CAT.get(), MeteorCatEntityRenderer::new);
		}
		@SubscribeEvent
		public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
			event.registerSpriteSet(OMMParticles.LASER_PARTICLE.get(), LaserParticle.EggCrackFactory::new);
			event.registerSpriteSet(OMMParticles.LASER_PARTICLE_SMALL.get(), LaserParticleSmall.EggCrackFactory::new);
			event.registerSpriteSet(OMMParticles.LASER_FLASH_PARTICLE.get(), LaserFlashParticle.LaserFlashFactory::new);
		}

		@SubscribeEvent
		public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
			event.registerLayerDefinition(MeteorProjectileEntityModel.METEOR, MeteorProjectileEntityModel::getTexturedModelData);
		}
	}

	/*@Mod.EventBusSubscriber(modid = OhMyMeteors.MOD_ID, value = Dist.CLIENT)
	public static class OhMyMeteorsClient {

    /*public void registerParticles(){
        ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_PARTICLE, LaserParticle.EggCrackFactory::new);
        ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_PARTICLE_SMALL, LaserParticleSmall.EggCrackFactory::new);
        ParticleFactoryRegistry.getInstance().register(OMMParticles.LASER_FLASH_PARTICLE, LaserFlashParticle.LaserFlashFactory::new);
    }


		@SubscribeEvent
		static void onClientSetup(FMLClientSetupEvent event) {
			LOGGER.error("Regustering entity struff");
			registerEntityStuff();
		}
		@SubscribeEvent
		public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
			event.registerSpriteSet(OMMParticles.LASER_PARTICLE.get(), LaserParticle.EggCrackFactory::new);
			event.registerSpriteSet(OMMParticles.LASER_PARTICLE_SMALL.get(), LaserParticleSmall.EggCrackFactory::new);
			event.registerSpriteSet(OMMParticles.LASER_FLASH_PARTICLE.get(), LaserFlashParticle.LaserFlashFactory::new);
		}

		public static void registerEntityStuff(){
			EntityRenderers.register(OMMEntities.METEOR_PROJECTILE_ENTITY.get(), MeteorProjectileEntityRenderer::new);
			EntityRenderers.register(OMMEntities.METEOR_KITTY_CAT.get(), MeteorCatEntityRenderer::new);


        /*EntityModelLayerRegistry.registerModelLayer(MeteorProjectileEntityModel.METEOR, MeteorProjectileEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(OMMEntities.METEOR_PROJECTILE_ENTITY, MeteorProjectileEntityRenderer::new);
        //EntityModelLayerRegistry.registerModelLayer(MeteorCatEntityModel., MeteorProjectileEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(OMMEntities.METEOR_KITTY_CAT, MeteorCatEntityRenderer::new);

		}

		@SubscribeEvent
		public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
			event.registerLayerDefinition(MeteorProjectileEntityModel.METEOR, MeteorProjectileEntityModel::getTexturedModelData);
		}

    /*
    public static void registerBlockStuff(){
        BlockRenderLayerMap.INSTANCE.putBlock(OMMBlocks.BASIC_METEOR_LASER, RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(OMMBlocks.ADVANCED_METEOR_LASER, RenderType.translucent());
    }

	}*/

}