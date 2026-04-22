package me.emafire003.dev.ohmymeteors.blocks;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum RockTemperature implements StringRepresentable {
    HOT("hot"),
    MID("mid"),
    NORMAL("normal");

    private final String name;
    RockTemperature(final String name){
        this.name = name;
    }

    public static final Codec<RockTemperature> CODEC = StringRepresentable.fromEnum(RockTemperature::values);

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
