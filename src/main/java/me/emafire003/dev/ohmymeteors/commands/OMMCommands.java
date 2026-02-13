package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorShowerTypeArgumentType;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorSizeClassArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public class OMMCommands {


    //Based on Factions' code https://github.com/ickerio/factions
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        LiteralCommandNode<CommandSourceStack> omm_commands = Commands
                .literal("omm")
                .requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                .build();

        dispatcher.getRoot().addChild(omm_commands);

        LiteralCommandNode<CommandSourceStack> pal_alias = Commands
                .literal("ohmymeteors")
                .requires(serverCommandSource -> serverCommandSource.hasPermission(2))
                .build();


        dispatcher.getRoot().addChild(pal_alias);

        OMMCommand[] commands = new OMMCommand[] {
                new SpawnMeteorCommand(),
                new ConfigCommand(),
                new CustomStructureCommand()
        };

        for (OMMCommand command : commands) {
            omm_commands.addChild(command.getNode(registryAccess));
        }
    }

    public static void registerArguments(){
        ArgumentTypeRegistry.registerArgumentType(OhMyMeteors.getIdentifier("meteor_size_class"), MeteorSizeClassArgumentType.class, SingletonArgumentInfo.contextFree(MeteorSizeClassArgumentType::meteorSizeClass));
        ArgumentTypeRegistry.registerArgumentType(OhMyMeteors.getIdentifier("meteor_shower_type"), MeteorShowerTypeArgumentType.class, SingletonArgumentInfo.contextFree(MeteorShowerTypeArgumentType::meteorShowerType));

    }
}
