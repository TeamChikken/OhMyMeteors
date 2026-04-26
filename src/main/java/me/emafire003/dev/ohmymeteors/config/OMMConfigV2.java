package me.emafire003.dev.ohmymeteors.config;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.fzzyhmstrs.fzzy_config.annotations.*;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.jetbrains.annotations.NotNull;

import java.lang.Integer;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unused")
@Version(version = 8)
@Translatable.Name("OhMyMeteors Config")
@AdminAccess(perms = {"ohmyemeteors.config.edit"}, fallback = 4)
public class OMMConfigV2 extends Config {

    public OMMConfigV2(){
        super(ResourceLocation.fromNamespaceAndPath(OhMyMeteors.MOD_ID, "ohmymeteors_config"));
    }

    @Name("Meteor Spawning")
    @Comment("Meteor Spawning")
    public MeteorSpawningSection meteorSpawning = new MeteorSpawningSection();

    public static class MeteorSpawningSection extends ConfigSection {

    //Group
        public ConfigGroup simpleSpawns = new ConfigGroup("simpleSpawns");

        @Comment("Expressed as '1 in <x>' chances of spawning a meteor each tick (similar to randomTickSpeed). " +
                "Setting it to a negative value will disable natural meteor spawn. " +
                "For example, by default it has a chance of 1 in 30000, so 0.03%, or 1/30000")
        public int meteor_spawn_chance = 30000;
        //configs.addKeyValuePair(new Pair<>("meteor_spawn_chance", 30000), );

        @Comment("Should the spawn rate be different during the night?")
        public boolean modify_spawn_chance_at_night = false;
        //configs.addKeyValuePair(new Pair<>("modify_spawn_chance_at_night", false),);

        @Comment("The chance for a meteor to spawn at night if enabled. Expressed as in 1 in <x> chances, as described above.")
        public int meteor_night_spawn_chance = 10000;
        //configs.addKeyValuePair(new Pair<>("meteor_night_spawn_chance", 10000),);

        @Comment("The chance of spawning a special meteor structure in a certain size category (for example the meteor cat meteor), on top of the normal meteor spawn chance. " +
                "(works like the other chances, aka 1 in <x> probability)")
        public int special_meteors_chance = 10;
        //configs.addKeyValuePair(new Pair<>("special_meteors_chance", 10), );

        @Comment("Should huge meteors be able to spawn? They are meteors bigger than the maximum size of the big ones")
        public boolean spawn_huge_meteors = true;
        //configs.addKeyValuePair(new Pair<>("spawn_huge_meteors", true),);

        @Comment("The chance for a spawned meteor to be of huge size. Expressed as in 1 in <x> chances. (on top of the 'normal' spawning chance)")
        public int huge_meteor_chance = 100;
        //configs.addKeyValuePair(new Pair<>("huge_meteor_chance", 100),);

        @Comment("Expressed in blocks, represents the min distance (as in a radius) from the origin of the meteor " +
                "(like a player) in which the meteor wont' spawn in. (Remember that it has an angled trajectory so it could end up in that area regardless)")
        public int min_meteor_spawn_distance = 5;
        //configs.addKeyValuePair(new Pair<>("min_meteor_spawn_distance", 2), );

        @Comment("Expressed in blocks, represents the max distance (as in a radius) from the origin of the meteor " +
                "(like a player) in which a meteor can spawn in. (Remember that it has an angled trajectory so it could end up in that area regardless)")
        public int max_meteor_spawn_distance = 25;
        //configs.addKeyValuePair(new Pair<>("max_meteor_spawn_distance", 25), );

        @Comment("The world height (y level) at which meteors spawn in the world")
        public int meteor_spawn_height = 300;
        //configs.addKeyValuePair(new Pair<>("meteor_spawn_height", 300), );

        @Comment("Should there be a cooldown between a meteor spawning one meteor and then another?")
        public boolean should_cooldown_between_meteors = true;
        //configs.addKeyValuePair(new Pair<>("should_cooldown_between_meteors", true),);

        @Comment("The minimum time interval (in seconds) between spawning a meteor and then another")
        public int min_meteor_cooldown_time = 20;
        //configs.addKeyValuePair(new Pair<>("min_meteor_cooldown_time", 20),);

