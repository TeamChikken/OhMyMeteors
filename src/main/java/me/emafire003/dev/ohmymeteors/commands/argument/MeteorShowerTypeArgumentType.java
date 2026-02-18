package me.emafire003.dev.ohmymeteors.commands.argument;

import com.mojang.brigadier.context.CommandContext;
import me.emafire003.dev.ohmymeteors.util.MeteorShowerType;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.commands.CommandSourceStack;


public class MeteorShowerTypeArgumentType extends StringRepresentableArgument<MeteorShowerType> {

    protected MeteorShowerTypeArgumentType() {
        super(MeteorShowerType.CODEC, MeteorShowerType::values);
    }

    public static StringRepresentableArgument<MeteorShowerType> meteorShowerType() {
        return new MeteorShowerTypeArgumentType();
    }

    public static MeteorShowerType getMeteorShowerType(CommandContext<CommandSourceStack> context, String id) {
        return context.getArgument(id, MeteorShowerType.class);
    }
}
