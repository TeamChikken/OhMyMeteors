package me.emafire003.dev.ohmymeteors.commands.argument;

import com.mojang.brigadier.context.CommandContext;
import me.emafire003.dev.ohmymeteors.util.MeteorSizeClass;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.commands.CommandSourceStack;


public class MeteorSizeClassArgumentType extends StringRepresentableArgument<MeteorSizeClass> {

    protected MeteorSizeClassArgumentType() {
        super(MeteorSizeClass.CODEC, MeteorSizeClass::values);
    }

    public static StringRepresentableArgument<MeteorSizeClass> meteorSizeClass() {
        return new MeteorSizeClassArgumentType();
    }

    public static MeteorSizeClass getMeteorSizeClass(CommandContext<CommandSourceStack> context, String id) {
        return context.getArgument(id, MeteorSizeClass.class);
    }
}