        @ConfigGroup.Pop
        @Comment("The higher the value the less the meteor will go diagonally, and will keep mostly vertical. (randomness remains so it's not 100%). Generally, you should remain between 1 and 10.")
        public double meteor_dispersion_factor = 3.1;
        //configs.addKeyValuePair(new Pair<>("meteor_dispersion_factor", 3.1), );
    //Group
        public ConfigGroup spawnsPrecise = new ConfigGroup("spawnPrecise");

        @Comment("A list of the IDs of the dimensions in which meteors can or cannot naturally spawn in (see mode setting below), vanilla or not.")
        public List<String> spawn_dimensions = List.of(BuiltinDimensionTypes.OVERWORLD_EFFECTS.toString(), BuiltinDimensionTypes.END_EFFECTS.toString());
        //configs.addKeyValuePair(new Pair<>("spawn_dimensions", List.of(BuiltinDimensionTypes.OVERWORLD_EFFECTS.toString(), BuiltinDimensionTypes.END_EFFECTS.toString())),"A list of the IDs of the dimensions in which meteors can naturally spawn in, vanilla or not.");
//TODO new setting write in changelog and implenent
        @Comment("If set to false will behave like a blacklist, aka meteors won't spawn in those dimensions. If true will behave like a whitelist, meteors will spawn ONLY in those dimensions.")
        public boolean dimension_list_mode = true; //TODO migrate to enum?

        @Inline
        @Comment("A map consisting of dimension:chance of spawning. The dimension must one in which the meteor can spawn in as specified above, otherwise meteors won't spawn at all. This chance will ALWAYS override the default if present. The spawn chance works as described above.")
        public Map<String, Integer> dimension_chances =  Map.of(
                BuiltinDimensionTypes.END_EFFECTS.toString(), meteor_spawn_chance*10
        );
        //configs.addKeyValuePair(new Pair<>("dimension_chances", DIMENSION_CHANCES_default), );

        @Inline
        @Comment("The same as above but with a possibly different chance at night if enabled")
        public Map<String, Integer> dimension_night_chances = Map.of(
                BuiltinDimensionTypes.END_EFFECTS.toString(), meteor_night_spawn_chance*10
                );
        //configs.addKeyValuePair(new Pair<>("dimension_night_chances", DIMENSION_NIGHT_CHANCES_default), );

        @Inline
        @Comment("A list of the IDs of the biomes in which meteors can or cannot naturally spawn in (see mode setting below), vanilla or not.")
        public List<String> biome_spawn_list = List.of(Biomes.CHERRY_GROVE.location().toString(), Biomes.SOUL_SAND_VALLEY.location().toString(), "modname:modbiome");
        //configs.addKeyValuePair(new Pair<>("biome_spawn_list", BIOME_SPAWN_LIST_default),"A black or whitelist of the biomes in which meteor will or will not spawn according to the setting above");

        @Inline
        @Comment("If set to false will behave like a blacklist, aka meteors won't spawn in those biomes. If true will behave like a whitelist, meteors will spawn ONLY in those biomes.")
        public boolean biome_list_mode = false; //TODO migrate to enum?
        //configs.addKeyValuePair(new Pair<>("biome_list_mode", false),);

        @Inline
        @Comment("A map consisting of biome=chance of spawning. The spawn chance works as described above. The meteors must be able to spawn in those biomes, otherwise they won't!")
        public Map<String, Integer> biome_chances =  Map.of(
                Biomes.DESERT.location().toString(), meteor_spawn_chance-10,
                "modname:modbiome", 2025
        );
        //configs.addKeyValuePair(new Pair<>("biome_chances", BIOME_CHANCES_default), "A map consisting of biome=chance of spawning. The spawn chance works as described above. The meteors must be able to spawn in those biomes, otherwise they won't!");

        @ConfigGroup.Pop
        @Inline
        @Comment("The same as above but with a possibly different chance at night if enabled")
        public Map<String, Integer> biome_night_chances = Map.of(
                Biomes.DESERT.location().toString(), meteor_night_spawn_chance-5,
                "modname:modbiome", 2025
        );
        //configs.addKeyValuePair(new Pair<>("biome_night_chances", BIOME_NIGHT_CHANCES_default),);

    //Group
        public ConfigGroup meteorSizes = new ConfigGroup("meteorSizes");

