package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;


//Based on Factions' code https://github.com/ickerio/factions (MIT license)
public interface OMMCommand {
    LiteralCommandNode<CommandSourceStack> getNode(CommandBuildContext registryAccess);

}
