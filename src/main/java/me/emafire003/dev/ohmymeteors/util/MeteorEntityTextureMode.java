package me.emafire003.dev.ohmymeteors.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MeteorEntityTextureMode implements StringRepresentable {
    DYNAMIC_HEIGHT("DYNAMIC_HEIGHT"),
    DYNAMIC_DISTANCE("DYNAMIC_DISTANCE"),
    DYNAMIC_AUTO("DYNAMIC_AUTO"),
    NORMAL("NORMAL"),
    MID("MID"),
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


}
