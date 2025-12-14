package me.emafire003.dev.ohmymeteors.commands.argument;

import com.mojang.brigadier.context.CommandContext;
import me.emafire003.dev.ohmymeteors.util.MeteorSizeClass;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;


public class MeteorSizeClassArgumentType extends EnumArgumentType<MeteorSizeClass> {

    protected MeteorSizeClassArgumentType() {
        super(MeteorSizeClass.CODEC, MeteorSizeClass::values);
    }

    public static EnumArgumentType<MeteorSizeClass> meteorSizeClass() {
        return new MeteorSizeClassArgumentType();
    }

    public static MeteorSizeClass getMeteorSizeClass(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, MeteorSizeClass.class);
    }
}
