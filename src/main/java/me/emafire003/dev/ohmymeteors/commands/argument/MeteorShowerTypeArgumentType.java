package me.emafire003.dev.ohmymeteors.commands.argument;

import com.mojang.brigadier.context.CommandContext;
import me.emafire003.dev.ohmymeteors.util.MeteorShowerType;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;


public class MeteorShowerTypeArgumentType extends EnumArgumentType<MeteorShowerType> {

    protected MeteorShowerTypeArgumentType() {
        super(MeteorShowerType.CODEC, MeteorShowerType::values);
    }

    public static EnumArgumentType<MeteorShowerType> meteorShowerType() {
        return new MeteorShowerTypeArgumentType();
    }

    public static MeteorShowerType getMeteorShowerType(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, MeteorShowerType.class);
    }
}
