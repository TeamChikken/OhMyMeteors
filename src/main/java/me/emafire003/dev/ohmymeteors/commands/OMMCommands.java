package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorSizeClassArgumentType;
import me.emafire003.dev.ohmymeteors.util.MeteorSizeClass;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class OMMCommands {

    //Based on Factions' code https://github.com/ickerio/factions
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralCommandNode<ServerCommandSource> omm_commands = CommandManager
                .literal("omm")
                .requires(CommandManager.requirePermissionLevel(CommandManager.ADMINS_CHECK))
                .build();

        dispatcher.getRoot().addChild(omm_commands);

        LiteralCommandNode<ServerCommandSource> pal_alias = CommandManager
                .literal("ohmymeteors")
                .requires(CommandManager.requirePermissionLevel(CommandManager.ADMINS_CHECK))
                .build();


        dispatcher.getRoot().addChild(pal_alias);

        OMMCommand[] commands = new OMMCommand[] {
                new SpawnMeteorCommand(),
                new ConfigCommand()
        };

        for (OMMCommand command : commands) {
            omm_commands.addChild(command.getNode(registryAccess));
        }
    }

    public static void registerArguments(){
        ArgumentTypeRegistry.registerArgumentType(OhMyMeteors.getIdentifier("meteor_size_class"), MeteorSizeClassArgumentType.class, ConstantArgumentSerializer.of(MeteorSizeClassArgumentType::meteorSizeClass));

    }
}
