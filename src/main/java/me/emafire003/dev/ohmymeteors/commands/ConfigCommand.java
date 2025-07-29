package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.compat.perms.PermissionsChecker;
import me.emafire003.dev.ohmymeteors.config.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class ConfigCommand implements OMMCommand {

    private int openConfig(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            ServerCommandSource source = context.getSource();
            //TODO test
            if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)){
                source.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.literal("Warning! The config file is located on the server, currently opening the your client's config! If you are an OP make sure to upload it on the server later!").formatted(Formatting.GOLD)));
            }

            Util.getOperatingSystem().open(Config.FILEPATH.toFile());

            source.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.literal("Make sure to use ").append(Text.literal("/omm config reload").formatted(Formatting.BLUE).append(Text.literal(" when you have finished editing the config file!").formatted(Formatting.RESET)))));
            return 1;
        }catch (Exception e){
            context.getSource().sendError(Text.literal("[Oh My, Meteors!] ").append("§cThere has been an error while reloading the config, check the logs"));
            e.printStackTrace();
            return 0;
        }
    }

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
                .literal("config")
                .requires(PermissionsChecker.hasPerms(OhMyMeteors.MOD_ID+".commands.config", 2))
                .then(
                        CommandManager.literal("reload").executes(this::reloadConfig)
                ).then(
                        CommandManager.literal("open").executes(this::openConfig)
                )

                .build();
    }
}
