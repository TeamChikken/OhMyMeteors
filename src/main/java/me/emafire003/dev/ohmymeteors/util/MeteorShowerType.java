package me.emafire003.dev.ohmymeteors.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum MeteorShowerType implements StringRepresentable {
    INSTANT("instant"),
    DELAYED("delayed"),
    DELAYED_DIRECTION("delayed_direction");

    private final String name;
    MeteorShowerType(final String name){
        this.name = name;
    }

    public static final Codec<MeteorShowerType> CODEC = StringRepresentable.fromEnum(MeteorShowerType::values);

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }
}
