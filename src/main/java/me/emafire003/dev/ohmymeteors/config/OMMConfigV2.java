package me.emafire003.dev.ohmymeteors.config;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.fzzyhmstrs.fzzy_config.annotations.AdminAccess;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Inline;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Version(version = 8)
@AdminAccess(perms = {"ohmyemeteors.config.edit"}, fallback = 4)
public class OMMConfigV2 extends Config {

    public OMMConfigV2(){
        super(ResourceLocation.fromNamespaceAndPath(OhMyMeteors.MOD_ID, "ohmymeteors_config"), OhMyMeteors.MOD_ID);
    }

    public static class MeteorSpawningSection extends ConfigSection{

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
        //V7

        @Comment("The higher the value the less the meteor will go diagonally, and will keep mostly vertical. (randomness remains so it's not 100%). Generally, you should remain between 1 and 10.")
        public double meteor_dispersion_factor = 3.1;
        //configs.addKeyValuePair(new Pair<>("meteor_dispersion_factor", 3.1), );

        @ConfigGroup.Pop
        public ConfigGroup spawnsPrecise = new ConfigGroup("spawnPrecise");

        @Comment("A list of the IDs of the dimensions in which meteors can or cannot naturally spawn in (see mode setting below), vanilla or not.")
        public List<String> spawn_dimensions = List.of(BuiltinDimensionTypes.OVERWORLD_EFFECTS.toString(), BuiltinDimensionTypes.END_EFFECTS.toString());
        //TODO add the other settings for the whitelist/blacklist
        //configs.addKeyValuePair(new Pair<>("spawn_dimensions", List.of(BuiltinDimensionTypes.OVERWORLD_EFFECTS.toString(), BuiltinDimensionTypes.END_EFFECTS.toString())),"A list of the IDs of the dimensions in which meteors can naturally spawn in, vanilla or not.");

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

        @Inline
        @Comment("The same as above but with a possibly different chance at night if enabled")
        public Map<String, Integer> biome_night_chances = Map.of(
                Biomes.DESERT.location().toString(), meteor_night_spawn_chance-5,
                "modname:modbiome", 2025
        );
        //configs.addKeyValuePair(new Pair<>("biome_night_chances", BIOME_NIGHT_CHANCES_default),);

        @ConfigGroup.Pop
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

        @Comment("The max size limit of how big a huge meteor can be")
        public int huge_meteor_size_limit = 40;
        //configs.addKeyValuePair(new Pair<>("huge_meteor_size_limit", 40),"The size limit of how big a huge meteor can be");

    }

    public static class MeteorBehaviourSection extends ConfigSection{

    }

    //TODO maybe figure out a better name?
    public static class AnnounceSection extends ConfigSection{

    }

    public static class LasersSection extends ConfigSection{

    }

    public static class VisualsSection extends ConfigSection{

    }

    public static class MeteorShowerSection extends ConfigSection{

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
