package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.config.Config;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadConfigCommand implements OMMCommand {

    private int reloadConfig(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            Config.reloadConfig();
            context.getSource().sendMessage(Text.literal("[Oh My, Meteors!] Config reloaded!"));
            return 1;
        }catch (Exception e){
            context.getSource().sendError(Text.literal("[Oh My, Meteors!] ").append("§cThere has been an error while reloading the config, check the logs"));
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> getNode(CommandRegistryAccess registryAccess) {
        return CommandManager
                .literal("reload")
                .executes(this::reloadConfig)
                .build();
    }
}