        @ValidatedInt.Restrict(min = 1)
        @Comment("The smallest size a natural meteor can have when spawned in. Cannot go below 1")
        public int natural_meteor_min_size = 1;
        //configs.addKeyValuePair(new Pair<>("natural_meteor_min_size", 1), );

        @ValidatedInt.Restrict(max = 50)
        @Comment("The biggest size a natural meteor can have when spawned in. Cannot go above 50.")
        public int natural_meteor_max_size = 10;
        //configs.addKeyValuePair(new Pair<>("natural_meteor_max_size", 10),);

        @Comment("The maximum size of meteor that can be considered small, and that will spawn a small meteor structure upon impact")
        public int max_small_meteor_size = 4;
        //configs.addKeyValuePair(new Pair<>("max_small_meteor_size", 4), );

        @Comment("The maximum size of meteor that can be considered medium, and will spawn a medium meteor structure upon impact")
        public int max_medium_meteor_size = 7;
        //configs.addKeyValuePair(new Pair<>("max_medium_meteor_size", 7), );

        @Comment("The maximum size of meteor that can be considered big, and will spawn a big meteor structure upon impact. " +
                "Only these can spawn a meteor cat by default.")
        public int max_big_meteor_size = 20;
        //configs.addKeyValuePair(new Pair<>("max_big_meteor_size", 20),);

        @ConfigGroup.Pop
        @Comment("The max size limit of how big a huge meteor can be")
        public int huge_meteor_size_limit = 40;
        //configs.addKeyValuePair(new Pair<>("huge_meteor_size_limit", 40),"The size limit of how big a huge meteor can be");
    }

    @Name("Meteor Behaviour")
    @Comment("Meteor Behaviour")
    public MeteorBehaviourSection meteorBehaviourSection = new MeteorBehaviourSection();

    public static class MeteorBehaviourSection extends ConfigSection{

        @Comment("A factor to ADD to the explosion power (by default, the power is equal to the meteor size), thus increasing the damage and radius of the explosion. Also supports negative numbers")
        public int explosion_power_modifier = 0;
        //configs.addKeyValuePair(new Pair<>("explosion_power_modifier", 0), );
//TODO new setting implement and changelog
        @Comment("A factor to MULTIPLY the explosion power (by default, the power is equal to the meteor size), thus increasing the damage and radius of the explosion.")
        public float explosion_power_multiplier = 1.0f;

        @Comment("Should meteors be able to destroy blocks on impact?")
        public boolean meteor_griefing = true;
        //configs.addKeyValuePair(new Pair<>("meteor_griefing", true),);

        @Comment("Should meteors spawn the meteor structure after impact?")
        public boolean meteor_structure = true;
        //configs.addKeyValuePair(new Pair<>("meteor_structure", true),);

        @Comment("Should the meteor structure only replace air blocks?")
        public boolean only_replace_air = false;
        //configs.addKeyValuePair(new Pair<>("only_replace_air", false),);
//TODO new setting, wiki & implementation
        @Comment("Should scatter meteors be spawned when a Basic laser destroys a bigger meteor?")
        public boolean spawn_scatter_meteors = false;
        //configs.addKeyValuePair(new Pair<>("only_replace_air", false),);

        @Comment("Should the meteors that come out of a bigger meteor when it's broken be able to destroy blocks on impact?")
        public boolean scatter_meteor_griefing = true;
        //configs.addKeyValuePair(new Pair<>("scatter_meteor_griefing", true),);

        @Comment("Should the meteors that come out of a bigger meteor when it's broken be able to destroy spawn structures on impact?")
        public boolean scatter_meteor_structure = true;
        //configs.addKeyValuePair(new Pair<>("scatter_meteor_structure", true),);

        @Comment("Should the meteors that come out of a bigger meteor when it's broken only replace air blocks for their structure?")
        public boolean scatter_only_replace_air = true;
        //configs.addKeyValuePair(new Pair<>("scatter_only_replace_air", true),);

        @Comment("Should meteors explode when they come into contact with an entity? (if true you could use an arrow to make the meteor explode for example)")
        public boolean explode_on_entity_collision = false;
        //configs.addKeyValuePair(new Pair<>("explode_on_entity_collision", false), );

        @Comment("Should fire be spawned on meteor impact? (it looks cool!)")
        public boolean spawn_fire_with_meteor = true;
        //configs.addKeyValuePair(new Pair<>("spawn_fire_with_meteor", true),);

