package me.emafire003.dev.ohmymeteors.util;

import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;

public enum MeteorSizeClass implements StringRepresentable {
    SMALL("small"),
    MEDIUM("medium"),
    BIG("big"),
    HUGE("huge");

    private final String name;
    MeteorSizeClass(final String name){
        this.name = name;
    }

    public static final EnumCodec<MeteorSizeClass> CODEC = StringRepresentable.fromEnum(MeteorSizeClass::values);

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
