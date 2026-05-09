package me.emafire003.dev.ohmymeteors.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ParticleMode implements StringRepresentable {
    FANCY("fancy"),
    MINIMAL("minimal"),
    NONE("none");

    private final String name;
    ParticleMode(final String name){
        this.name = name;
    }

    public static final Codec<ParticleMode> CODEC = StringRepresentable.fromEnum(ParticleMode::values);

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
