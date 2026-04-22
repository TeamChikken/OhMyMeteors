package me.emafire003.dev.ohmymeteors.util;

import com.mojang.serialization.Codec;
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import me.fzzyhmstrs.fzzy_config.util.Translatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

@Translatable.Name("Particle Mode")
public enum ParticleMode implements StringRepresentable, EnumTranslatable {
    @Name("FANCY")
    FANCY("FANCY"),
    @Name("LESS")
    LESS("LESS"),
    @Name("MINIMAL")
    MINIMAL("MINIMAL"),
    @Name("NONE")
    NONE("NONE");

    private final String name;
    ParticleMode(final String name){
        this.name = name;
    }

    public static final Codec<ParticleMode> CODEC = StringRepresentable.fromEnum(ParticleMode::values);

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    @NotNull
    @Override
    public String prefix() {
        return "ohmymeteors.particle_mode_enum";
    }

}