        @Comment( "If true will use a spherical explosion instead of the vanilla cubical one. These look nicer at higher explosion power/ranges, but after power 100 become a bit laggy.")
        public boolean use_better_explosions = true;
        //configs.addKeyValuePair(new Pair<>("use_better_explosions", true),);

        @Comment("A factor to ADD to the speed at which the meteor falls downwards. It is added to a randomly generated number between 1 and 0. Also supports negative numbers (the meteor may go upwards tho!)")
        public int downwards_speed_modifier = 0;
        //configs.addKeyValuePair(new Pair<>("downwards_speed_modifier", 0),);

//TODO new setting implement and changelog
        @Comment("A factor to MULTIPLY the speed at which the meteor falls downwards. It is added to a randomly generated number between 1 and 0")
        public double downwards_speed_multiplier = 1.0;

        @Comment("Should meteors be (more or less) directed towards the nearest player?")
        public boolean homing_meteors = false;
        //configs.addKeyValuePair(new Pair<>("homing_meteors", false),);

    }

    @Name("Notifications")
    @Comment("Notifications")
    public NotificationSection notificationSection = new NotificationSection();

    public static class NotificationSection extends ConfigSection{
        public NotificationSection(){
            super();
        }

        public ConfigGroup chatMessages = new ConfigGroup("chatMessages");
        @Comment("Should players get a message in chat/hotbar when a meteor spawns?")
        public boolean announce_meteor_spawn = false;
        //configs.addKeyValuePair(new Pair<>("announce_meteor_spawn", false), );

        @Comment("Should players get a message in chat/hotbar when a meteor is destroyed?")
        public boolean announce_meteor_destroyed = false;
        //configs.addKeyValuePair(new Pair<>("announce_meteor_destroyed", false),);

        @Comment("Should the (^above^) announcement be displayed above the hotbar in the actionbar or in chat?")
        public boolean actionbar_announcements = true;
        //configs.addKeyValuePair(new Pair<>("actionbar_announcements", true),);

        @ConfigGroup.Pop
        @Comment("If announcements are enabled, should they also display the (approximate) coordinates of the meteor being spawned/destroyed?")
        public boolean announce_location = true;
        //configs.addKeyValuePair(new Pair<>("announce_location", true),);

        public ConfigGroup explosionSounds = new ConfigGroup("explosionSounds");
        @Comment("Should the explosion sound of the meteor be heard by all players online?")
        public boolean global_explosion_sound = false;
        //configs.addKeyValuePair(new Pair<>("global_explosion_sound", false),);

        @Comment( "Should the explosion sound of the meteor be heard by all players around a certain area from the impact point?")
        public boolean area_explosion_sound = false;
        //configs.addKeyValuePair(new Pair<>("area_explosion_sound", false),);

        @ConfigGroup.Pop
        @Comment("The radius in blocks of the area in which the sound of the meteor will be heard if the option above is true")
        public int area_explosion_sound_radius = 500;
        //configs.addKeyValuePair(new Pair<>("area_explosion_sound_radius", 500),);

    }

    @Name("Lasers")
    @Comment("Lasers")
    public LasersSection lasersSection = new LasersSection();
    public static class LasersSection extends ConfigSection{
        public LasersSection(){
            super();
        }

        @Comment("The radius in blocks of the xz area covered by the Basic laser block, where meteors will be blown up")
        public int basic_laser_area_radius = 32;
        //configs.addKeyValuePair(new Pair<>("basic_laser_area_radius", 32),);

        @Comment("How many blocks up from the position of the basic laser should meteors be checked for? (note that the detection box is only 2 blocks thick, not the whole way)")
        public int basic_laser_height = 64;
        //configs.addKeyValuePair(new Pair<>("basic_laser_height", 64),);

        @Comment("The radius in blocks of the xz area covered by the advanced laser block, where meteors will be blown up")
        public int advanced_laser_area_radius = 48;
        //configs.addKeyValuePair(new Pair<>("advanced_laser_area_radius", 48),);

        @Comment("How many blocks up from the position of the advanced laser should meteors be checked for? (note that the detection box is only 2 blocks thick, not the whole way)")
        public int advanced_laser_height = 64;
        //configs.addKeyValuePair(new Pair<>("advanced_laser_height", 64),);

