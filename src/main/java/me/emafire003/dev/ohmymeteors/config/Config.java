package me.emafire003.dev.ohmymeteors.config;

import com.mojang.datafixers.util.Pair;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionTypes;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.LOGGER;

public class Config {
    public static SimpleConfig CONFIG;
    private static ConfigProvider configs;

    private static final int ver = 5;
    public static Path FILEPATH;

    public static int VERSION;

    public static int NATURAL_METEOR_MIN_SIZE = 1; //as in the ones that spawn from the sky not the ones you can summon
    public static int NATURAL_METEOR_MAX_SIZE = 10;

    public static int MIN_METEOR_SPAWN_DISTANCE = 2; //As in a radius of blocks around the player in which the meteor won't spawn in (but remember that it can have an angled trajectory)
    public static int MAX_METEOR_SPAWN_DISTANCE = 30; //TODO probably increase it back to 50? Nah

    public static int METEOR_SPAWN_HEIGHT = 300; //At which y level should meteors spawn?

    public static boolean SHOULD_BYPASS_LEAVES = true; //Should the meteor bypass leaves instead of exploding midair om them?
    public static boolean SHOULD_DESTROY_LEAVES = true; //aka the bypassed leaves will be removed

    public static int METEOR_SPAWN_CHANCE = 20000;
    public static boolean SPAWN_HUGE_METEORS = true;
    public static int HUGE_METEOR_CHANCE = 100;
    public static int HUGE_METEOR_SIZE_LIMIT = 35;

    public static boolean MODIFY_SPAWN_CHANCE_AT_NIGHT = false;
    public static int METEOR_NIGHT_SPAWN_CHANCE = 10000;

    public static boolean SHOULD_COOLDOWN_BETWEEN_METEORS = true;
    public static int MIN_METEOR_COOLDOWN_TIME = 20; //In seconds

    public static boolean HOMING_METEORS = false; //These would not spawn with a random direction but aim towards a selected player

    public static boolean ANNOUNCE_METEOR_SPAWN = false;
    public static boolean ANNOUNCE_METEOR_DESTROYED = false;
    public static boolean ACTIONBAR_ANNOUNCEMENTS = true;

    public static boolean METEOR_GRIEFING = true;
    public static boolean SCATTER_METEOR_GRIEFING = true;

    public static boolean METEOR_STRUCTURE = true;
    public static boolean SCATTER_METEOR_STRUCTURE = true;

    public static int BASIC_LASER_AREA_RADIUS = 32;
    public static int BASIC_LASER_HEIGHT = 64;
    public static int ADVANCED_LASER_AREA_RADIUS = 48;
    public static int ADVANCED_LASER_HEIGHT = 64;
    
    public static boolean SHOULD_BASIC_LASER_COOLDOWN = true;
    public static int BASIC_LASER_COOLDOWN = 3; //seconds
    public static boolean SHOULD_ADVANCED_LASER_COOLDOWN = true;
    public static int ADVANCED_LASER_COOLDOWN = 1; //seconds
    
    public static int MAX_SMALL_METEOR_SIZE = 4;
    public static int MAX_MEDIUM_METEOR_SIZE = 7;
    public static int MAX_BIG_METEOR_SIZE = 20;

    public static boolean USE_FORCED_PARTICLES = true;

    //V2
    public static int SPECIAL_METEORS_CHANCE = 10;
    public static boolean GLOBAL_EXPLOSION_SOUND = false;
    public static boolean AREA_EXPLOSION_SOUND = false;
    public static int AREA_EXPLOSION_SOUND_RADIUS = 500; //in blocks
    public static int EXPLOSION_POWER_MODIFIER = 0;

