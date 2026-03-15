package me.emafire003.dev.ohmymeteors.util;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MeteorSizeClass implements StringRepresentable {
    SMALL("SMALL"),
    MEDIUM("MEDIUM"),
    BIG("BIG"),
    HUGE("HUGE");

    private final String name;
    MeteorSizeClass(final String name){
        this.name = name;
    }

    public static final EnumCodec<MeteorSizeClass> CODEC = StringRepresentable.fromEnum(MeteorSizeClass::values);

    @Override
    public @NotNull String getSerializedName() {
        return this.name.toLowerCase();
    }
}
