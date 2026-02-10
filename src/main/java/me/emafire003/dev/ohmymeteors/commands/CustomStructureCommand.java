package me.emafire003.dev.ohmymeteors.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorSizeClassArgumentType;
import me.emafire003.dev.ohmymeteors.compat.perms.PermissionsChecker;
import me.emafire003.dev.ohmymeteors.compat.schemconvert.SchemConvertCompat;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.mixin.MinecraftServerSessionAccessor;
import me.emafire003.dev.ohmymeteors.util.MeteorSizeClass;
import me.emafire003.dev.ohmymeteors.util.packutils.PackMeta;
import me.emafire003.dev.ohmymeteors.util.packutils.PackUtilThing;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
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
                if(!Files.exists(Path.of(generated_path+"/minecraft/structures/" + structureId.getPath()+".nbt"))){
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed.origin_not_found", structureId.getPath()+".nbt")));
                    OhMyMeteors.LOGGER.warn("The path in which the file was searched: " + Path.of(generated_path+"/minecraft/structures/" + structureId.getPath()+".nbt"));
                    return 0;
                }
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

    private int addStructureWE(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            String schemId = StringArgumentType.getString(context, "schemId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            if(Config.SCHEMCONVERT_PRESENT || FabricLoader.getInstance().isDevelopmentEnvironment()){
                //the path where worldedit schematics are stored
                Path we_schempath = Path.of(FabricLoader.getInstance().getConfigDir().normalize().toString()+"/worldedit/schematics/");

                //TODO test the thing here if it works
                schemId = schemId.replaceAll("\\.schem", "");

                String struct_id = "/"+size.asString()+"/"+ schemId.toLowerCase()+".nbt";
                if(special){
                    struct_id = "/"+size.asString()+"/special/"+ schemId.toLowerCase()+".nbt";
                }

                //Generates the datapack folders
                try {
                    generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.DATAPACKS));
                } catch (IOException e) {
                    e.printStackTrace();
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                    return 0;
                }

                //copies the structure file from the generated directory into the datapack folder. sends error if the file already exists
                try{
                    if(Files.exists(Path.of(PACK_DIR_STRUCTURE + struct_id))){
                        context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                        context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                        return 0;
                    }
                    if(!Files.exists(Path.of(we_schempath+ "/" +schemId+".schem"))){
                        context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed.origin_not_found", schemId+".schem")));
                        OhMyMeteors.LOGGER.warn("The path in which the file was searched: " + Path.of(we_schempath+ "/" +schemId+".schem"));
                        return 0;
                    }
                    SchemConvertCompat.convertToNbt(new File(we_schempath+ "/" +schemId+".schem"), Path.of(PACK_DIR_STRUCTURE + struct_id).toFile());
                }catch (FileAlreadyExistsException e){
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                    return 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                }

                if(special){
                    context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.success", schemId, size.asString()).append(Text.translatable("command.ohmymeteors.custom.special"))));
                }else{
                    context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.success", schemId, size.asString())));
                }
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.reload")));

                return 1;
            }else{
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX)
                        .append(Text.translatable("command.ohmymeteors.custom.add.3dpary_schems", "WorldEdit §7.schem§r")));
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX)
                        .append(Text.translatable("command.ohmymeteors.custom.add.schemconvert_link")
                                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                        "https://github.com/PiTheGuy/SchemConvert/releases")))));
                return 0;
            }
        }catch (Exception e){
            String schemId = StringArgumentType.getString(context, "schemId");
            context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
            e.printStackTrace();
            return 0;
        }
    }

    private int addStructureLitematica(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            String schemId = StringArgumentType.getString(context, "schemId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            if(Config.SCHEMCONVERT_PRESENT || FabricLoader.getInstance().isDevelopmentEnvironment()){
                //the path where litematica schematics are stored
                Path lm_schempath = Path.of(FabricLoader.getInstance().getConfigDir().getParent().normalize().toString()+"/schematics/");

                schemId = schemId.replaceAll("\\.litematic", "");

                String struct_id = "/"+size.asString()+"/"+ schemId.toLowerCase()+".nbt";
                if(special){
                    struct_id = "/"+size.asString()+"/special/"+ schemId.toLowerCase()+".nbt";
                }

                //Generates the datapack folders
                try {
                    generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.DATAPACKS));
                } catch (IOException e) {
                    e.printStackTrace();
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                    return 0;
                }

                //copies the structure file from the generated directory into the datapack folder. sends error if the file already exists
                try{
                    if(Files.exists(Path.of(PACK_DIR_STRUCTURE + struct_id))){
                        context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                        context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                        return 0;
                    }
                    if(!Files.exists(Path.of(lm_schempath+ "/" +schemId+".litematic"))){
                        context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed.origin_not_found", schemId+".litematic")));
                        OhMyMeteors.LOGGER.warn("The path in which the file was searched: " + Path.of(lm_schempath+ "/" +schemId+".litematic"));
                        return 0;
                    }
                    SchemConvertCompat.convertToNbt(new File(lm_schempath+ "/" +schemId+".litematic"), Path.of(PACK_DIR_STRUCTURE + struct_id).toFile());
                }catch (FileAlreadyExistsException e){
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                    return 0;
                } catch (IOException e) {
                    e.printStackTrace();
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                }

                if(special){
                    context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.success", schemId, size.asString()).append(Text.translatable("command.ohmymeteors.custom.special"))));
                }else{
                    context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.success", schemId, size.asString())));
                }
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.reload")));

                return 1;
            }else{
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX)
                        .append(Text.translatable("command.ohmymeteors.custom.add.3dpary_schems", "Litematica §7.litematic§r")));
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX)
                        .append(Text.translatable("command.ohmymeteors.custom.add.schemconvert_link")
                                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                        "https://github.com/PiTheGuy/SchemConvert/releases")))));
                return 0;
            }
        }catch (Exception e){
            String schemId = StringArgumentType.getString(context, "schemId");
            context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.add.failed", schemId)));
            e.printStackTrace();
            return 0;
        }
    }


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

    private int edit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            String og_structureId = StringArgumentType.getString(context, "originalName");
            MeteorSizeClass og_size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "ogSizeClass");
            boolean og_special = BoolArgumentType.getBool(context, "ogSpecial");
            String new_structureId = StringArgumentType.getString(context, "newName");
            MeteorSizeClass new_size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "newSizeClass");
            boolean new_special = BoolArgumentType.getBool(context, "newSpecial");

            //Generates the datapack folders
            generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.DATAPACKS));

            String og_struct_id = "/"+og_size.asString()+"/"+ og_structureId+".nbt";
            if(og_special){
                og_struct_id = "/"+og_size.asString()+"/special/"+ og_structureId+".nbt";
            }

            String new_struct_id = "/"+new_size.asString()+"/"+ new_structureId+".nbt";
            if(new_special){
                new_struct_id = "/"+new_size.asString()+"/special/"+ new_structureId+".nbt";
            }

            if(!Files.exists(Path.of(PACK_DIR_STRUCTURE + og_struct_id))){
                if(og_special){
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.edit.file_not_found", og_structureId, og_size.toString(), "✔")));
                }else{
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.edit.file_not_found", og_structureId, og_size.toString(), "x")));
                }
                return 0;
            }

            //copies the structure file from the generated directory into the datapack folder. sends error if the file already exists
            try{
                Files.move(Path.of(PACK_DIR_STRUCTURE + og_struct_id),
                        Path.of(PACK_DIR_STRUCTURE + new_struct_id));
                if(new_special){
                    context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.edit.success", og_structureId, new_structureId, new_size.toString())
                            .append(Text.translatable("command.ohmymeteors.custom.special"))));
                }else{
                    context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.edit.success", og_structureId, new_structureId, new_size.toString())
                            .append(Text.translatable("command.ohmymeteors.custom.not_special"))));
                }

            }catch (FileAlreadyExistsException e){
                context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.edit.dest_file_already_exists", new_structureId)));
                return 0;
            }

            context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.reload")));

            return 1;
        }catch (Exception e){
            String name = StringArgumentType.getString(context, "originalName");
            context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.edit.failed", name)));
            e.printStackTrace();
            return 0;
        }
    }

    private int ignoreDefaults(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            boolean activate = BoolArgumentType.getBool(context, "ignore");

            //Generates the datapack folders
            generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.DATAPACKS));

            if(activate){
                //TODO note (so not actually a todo): this could also be used to get the path of a structure file.
                //Path path = context.getSource().getWorld().getStructureTemplateManager().getTemplatePath(OhMyMeteors.getIdentifier("error"), ".nbt");

                //TODO write in the changelog/wiki that the ignore_thing can just be any file. aslo maybe update the default datapack thingy
                try{
                    Files.createFile(Path.of(PACK_DIR_STRUCTURE + "/ignoredefault.nbt"));
                    context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.ignoredefaults.on")));
                }catch (FileAlreadyExistsException e){
                    context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.ignoredefaults.already_on")));
                    return 0;
                }
            }else{
                Files.deleteIfExists(Path.of(PACK_DIR_STRUCTURE + "/ignoredefault.nbt"));
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.ignoredefaults.off")));
            }
            return 1;
        }catch (Exception e){
            context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.ignoredefaults.failed")));
            e.printStackTrace();
            return 0;
        }
    }

    private int displayCurrent(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try{
            context.getSource().sendMessage(Text.literal(String.valueOf(Path.of((((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.DATAPACKS))+"/ohmymeteors_generated/pack.mcmeta"))));
            //TODO what the heck? it always says the file is not there :/
            /*if(!Files.exists(Path.of((((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.DATAPACKS))+"/ohmymeteors_generated/pack.mcmeta"))){
                context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.not_generated")));
                return 1;
            }*/
            
            generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getSession().getDirectory(WorldSavePath.DATAPACKS));

            //Cheks and lists small meteors
            Path smalls = Path.of(PACK_DIR_STRUCTURE+"/small/");
            context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.small").append(Text.literal(": "))));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(smalls)) {
                for (Path file : stream) {
                    if(file.getFileName().toString().equals("special")){
                        context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.small").append(Text.literal(" ")).append(Text.translatable("command.ohmymeteors.custom.display.special")).append(Text.literal(": "))));
                        try (DirectoryStream<Path> stream_s = Files.newDirectoryStream(file)) {
                            for (Path file_s : stream_s) {
                                context.getSource().sendMessage(Text.literal("§7-§r "+ file_s.getFileName()));
                            }
                        }
                    }else{
                        context.getSource().sendMessage(Text.literal("§7-§r "+ file.getFileName()));
                    }

                }
            }


            //Medium ones
            Path mediums = Path.of(PACK_DIR_STRUCTURE+"/medium/");
            context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.medium").append(Text.literal(": "))));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(mediums)) {
                for (Path file : stream) {
                    if(file.getFileName().toString().equals("special")){
                        context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.medium").append(Text.literal(" ")).append(Text.translatable("command.ohmymeteors.custom.display.special")).append(Text.literal(": "))));
                        try (DirectoryStream<Path> stream_s = Files.newDirectoryStream(file)) {
                            for (Path file_s : stream_s) {
                                context.getSource().sendMessage(Text.literal("§7-§r "+ file_s.getFileName()));
                            }
                        }
                    }else{
                        context.getSource().sendMessage(Text.literal("§7-§r "+ file.getFileName()));
                    }
                }
            }

            Path bigs = Path.of(PACK_DIR_STRUCTURE+"/big/");
            context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.big").append(Text.literal(": "))));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(bigs)) {
                for (Path file : stream) {
                    if(file.getFileName().toString().equals("special")){
                        context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.big").append(Text.literal(" ")).append(Text.translatable("command.ohmymeteors.custom.display.special")).append(Text.literal(": "))));
                        try (DirectoryStream<Path> stream_s = Files.newDirectoryStream(file)) {
                            for (Path file_s : stream_s) {
                                context.getSource().sendMessage(Text.literal("§7-§r "+ file_s.getFileName()));
                            }
                        }
                    }else{
                        context.getSource().sendMessage(Text.literal("§7-§r "+ file.getFileName()));
                    }

                }
            }

            Path huges = Path.of(PACK_DIR_STRUCTURE+"/huge/");
            context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.huge").append(Text.literal(": "))));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(huges)) {
                for (Path file : stream) {
                    if(file.getFileName().toString().equals("special")){
                        context.getSource().sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.huge").append(Text.literal(" ")).append(Text.translatable("command.ohmymeteors.custom.display.special")).append(Text.literal(": "))));
                        try (DirectoryStream<Path> stream_s = Files.newDirectoryStream(file)) {
                            for (Path file_s : stream_s) {
                                context.getSource().sendMessage(Text.literal("§7-§r "+ file_s.getFileName()));
                            }
                        }
                    }else{
                        context.getSource().sendMessage(Text.literal("§7-§r "+ file.getFileName()));
                    }

                }
            }
            return 1;
        }catch (Exception e){
            context.getSource().sendError(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("command.ohmymeteors.custom.display.failed")));
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
                                        CommandManager.literal("structureblock").then(
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
                                        CommandManager.literal("worldedit_schematic")
                                                .then(
                                                        CommandManager.argument("schemId", StringArgumentType.string())
                                                                .then(
                                                                        CommandManager.argument("sizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                                .then(CommandManager.argument("special", BoolArgumentType.bool())
                                                                                        .executes(this::addStructureWE)
                                                                        )
                                                        )
                                        )

                                )
                                .then(
                                        CommandManager.literal("litematica_schematic")
                                                .then(
                                                        CommandManager.argument("schemId", StringArgumentType.string())
                                                                .then(
                                                                        CommandManager.argument("sizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                                .then(CommandManager.argument("special", BoolArgumentType.bool())
                                                                                        .executes(this::addStructureLitematica)
                                                                                )
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
                                                .executes(this::ignoreDefaults)
                                )
                )
                .then(
                        CommandManager.literal("display_current")
                                .executes(this::displayCurrent)
                )
                .then(
                        CommandManager.literal("edit")
                                .then(
                                        CommandManager.argument("originalName", StringArgumentType.string())
                                                .then(
                                                        CommandManager.argument("ogSizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                .then(CommandManager.argument("ogSpecial", BoolArgumentType.bool())
                                                                        .then(CommandManager.argument("newName", StringArgumentType.string())
                                                                                .then(CommandManager.argument("newSizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                                        .then(CommandManager.argument("newSpecial", BoolArgumentType.bool())
                                                                                                .executes(this::edit)
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                )
                                )
                )
                .build();
    }
}
