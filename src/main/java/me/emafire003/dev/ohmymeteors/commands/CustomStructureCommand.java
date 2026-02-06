package me.emafire003.dev.ohmymeteors.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorSizeClassArgumentType;
import me.emafire003.dev.ohmymeteors.compat.perms.PermissionsChecker;
import me.emafire003.dev.ohmymeteors.mixin.MinecraftServerSessionAccessor;
import me.emafire003.dev.ohmymeteors.util.MeteorSizeClass;
import me.emafire003.dev.ohmymeteors.util.packutils.PackMeta;
import me.emafire003.dev.ohmymeteors.util.packutils.PackUtilThing;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomStructureCommand implements OMMCommand {

    private int addStructure(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            Identifier structureId = IdentifierArgumentType.getIdentifier(context, "structureId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            //Generates the datapack folders
            generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.DATAPACKS));

            String struct_id = "/"+size.asString()+"/"+ structureId.getPath()+".nbt";
            if(special){
                struct_id = "/"+size.asString()+"/special/"+ structureId.getPath()+".nbt";
            }

            //The path of the "generated" folder where structures are saved
            String generated_path = ((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.GENERATED).toString();

            //copies the structure file from the generated directory into the datapack folder. sends error if the file already exists
            try{
                Files.copy(Path.of(generated_path+"/minecraft/structures/" + structureId.getPath()+".nbt"),
                        Path.of(PACK_DIR_STRUCTURE + struct_id));
            }catch (FileAlreadyExistsException e){
                context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", structureId.getPath())));
                context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                return 0;
            }

            if(special){
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.success", structureId.getPath(), size.asString()).append(Text.translatable("command.ohmymeteors.custom.special"))));
            }else{
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.success", structureId.getPath(), size.asString())));
            }
            context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.reload")));

            return 1;
        }catch (Exception e){
            Identifier structureId = IdentifierArgumentType.getIdentifier(context, "structureId");
            context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", structureId.getPath())));
            e.printStackTrace();
            return 0;
        }
    }

    //TODO add a rename thingy
    // TODO add command to add the ignorefiles thingy

    private int removeStructure(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            Identifier structureId = IdentifierArgumentType.getIdentifier(context, "structureId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            String struct_id = "/"+size.asString()+"/"+ structureId.getPath()+".nbt";
            if(special){
                struct_id = "/"+size.asString()+"/special/"+ structureId.getPath()+".nbt";
            }

            //deletes the file (if it exists)
            Files.deleteIfExists(Path.of(PACK_DIR_STRUCTURE + struct_id));

            context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.remove.success", structureId.getPath())));
            context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.reload")));

            return 1;
        }catch (Exception e){
            Identifier structureId = IdentifierArgumentType.getIdentifier(context, "structureId");
            context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.remove.failed", structureId.getPath())));
            e.printStackTrace();
            return 0;
        }
    }

    public static Path PACK_DIR_STRUCTURE;

    //TODO remember to update this for 1.20.1 and below!
    /** Generates the datapack folders and mcmeta file in the datapack folder given in its argument*/
    public static void generateDatapack(Path datapackDir) throws IOException {

        //creates the datapack directory
        Path PACK_DIR = Files.createDirectories(Path.of(datapackDir.toString() + "/ohymymeteors_generated/"));

        //creates the pack.mcmeta file
        try (FileWriter fileWriter = new FileWriter(String.valueOf(PACK_DIR.resolve("pack.mcmeta").toFile()), StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter, 4096)) {

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(new PackUtilThing(new PackMeta()), bufferedWriter);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //creates the structure directories
        PACK_DIR_STRUCTURE = Files.createDirectories(Path.of(PACK_DIR + "/data/" + OhMyMeteors.MOD_ID + "/structure/"));
        Files.createDirectories(Path.of(PACK_DIR_STRUCTURE + "/big/special/"));
        Files.createDirectories(Path.of(PACK_DIR_STRUCTURE + "/huge/special/"));
        Files.createDirectories(Path.of(PACK_DIR_STRUCTURE + "/medium/special/"));
        Files.createDirectories(Path.of(PACK_DIR_STRUCTURE + "/small/special/"));
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> getNode(CommandRegistryAccess registryAccess) {
        return CommandManager
                .literal("custom")
                .requires(PermissionsChecker.hasPerms(OhMyMeteors.MOD_ID+".commands.custom", 2))
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
                        CommandManager.literal("remove")
                                .then(
                                        CommandManager.argument("structureId", IdentifierArgumentType.identifier()
                                                )
                                                .then(
                                                        CommandManager.argument("sizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                .then(CommandManager.argument("special", BoolArgumentType.bool())
                                                                        .executes(this::removeStructure)
                                                                )
                                                )
                                )
                )
                .then(
                        CommandManager.literal("ignoredefaults")
                                .then(
                                        CommandManager.argument("ignore", BoolArgumentType.bool())
                                                .executes(this::addStructure)
                                )
                )
                .build();
    }
}
