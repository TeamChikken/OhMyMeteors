package me.emafire003.dev.ohmymeteors.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum MeteorShowerType implements StringIdentifiable {
    INSTANT("instant"),
    DELAYED("delayed"),
    DELAYED_DIRECTION("delayed_direction");

    private final String name;
    MeteorShowerType(final String name){
        this.name = name;
    }

    public static final Codec<MeteorShowerType> CODEC = StringIdentifiable.createCodec(MeteorShowerType::values);

    @Override
    public String asString() {
        return this.name;
    }
}
