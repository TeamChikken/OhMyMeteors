package me.emafire003.dev.ohmymeteors.util;

import com.mojang.serialization.Codec;
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

@Translatable.Name("Meteor Texture mode")
public enum MeteorEntityTextureMode implements StringRepresentable, EnumTranslatable {
    @Name("DYNAMIC_HEIGHT")
    DYNAMIC_HEIGHT("DYNAMIC_HEIGHT"),
    @Name("DYNAMIC_DISTANCE")
    DYNAMIC_DISTANCE("DYNAMIC_DISTANCE"),
    @Name("DYNAMIC_AUTO")
    DYNAMIC_AUTO("DYNAMIC_AUTO"),
    @Name("NORMAL")
    NORMAL("NORMAL"),
    @Name("MID")
    MID("MID"),
    @Name("HOT")
    HOT("HOT");

    private final String name;
    MeteorEntityTextureMode(final String name){
        this.name = name;
    }

    public static final Codec<MeteorEntityTextureMode> CODEC = StringRepresentable.fromEnum(MeteorEntityTextureMode::values);

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    @NotNull
    @Override
    public String prefix() {
        return "ohmymeteors.texture_mode_enum";
    }

}
