package me.emafire003.dev.ohmymeteors.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum MeteorSizeClass implements StringRepresentable {
    SMALL("small"),
    MEDIUM("medium"),
    BIG("big"),
    HUGE("huge");

    private final String name;
    MeteorSizeClass(final String name){
        this.name = name;
    }

    public static final Codec<MeteorSizeClass> CODEC = StringRepresentable.fromEnum(MeteorSizeClass::values);

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
