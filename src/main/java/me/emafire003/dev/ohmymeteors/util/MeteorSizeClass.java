package me.emafire003.dev.ohmymeteors.util;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Mirror;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

    public static final Codec<MeteorSizeClass> CODEC = StringRepresentable.fromEnum(MeteorSizeClass::values);

    public static final StreamCodec<ByteBuf, MeteorSizeClass> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    @Override
    public @NotNull String getSerializedName() {
        return this.name.toLowerCase();
    }
}
