package me.emafire003.dev.ohmymeteors.util;

import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;

public enum MeteorShowerType implements StringRepresentable {
    INSTANT("instant"),
    DELAYED("delayed"),
    DELAYED_DIRECTION("delayed_direction");

    private final String name;
    MeteorShowerType(final String name){
        this.name = name;
    }

    public static final EnumCodec<MeteorShowerType> CODEC = StringRepresentable.fromEnum(MeteorShowerType::values);

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