        @Comment("Should the laser be in a cooldown where it can't fire, after it has just fired?")
        public boolean should_basic_laser_cooldown = true;
        //configs.addKeyValuePair(new Pair<>("should_basic_laser_cooldown", true),);

        @Comment("How many seconds should this cooldown last?")
        public int basic_laser_cooldown = 5;
        //configs.addKeyValuePair(new Pair<>("basic_laser_cooldown", 3),);

        @Comment("Should the laser be in a cooldown where it can't fire, after it has just fired?")
        public boolean should_advanced_laser_cooldown = true;
        //configs.addKeyValuePair(new Pair<>("should_advanced_laser_cooldown", true),);

        @Comment("How many seconds should this cooldown last?")
        public int advanced_laser_cooldown = 3;
        //configs.addKeyValuePair(new Pair<>("advanced_laser_cooldown", 3),);

        //TODO wiki write the cooldown change in the changelog
    }

    @Name("Visuals")
    @Comment("Visuals")
    public VisualsSection visualsSection = new VisualsSection();
    public static class VisualsSection extends ConfigSection{
        public VisualsSection(){
            super();
        }

        @Comment("Should meteor and laser particles be forced? They will be rendered further away and look better, but if there are too many of them you may want to disable this for lag reasons. Note: some particles will never displays as forced, like the lasers target box")
        public boolean use_forced_particles = true;
        //configs.addKeyValuePair(new Pair<>("use_forced_particles", true),);

        //TODO should be .RESTART
        //@RequiresAction(action = Action.RELOG)
        @Comment("How far should meteors be rendered. WARNING: YOU NEED TO RESTART YOUR SERVER AND CLIENT IF YOU CHANGE THIS VALUE in order for it to take effect. It is multiplied by the 'entity' render distance of the server")
        public int meteor_render_distance = 200;
        //configs.addKeyValuePair(new Pair<>("meteor_render_distance", 200),);

        @Comment("If true, makes the sky glow a certain color when a meteor passes by. This only applies if the meteor is in rendering range and not world or server wide.")
        public boolean meteor_skyglow = true;
        //configs.addKeyValuePair(new Pair<>("meteor_skyglow", true),);

        //"#048da5" TODO reimplement correctly
        @Comment("The color to apply to the sky when a meteor passes by if it's enabled. By default it's a lightblue-cyan color. Bear in mind that Minecraft still applies its own colors, so some shades (like green) work less well than others (blue)")
        public ValidatedColor meteor_skyglow_color = new ValidatedColor(4,141,161);
        //configs.addKeyValuePair(new Pair<>("meteor_skyglow_color", "#048da5"),);

    }

    @Name("Meteor Showers")
    @Comment("Meteor Showers")
    public MeteorShowerSection meteorShowerSection = new MeteorShowerSection();
    public static class MeteorShowerSection extends ConfigSection{
        public MeteorShowerSection(){
            super();
        }

        @Comment("If true, there will be a chance that meteor showers will spawn (a lot of meteors spawning at the same time & place. It can also sometimes cause brief lag spikes, nothing too dramatic tho)")
        public boolean meteor_showers_enabled = true;
        //configs.addKeyValuePair(new Pair<>("meteor_showers_enabled", true),);

        @Comment("The chance for a meteor shower to spawn (on top of the normal meteor spawning chance, so it's meteor_spawn*meteor_shower_spawn)")
        public int meteor_shower_chance = 100;
        //configs.addKeyValuePair(new Pair<>("meteor_shower_chance", 100),);

        @Comment("The minimum number of meteors that are going to spawn in the meteor shower.")
        public int min_meteors_in_shower = 5;
        //configs.addKeyValuePair(new Pair<>("min_meteors_in_shower", 5),);

        @Comment("The maximum number of meteors that are going to spawn in the meteor shower.")
        public int max_meteors_in_shower = 15; //todo state change from the old default in the changelog
        //configs.addKeyValuePair(new Pair<>("max_meteors_in_shower", 20),);

        @Comment("The delay (in ticks) between each meteor that gets spawned in delayed and direction delayed meteor shower")
        public int meteor_shower_delay_ticks = 15;
        //configs.addKeyValuePair(new Pair<>("meteor_shower_delay_ticks", 15),);

    }

    @Override
    public @NotNull SaveType saveType() {
        return SaveType.SEPARATE;
    }

    @Override
    public int defaultPermLevel() {
        return 4;
    }


}
