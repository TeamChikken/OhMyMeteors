package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorSizeClassArgumentType;
import me.emafire003.dev.ohmymeteors.compat.perms.PermissionsChecker;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.util.MeteorSizeClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class CustomStructureCommand implements OMMCommand {

    private int openConfig(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            ServerCommandSource source = context.getSource();
            if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)){
                source.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.literal("Warning! The config file is located on the server, go to your server's config folder and edit '").formatted(Formatting.GOLD).append(Text.literal(Config.FILEPATH.toFile().toString()).formatted(Formatting.LIGHT_PURPLE).append(Text.literal("'").formatted(Formatting.GOLD)))));
                return 2;
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


    private int addStructure(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            Identifier structureId = IdentifierArgumentType.getIdentifier(context, "structureId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            
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
                .literal("custom")
                .requires(PermissionsChecker.hasPerms(OhMyMeteors.MOD_ID+".commands.custom", 2))
                .then(
                        CommandManager.literal("add")
                                .then(
                                        CommandManager.argument("name", StringArgumentType.string()
                                                )
                                        .then(
                                               CommandManager.argument("corner_1_pos", Vec3ArgumentType.vec3())
                                                       .then(
                                                               CommandManager.argument("corner_2_pos", Vec3ArgumentType.vec3())
                                                                       .then(
                                                                               CommandManager.argument("sizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                                       .then(CommandManager.argument("special", BoolArgumentType.bool())
                                                                                               .executes(null)
                                                                                       )
                                                                       )
                                               )
                                        )

                                )
                )

                .then(
                        CommandManager.literal("add")
                                .then(
                                        CommandManager.argument("structureId", IdentifierArgumentType.identifier()
                                                )
                                                .then(
                                                        CommandManager.argument("sizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                .then(CommandManager.argument("special", BoolArgumentType.bool())
                                                                        .executes(this::addStructure)
                                                                )
                                                )
                                )
                )

                .then(
                        CommandManager.literal("remove").executes(this::openConfig)
                )

                .build();
    }
}
