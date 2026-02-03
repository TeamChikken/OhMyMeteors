package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorSizeClassArgumentType;
import me.emafire003.dev.ohmymeteors.compat.perms.PermissionsChecker;
import me.emafire003.dev.ohmymeteors.mixin.MinecraftServerSessionAccessor;
import me.emafire003.dev.ohmymeteors.util.MeteorSizeClass;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

import java.nio.file.Path;

public class CustomStructureCommand implements OMMCommand {

    private int addStructure(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            Identifier structureId = IdentifierArgumentType.getIdentifier(context, "structureId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            Path datapackDir = ((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.GENERATED).normalize().getParent().resolve("datapack");

            context.getSource().sendMessage(Text.literal("datadir: " + datapackDir));
            return 1;
        }catch (Exception e){
            context.getSource().sendError(Text.literal("[Oh My, Meteors!] ").append("§cThere has been an error while reloading the config, check the logs"));
            e.printStackTrace();
            return 0;
        }
    }

    public static void generateDatapack(Path datapackDir){
       // File
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> getNode(CommandRegistryAccess registryAccess) {
        return CommandManager
                .literal("custom")
                .requires(PermissionsChecker.hasPerms(OhMyMeteors.MOD_ID+".commands.custom", 2))
                /*.then(
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
                )*/

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

                .build();
    }
}