    //V3
    public static double DOWNWARDS_SPEED_MODIFIER = 0;
    public static List<String> SPAWN_DIMENSIONS = List.of(DimensionTypes.OVERWORLD_ID.toString(), DimensionTypes.THE_END_ID.toString());
    public static Map<String, Integer> DIMENSION_CHANCES = Map.of(
            DimensionTypes.OVERWORLD_ID.toString(), METEOR_SPAWN_CHANCE,
            DimensionTypes.THE_END_ID.toString(), METEOR_SPAWN_CHANCE*10
    );
    public static Map<String, Integer> DIMENSION_NIGHT_CHANCES = Map.of(
            DimensionTypes.OVERWORLD_ID.toString(), METEOR_NIGHT_SPAWN_CHANCE,
            DimensionTypes.THE_END_ID.toString(), METEOR_NIGHT_SPAWN_CHANCE*10
    );

    public static boolean BIOME_LIST_MODE = false;
    public static List<String> BIOME_SPAWN_LIST = List.of(BiomeKeys.BADLANDS.getValue().toString(), BiomeKeys.SOUL_SAND_VALLEY.getValue().toString(), "modname:modbiome");
    public static Map<String, Integer> BIOME_CHANCES = Map.of(
            BiomeKeys.DESERT.getValue().toString(), METEOR_SPAWN_CHANCE+10,
            "modname:modbiome", 2025
    );
    public static Map<String, Integer> BIOME_NIGHT_CHANCES = Map.of(
            BiomeKeys.DESERT.getValue().toString(), METEOR_NIGHT_SPAWN_CHANCE+5,
            "modname:modbiome", 2025
    );

    //V4
    public static boolean USE_BETTER_EXPLOSIONS = true;
    public static int METEOR_RENDER_DISTANCE = 200;


    private static final Map<String, Integer> DIMENSION_CHANCES_default = Map.of(
            DimensionTypes.THE_END_ID.toString(), METEOR_SPAWN_CHANCE*10
    );
    private static final Map<String, Integer> DIMENSION_NIGHT_CHANCES_default = Map.of(
            DimensionTypes.THE_END_ID.toString(), METEOR_NIGHT_SPAWN_CHANCE*10
    );

    private static final Map<String, Integer> BIOME_CHANCES_default = Map.of(
            BiomeKeys.DESERT.getValue().toString(), METEOR_SPAWN_CHANCE-10,
            "modname:modbiome", 2025
    );
    private static final Map<String, Integer> BIOME_NIGHT_CHANCES_default = Map.of(
            BiomeKeys.DESERT.getValue().toString(), METEOR_NIGHT_SPAWN_CHANCE-5,
            "modname:modbiome", 2025
    );
    private static final List<String> BIOME_SPAWN_LIST_default =  List.of(BiomeKeys.BADLANDS.getValue().toString(), BiomeKeys.SOUL_SAND_VALLEY.getValue().toString(), "modname:modbiome");

    //V5
    public static boolean METEOR_SHOWERS_ENABLED = true;
    public static int METEOR_SHOWER_CHANCE = 100; //on top of the normal meteor chance. aka a metor drop may become a meteor shower
    public static int MIN_METEORS_IN_SHOWER = 5;
    public static int MAX_METEORS_IN_SHOWER = 20;
    public static boolean ANNOUNCE_LOCATION = true;
    public static boolean ONLY_REPLACE_AIR = false;
    public static boolean SCATTER_ONLY_REPALCE_AIR = false;

    public static void handleVersionChange(){
        int version_found = CONFIG.getOrDefault("version", ver);
        if(version_found != ver){
            LOGGER.warn("DIFFERENT CONFIG VERSION DETECTED, updating...");
            HashMap<String, String> config_old = CONFIG.getConfigCopy();
            try {
                CONFIG.delete();
                CONFIG = SimpleConfig.of(OhMyMeteors.MOD_ID + "_config").provider(configs).request();
                HashMap<Pair<String, ?>, Pair<String, ?>> sub_map = new HashMap<>();

                CONFIG.getConfigCopy().forEach((key, value) -> sub_map.put(new Pair<>(key, value),  new Pair<>(key, config_old.get(key))));
                CONFIG.updateValues(sub_map);
            } catch (IOException e) {
                LOGGER.info("Could not delete config file");
                e.printStackTrace();
            }
        }
    }

