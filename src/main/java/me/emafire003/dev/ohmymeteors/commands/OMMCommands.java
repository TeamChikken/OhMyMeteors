package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
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
}
