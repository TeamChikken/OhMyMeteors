package me.emafire003.dev.ohmymeteors.util;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;

public class OMMTags {

    public static final TagKey<Block> METEOR_BYPASSES = create("meteor_bypasses");
    public static final TagKey<Block> METEOR_BYPASSES_AND_DESTROY = create("meteor_bypasses_and_destroy");

    public static TagKey<Block> create(String id){
        return TagKey.create(Registries.BLOCK, OhMyMeteors.getIdentifier(id));
    }

    public static void registerTags(){

    }
}
