package me.emafire003.dev.ohmymeteors.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.StringIdentifiable;

public enum MeteorSizeClass implements StringIdentifiable {
    SMALL("small"),
    MEDIUM("medium"),
    BIG("big"),
    HUGE("huge");

    private final String name;
    MeteorSizeClass(final String name){
        this.name = name;
    }

    public static final Codec<MeteorSizeClass> CODEC = StringIdentifiable.createCodec(MeteorSizeClass::values);

    @Override
    public String asString() {
        return this.name;
    }
}