    public static void registerConfigs() {
        configs = new ConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(OhMyMeteors.MOD_ID + "_config").provider(configs).request();

        handleVersionChange();

        try{
            assignConfigs();
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.warn("ERROR! The config could not be read, generating a new one...");

            File source = OhMyMeteors.PATH.resolve(OhMyMeteors.MOD_ID + "_config.yml").toFile();
            File target = OhMyMeteors.PATH.resolve(OhMyMeteors.MOD_ID + "_corruptedorold_config.yml").toFile();
            try{
                FileUtils.copyFile(source, target);
                if(CONFIG.delete()){
                    LOGGER.info("Config deleted successfully");
                }else{
                    LOGGER.error("The config could not be deleted");
                }
            } catch (IOException f) {
                f.printStackTrace();
            }
            CONFIG = SimpleConfig.of(OhMyMeteors.MOD_ID + "_config").provider(configs).request();
            assignConfigs();
            LOGGER.warn("Generated a new config file, make sure to configure it again!");
        }

        LOGGER.info("All " + configs.getConfigsList().size() + " have been set properly");
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("version", ver), "The version of the config. DO NOT CHANGE IT :D");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("min_meteor_spawn_distance", 2), "Expressed in blocks, represents the min distance (as in a radius) from the origin of the meteor " +
                "(like a player) in which the meteor wont' spawn in. (Remember that it has an angled trajectory so it could end up in that area regardless)");

        configs.addKeyValuePair(new Pair<>("max_meteor_spawn_distance", 25), "Expressed in blocks, represents the max distance (as in a radius) from the origin of the meteor " +
                "(like a player) in which a meteor can spawn in. (Remember that it has an angled trajectory so it could end up in that area regardless)");
        configs.addKeyValuePair(new Pair<>("meteor_spawn_height", 300), "The world height (y level) at which meteors spawn in");
        configs.addKeyValuePair(new Pair<>("meteor_spawn_chance", 20000), "Expressed as '1 in <x>' chances of spawning a meteor. For example, by default it has a chance of 1 in 20000");
        configs.addKeyValuePair(new Pair<>("modify_spawn_chance_at_night", false),"Should the spawn rate be different during the night?");

        configs.addKeyValuePair(new Pair<>("spawn_huge_meteors", true),"Should huge meteors be able to spawn? They are meteors bigger than the maximum size of the big ones");
        configs.addKeyValuePair(new Pair<>("huge_meteor_chance", 100),"The chance for a spawned meteor to be of huge size. Expressed as in 1 in x chances. (on top of the 'normal' spawning chance)");
        configs.addKeyValuePair(new Pair<>("huge_meteor_size_limit", 35),"The size limit of how big a huge meteor can be");

        configs.addKeyValuePair(new Pair<>("modify_spawn_chance_at_night", false),"Should the spawn rate be different during the night?");
        configs.addKeyValuePair(new Pair<>("meteor_night_spawn_chance", 10000),"The chance for a meteor to spawn at night if enabled. Expressed as in 1 in x chances.");

        configs.addKeyValuePair(new Pair<>("natural_meteor_min_size", 1),"The smallest size a natural meteor can have when spawned in. Cannot go below 1");
        configs.addKeyValuePair(new Pair<>("natural_meteor_max_size", 10),"The biggest size a natural meteor can have when spawned in. Cannot go above 50.");
        //V2
        configs.addKeyValuePair(new Pair<>("explosion_power_modifier", 0), "A factor to ADD to the explosion power (by default, the power is equal to the meteor size), thus increasing the damage and radius of the explosion. Also supports negative numbers");
        //V3
        configs.addKeyValuePair(new Pair<>("downwards_speed_modifier", 0), "A factor to ADD to the speed at which the meteor falls downwards. It is added to a randomly generated number between 1 and 0. Also supports negative numbers");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("should_bypass_leaves", true),"This option has no effect as of version 0.4.0. Please use the 'ohmymeteors:meteor_bypasses' tag instead.");
        configs.addKeyValuePair(new Pair<>("should_destroy_leaves", true),"This option has no effect as of version 0.4.0. Please use the 'ohmymeteors:meteor_bypasses_and_destroy' tag instead");

        configs.addKeyValuePair(new Pair<>("homing_meteors", false),"Should meteors be (more or less) directed towards the nearest player?");

        configs.addKeyValuePair(new Pair<>("announce_meteor_spawn", false),"Should players get a message in chat/hotbar when a meteor spawns?");
        configs.addKeyValuePair(new Pair<>("announce_meteor_destroyed", false),"Should players get a message in chat/hotbar when a meteor is destroyed?");
        configs.addKeyValuePair(new Pair<>("actionbar_announcements", true),"Should the above announcement be displayed above the hotbar in the actionbar or in chat?");
        //V5
        configs.addKeyValuePair(new Pair<>("announce_location", true),"If announcements are enabled, should they also display the (approximate) coordinates of the meteor being spawned/destroyed?");

        //V2
        configs.addKeyValuePair(new Pair<>("global_explosion_sound", false),"Should the explosion sound of the meteor be heard by all players online?");
        configs.addKeyValuePair(new Pair<>("area_explosion_sound", false),"Should the explosion sound of the meteor be heard by all players around a certain area from the impact point?");
        configs.addKeyValuePair(new Pair<>("area_explosion_sound_radius", 500),"The radius in blocks of the area in which the sound of the meteor will be heard if the option above is true");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("should_cooldown_between_meteors", true),"Should there be a cooldown between a meteor spawning one meteor and then another?");
        configs.addKeyValuePair(new Pair<>("min_meteor_cooldown_time", 20),"The minimum time interval (in seconds) between spawning a meteor and then another");

        configs.addKeyValuePair(new Pair<>("meteor_griefing", true),"Should meteors be able to destroy blocks on impact?");
        configs.addKeyValuePair(new Pair<>("scatter_meteor_griefing", true),"Should the meteors that come out of a bigger meteor when it's broken be able to destroy blocks on impact?");

        configs.addKeyValuePair(new Pair<>("meteor_structure", true),"Should meteors spawn the meteor structure after impact?");
        configs.addKeyValuePair(new Pair<>("scatter_meteor_structure", true),"Should the meteors that come out of a bigger meteor when it's broken be able to destroy spawn structures on impact?");

        configs.addKeyValuePair(new Pair<>("only_replace_air", false),"Should the meteor structure only replace air blocks?");
        configs.addKeyValuePair(new Pair<>("scatter_only_replace_air", true),"Should the meteors that come out of a bigger meteor when it's broken only replace air blocks for their structure?");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("basic_laser_area_radius", 32),"The radius in blocks of the xz area covered by the Basic laser block, where meteors will be blown up");
        configs.addKeyValuePair(new Pair<>("basic_laser_height", 64),"How many blocks up from the position of the basic laser should meteors be checked for? (note that the detection box is only 2 blocks thick, not the whole way)");

        configs.addKeyValuePair(new Pair<>("advanced_laser_area_radius", 48),"The radius in blocks of the xz area covered by the advanced laser block, where meteors will be blown up");
        configs.addKeyValuePair(new Pair<>("advanced_laser_height", 64),"How many blocks up from the position of the advanced laser should meteors be checked for? (note that the detection box is only 2 blocks thick, not the whole way)");

        configs.addKeyValuePair(new Pair<>("should_basic_laser_cooldown", true),"Should the laser be in a cooldown where it can't fire, after it has just fired?");
        configs.addKeyValuePair(new Pair<>("basic_laser_cooldown", 3),"How many seconds should this cooldown last?");

        configs.addKeyValuePair(new Pair<>("should_advanced_laser_cooldown", true),"Should the laser be in a cooldown where it can't fire, after it has just fired?");
        configs.addKeyValuePair(new Pair<>("advanced_laser_cooldown", 1),"How many seconds should this cooldown last?");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");
        
        configs.addKeyValuePair(new Pair<>("max_small_meteor_size", 4),"The maximum size of meteor that can be considered small, and will spawn a small meteor structure upon impact");
        configs.addKeyValuePair(new Pair<>("max_medium_meteor_size", 7),"The maximum size of meteor that can be considered medium, and will spawn a medium meteor structure upon impact");
        configs.addKeyValuePair(new Pair<>("max_big_meteor_size", 20),"The maximum size of meteor that can be considered big, and will spawn a big meteor structure upon impact. Only these can spawn a meteor cat by default.");
        //V2
        configs.addKeyValuePair(new Pair<>("special_meteors_chance", 10), "The chance of spawning a special meteor structure in a certain size category (for example the meteor cat meteor) (works like the other chances, aka 1 in x probability)");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");
        configs.addKeyValuePair(new Pair<>("use_forced_particles", true),"Should meteor and laser particles be forced? They will be rendered further away and look better, but if there are too many of them you may want to disable this for lag reasons. Note: some particles will never displays as forced, like the lasers target box");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        //V3
        configs.addKeyValuePair(new Pair<>("spawn_dimensions", List.of(DimensionTypes.OVERWORLD_ID.toString(), DimensionTypes.THE_END_ID.toString())),"A list of the IDs of the dimensions in which meteors can naturally spawn in, vanilla or not.");
        configs.addKeyValuePair(new Pair<>("dimension_chances", DIMENSION_CHANCES_default), "A map consisting of dimension=chance of spawning. The dimension must be present in the list above, otherwise meteors won't spawn at all. This chance will ALWAYS ovveride the default if present. The spawn chance works as described above.");
        configs.addKeyValuePair(new Pair<>("dimension_night_chances", DIMENSION_NIGHT_CHANCES_default), "The same as above but with a possibly different chance at night if enabled");
        configs.addKeyValuePair(new Pair<>("biome_list_mode", false),"If set to false will behave like a blacklist, aka meteors won't spawn in those biomes. If true will behave like a whitelist, meteors will spawn ONLY in those biomes.");
        configs.addKeyValuePair(new Pair<>("biome_spawn_list", BIOME_SPAWN_LIST_default),"A black or whitelist of the biomes in which meteor will or will not spawn according to the setting above");

        configs.addKeyValuePair(new Pair<>("biome_chances", BIOME_CHANCES_default), "A map consisting of biome=chance of spawning. The spawn chance works as described above. The meteors must be able to spawn in those biomes, otherwise they won't!");
        configs.addKeyValuePair(new Pair<>("biome_night_chances", BIOME_NIGHT_CHANCES_default), "The same as above but with a possibly different chance at night if enabled");

        //V4
        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");

        configs.addKeyValuePair(new Pair<>("use_better_explosions", true),"If true will use a spherical explosion instead of the vanilla cubical one. These look nicer at higher explosion power/ranges, but after power 100 become quite laggy.");
        configs.addKeyValuePair(new Pair<>("meteor_render_distance", 200),"How far should meteors be rendered. WARNING: YOU NEED TO RESTART YOUR SERVER AND CLIENT IF YOU CHANGE THIS VALUE in order for it to take effect. It is multiplied by the 'entity render' distance of the server");

        //V5
        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");
        configs.addKeyValuePair(new Pair<>("meteor_showers_enabled", true),"If true, there will be a chance that meteor showers will spawn (a lot of meteors spawning at the same time & place. it can also cause brief lag spikes, nothing too dramatic tho)");
        configs.addKeyValuePair(new Pair<>("meteor_shower_chance", 100),"The chance for a meteor shower to spawn (on top of the normal meteor spawning chance, so it's meteor_spawn*meteor_shower spawn)");
        configs.addKeyValuePair(new Pair<>("min_meteors_in_shower", 5),"The minimum number of meteors that are going to spawn in the meteor shower.");
        configs.addKeyValuePair(new Pair<>("max_meteors_in_shower", 20),"The maximum number of meteors that are going to spawn in the meteor shower.");

        configs.addKeyValuePair(new Pair<>("spacer", "spacer"), "");
    }

    public static void reloadConfig(){
        registerConfigs();
        LOGGER.info("All " + configs.getConfigsList().size() + " have been reloaded properly");

    }

    private static void assignConfigs() {

        VERSION = CONFIG.getOrDefault("version", ver);

        MIN_METEOR_SPAWN_DISTANCE = CONFIG.getOrDefault("min_meteor_spawn_distance", 2);
        MAX_METEOR_SPAWN_DISTANCE = CONFIG.getOrDefault("max_meteor_spawn_distance", 30);
        MIN_METEOR_SPAWN_DISTANCE = CONFIG.getOrDefault("min_meteor_spawn_distance", 2);
        METEOR_SPAWN_HEIGHT = CONFIG.getOrDefault("meteor_spawn_height", 300);
        METEOR_SPAWN_CHANCE = CONFIG.getOrDefault("meteor_spawn_chance", 20000);
        MODIFY_SPAWN_CHANCE_AT_NIGHT = CONFIG.getOrDefault("modify_spawn_chance_at_night", false);
        METEOR_NIGHT_SPAWN_CHANCE = CONFIG.getOrDefault("meteor_night_spawn_chance", 10000);
        SPAWN_HUGE_METEORS = CONFIG.getOrDefault("spawn_huge_meteors", true);
        HUGE_METEOR_CHANCE = CONFIG.getOrDefault("huge_meteor_chance",  100);
        HUGE_METEOR_SIZE_LIMIT = CONFIG.getOrDefault("huge_meteor_size_limit", 35);

        NATURAL_METEOR_MIN_SIZE = CONFIG.getOrDefault("natural_meteor_min_size", 1);
        NATURAL_METEOR_MAX_SIZE = CONFIG.getOrDefault("natural_meteor_max_size", 10);

        SHOULD_BYPASS_LEAVES = CONFIG.getOrDefault("should_bypass_leaves", true);
        SHOULD_DESTROY_LEAVES = CONFIG.getOrDefault("should_destroy_leaves", true);
        HOMING_METEORS = CONFIG.getOrDefault("homing_meteors", false);

        SHOULD_COOLDOWN_BETWEEN_METEORS = CONFIG.getOrDefault("should_cooldown_between_meteors", true);
        MIN_METEOR_COOLDOWN_TIME = CONFIG.getOrDefault("min_meteor_cooldown_time", 20);

        ANNOUNCE_METEOR_SPAWN = CONFIG.getOrDefault("announce_meteor_spawn", false);
        ANNOUNCE_METEOR_DESTROYED = CONFIG.getOrDefault("announce_meteor_destroyed", false);

        ACTIONBAR_ANNOUNCEMENTS = CONFIG.getOrDefault("actionbar_announcements", true);

        METEOR_GRIEFING = CONFIG.getOrDefault("meteor_griefing", true);
        SCATTER_METEOR_GRIEFING = CONFIG.getOrDefault("scatter_meteor_griefing", true);
        METEOR_STRUCTURE = CONFIG.getOrDefault("meteor_structure", true);
        SCATTER_METEOR_STRUCTURE = CONFIG.getOrDefault("scatter_meteor_structure", true);
        
        BASIC_LASER_AREA_RADIUS = CONFIG.getOrDefault("basic_laser_area_radius", 32);
        BASIC_LASER_HEIGHT = CONFIG.getOrDefault("basic_laser_height", 64);

        ADVANCED_LASER_AREA_RADIUS = CONFIG.getOrDefault("advanced_laser_area_radius", 48);
        ADVANCED_LASER_HEIGHT = CONFIG.getOrDefault("advanced_laser_height", 64);
        
        SHOULD_BASIC_LASER_COOLDOWN = CONFIG.getOrDefault("should_basic_laser_cooldown", true);
        BASIC_LASER_COOLDOWN = CONFIG.getOrDefault("basic_laser_cooldown", 3);

        SHOULD_ADVANCED_LASER_COOLDOWN = CONFIG.getOrDefault("should_advanced_laser_cooldown", true);
        ADVANCED_LASER_COOLDOWN = CONFIG.getOrDefault("advanced_laser_cooldown", 3);

        MAX_SMALL_METEOR_SIZE = CONFIG.getOrDefault("max_small_meteor_size", 4);
        MAX_MEDIUM_METEOR_SIZE = CONFIG.getOrDefault("max_medium_meteor_size", 7);
        MAX_BIG_METEOR_SIZE = CONFIG.getOrDefault("max_big_meteor_size", 20);

        USE_FORCED_PARTICLES = CONFIG.getOrDefault("use_forced_particles", true);

        //V2
        SPECIAL_METEORS_CHANCE = CONFIG.getOrDefault("special_meteors_chance", 10);
        GLOBAL_EXPLOSION_SOUND = CONFIG.getOrDefault("global_explosion_sound", false);
        AREA_EXPLOSION_SOUND = CONFIG.getOrDefault("area_explosion_sound", false);
        AREA_EXPLOSION_SOUND_RADIUS = CONFIG.getOrDefault("area_explosion_sound_radius", 500);
        EXPLOSION_POWER_MODIFIER = CONFIG.getOrDefault("explosion_power_modifier", 0);

        //V3
        DOWNWARDS_SPEED_MODIFIER = CONFIG.getOrDefault("downwards_speed_modifier", 0);
        SPAWN_DIMENSIONS = CONFIG.getOrDefault("spawn_dimensions", List.of(DimensionTypes.OVERWORLD_ID.toString(), DimensionTypes.THE_END_ID.toString()));
        DIMENSION_CHANCES = CONFIG.getOrDefault("dimension_chances", DIMENSION_CHANCES_default);
        DIMENSION_NIGHT_CHANCES = CONFIG.getOrDefault("dimension_night_chances", DIMENSION_NIGHT_CHANCES_default);
        BIOME_LIST_MODE = CONFIG.getOrDefault("biome_list_mode", false);
        BIOME_SPAWN_LIST = CONFIG.getOrDefault("biome_spawn_list", BIOME_SPAWN_LIST_default);
        BIOME_CHANCES = CONFIG.getOrDefault("biome_chances", BIOME_CHANCES_default);
        BIOME_NIGHT_CHANCES = CONFIG.getOrDefault("biome_night_chances", BIOME_NIGHT_CHANCES_default);

        //V4
        USE_BETTER_EXPLOSIONS = CONFIG.getOrDefault("use_better_explosions", true);
        METEOR_RENDER_DISTANCE = CONFIG.getOrDefault("meteor_render_distance", 200);

        //V5
        METEOR_SHOWERS_ENABLED = CONFIG.getOrDefault("meteor_showers_enabled", true);
        METEOR_SHOWER_CHANCE = CONFIG.getOrDefault("meteor_shower_chance", 100);
        MIN_METEORS_IN_SHOWER = CONFIG.getOrDefault("min_meteors_in_shower", 5);
        MAX_METEORS_IN_SHOWER = CONFIG.getOrDefault("max_meteors_in_shower", 20);
        ONLY_REPLACE_AIR = CONFIG.getOrDefault("only_replace_air", false);
        SCATTER_ONLY_REPALCE_AIR = CONFIG.getOrDefault("only_repalcer_air_scatter", false);
    }
}

