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
import me.emafire003.dev.ohmymeteors.mixin.MinecraftServerSessionAccessor;
import me.emafire003.dev.ohmymeteors.util.MeteorSizeClass;
import me.emafire003.dev.ohmymeteors.util.packutils.PackMeta;
import me.emafire003.dev.ohmymeteors.util.packutils.PackUtilThing;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.loading.FMLLoader;

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

    private int addStructure(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            ResourceLocation structureId = ResourceLocationArgument.getId(context, "structureId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            //Generates the datapack folders
            generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getStorageSource().getLevelPath(LevelResource.DATAPACK_DIR));

            String struct_id = "/"+size.getSerializedName()+"/"+ structureId.getPath()+".nbt";
            if(special){
                struct_id = "/"+size.getSerializedName()+"/special/"+ structureId.getPath()+".nbt";
            }

            //The path of the "generated" folder where structures are saved
            String generated_path = ((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getStorageSource().getLevelPath(LevelResource.GENERATED_DIR).toString();

            //copies the structure file from the generated directory into the datapack folder. sends error if the file already exists
            try{
                if(!Files.exists(Path.of(generated_path+"/minecraft/structure/" + structureId.getPath()+".nbt"))){
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed.origin_not_found", structureId.getPath()+".nbt")));
                    OhMyMeteors.LOGGER.warn("The path in which the file was searched: " + Path.of(generated_path+"/minecraft/structure/" + structureId.getPath()+".nbt"));
                    return 0;
                }
                Files.copy(Path.of(generated_path+"/minecraft/structure/" + structureId.getPath()+".nbt"),
                        Path.of(PACK_DIR_STRUCTURE + struct_id));
            }catch (FileAlreadyExistsException e){
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", structureId.getPath())));
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                return 0;
            }

            if(special){
                context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.success", structureId.getPath(), size.getSerializedName()).append(Component.translatable("command.ohmymeteors.custom.special"))));
            }else{
                context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.success", structureId.getPath(), size.getSerializedName())));
            }
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.reload")));

            return 1;
        }catch (Exception e){
            ResourceLocation structureId = ResourceLocationArgument.getId(context, "structureId");
            context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", structureId.getPath())));
            e.printStackTrace();
            return 0;
        }
    }

    private int addStructureWE(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            String schemId = StringArgumentType.getString(context, "schemId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            //the path where worldedit schematics are stored
            Path we_schempath = Path.of(FMLLoader.getGamePath()+"/config/worldedit/schematics/");

            schemId = schemId.replaceAll("\\.schem", "");

            String struct_id = "/"+size.getSerializedName()+"/"+ schemId.toLowerCase()+".nbt";
            if(special){
                struct_id = "/"+size.getSerializedName()+"/special/"+ schemId.toLowerCase()+".nbt";
            }

            //Generates the datapack folders
            try {
                generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getStorageSource().getLevelPath(LevelResource.DATAPACK_DIR));
            } catch (IOException e) {
                e.printStackTrace();
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                return 0;
            }

            //copies the structure file from the generated directory into the datapack folder. sends error if the file already exists
            try{
                if(Files.exists(Path.of(PACK_DIR_STRUCTURE + struct_id))){
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                    return 0;
                }
                if(!Files.exists(Path.of(we_schempath+ "/" +schemId+".schem"))){
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed.origin_not_found", schemId+".schem")));
                    OhMyMeteors.LOGGER.warn("The path in which the file was searched: " + Path.of(we_schempath+ "/" +schemId+".schem"));
                    return 0;
                }
                SchemConvertCompat.convertToNbt(new File(we_schempath+ "/" +schemId+".schem"), Path.of(PACK_DIR_STRUCTURE + struct_id).toFile());
            }catch (FileAlreadyExistsException e){
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
            }

            if(special){
                context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.success", schemId, size.getSerializedName()).append(Component.translatable("command.ohmymeteors.custom.special"))));
            }else{
                context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.success", schemId, size.getSerializedName())));
            }
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.reload")));

            return 1;
        }catch (Exception e){
            String schemId = StringArgumentType.getString(context, "schemId");
            context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
            e.printStackTrace();
            return 0;
        }
    }

    private int addStructureLitematica(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            String schemId = StringArgumentType.getString(context, "schemId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            //the path where litematica schematics are stored
            Path lm_schempath = Path.of(FMLLoader.getGamePath()+"/schematics/");

            schemId = schemId.replaceAll("\\.litematic", "");

            String struct_id = "/"+size.getSerializedName()+"/"+ schemId.toLowerCase()+".nbt";
            if(special){
                struct_id = "/"+size.getSerializedName()+"/special/"+ schemId.toLowerCase()+".nbt";
            }

            //Generates the datapack folders
            try {
                generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getStorageSource().getLevelPath(LevelResource.DATAPACK_DIR));
            } catch (IOException e) {
                e.printStackTrace();
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                return 0;
            }

            //copies the structure file from the generated directory into the datapack folder. sends error if the file already exists
            try{
                if(Files.exists(Path.of(PACK_DIR_STRUCTURE + struct_id))){
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                    return 0;
                }
                if(!Files.exists(Path.of(lm_schempath+ "/" +schemId+".litematic"))){
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed.origin_not_found", schemId+".litematic")));
                    OhMyMeteors.LOGGER.warn("The path in which the file was searched: " + Path.of(lm_schempath+ "/" +schemId+".litematic"));
                    return 0;
                }
                SchemConvertCompat.convertToNbt(new File(lm_schempath+ "/" +schemId+".litematic"), Path.of(PACK_DIR_STRUCTURE + struct_id).toFile());
            }catch (FileAlreadyExistsException e){
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed.already_present")));
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
            }

            if(special){
                context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.success", schemId, size.getSerializedName()).append(Component.translatable("command.ohmymeteors.custom.special"))));
            }else{
                context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.success", schemId, size.getSerializedName())));
            }
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.reload")));

            return 1;
        }catch (Exception e){
            String schemId = StringArgumentType.getString(context, "schemId");
            context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.add.failed", schemId)));
            e.printStackTrace();
            return 0;
        }
    }


    private int removeStructure(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            ResourceLocation structureId = ResourceLocationArgument.getId(context, "structureId");
            MeteorSizeClass size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "sizeClass");
            boolean special = BoolArgumentType.getBool(context, "special");

            String struct_id = "/"+size.getSerializedName()+"/"+ structureId.getPath()+".nbt";
            if(special){
                struct_id = "/"+size.getSerializedName()+"/special/"+ structureId.getPath()+".nbt";
            }

            //deletes the file (if it exists)
            Files.deleteIfExists(Path.of(PACK_DIR_STRUCTURE + struct_id));

            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.remove.success", structureId.getPath())));
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.reload")));

            return 1;
        }catch (Exception e){
            ResourceLocation structureId = ResourceLocationArgument.getId(context, "structureId");
            context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.remove.failed", structureId.getPath())));
            e.printStackTrace();
            return 0;
        }
    }

    private int edit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            String og_structureId = StringArgumentType.getString(context, "originalName");
            MeteorSizeClass og_size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "ogSizeClass");
            boolean og_special = BoolArgumentType.getBool(context, "ogSpecial");
            String new_structureId = StringArgumentType.getString(context, "newName");
            MeteorSizeClass new_size = MeteorSizeClassArgumentType.getMeteorSizeClass(context, "newSizeClass");
            boolean new_special = BoolArgumentType.getBool(context, "newSpecial");

            //Generates the datapack folders
            generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getStorageSource().getLevelPath(LevelResource.DATAPACK_DIR));

            String og_struct_id = "/"+og_size.getSerializedName()+"/"+ og_structureId+".nbt";
            if(og_special){
                og_struct_id = "/"+og_size.getSerializedName()+"/special/"+ og_structureId+".nbt";
            }

            String new_struct_id = "/"+new_size.getSerializedName()+"/"+ new_structureId+".nbt";
            if(new_special){
                new_struct_id = "/"+new_size.getSerializedName()+"/special/"+ new_structureId+".nbt";
            }

            if(!Files.exists(Path.of(PACK_DIR_STRUCTURE + og_struct_id))){
                if(og_special){
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.edit.file_not_found", og_structureId, og_size.toString(), "✔")));
                }else{
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.edit.file_not_found", og_structureId, og_size.toString(), "x")));
                }
                return 0;
            }

            //copies the structure file from the generated directory into the datapack folder. sends error if the file already exists
            try{
                Files.move(Path.of(PACK_DIR_STRUCTURE + og_struct_id),
                        Path.of(PACK_DIR_STRUCTURE + new_struct_id));
                if(new_special){
                    context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.edit.success", og_structureId, new_structureId, new_size.toString())
                            .append(Component.translatable("command.ohmymeteors.custom.special"))));
                }else{
                    context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.edit.success", og_structureId, new_structureId, new_size.toString())
                            .append(Component.translatable("command.ohmymeteors.custom.not_special"))));
                }

            }catch (FileAlreadyExistsException e){
                context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.edit.dest_file_already_exists", new_structureId)));
                return 0;
            }

            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.reload")));

            return 1;
        }catch (Exception e){
            String name = StringArgumentType.getString(context, "originalName");
            context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.edit.failed", name)));
            e.printStackTrace();
            return 0;
        }
    }

    private int ignoreDefaults(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            boolean activate = BoolArgumentType.getBool(context, "ignore");

            //Generates the datapack folders
            generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getStorageSource().getLevelPath(LevelResource.DATAPACK_DIR));

            if(activate){
                //TODO note (so not actually a todo): this could also be used to get the path of a structure file.
                //Path path = context.getSource().getWorld().getStructureTemplateManager().getTemplatePath(OhMyMeteors.getIdentifier("error"), ".nbt");

                //TODO write in the changelog/wiki that the ignore_thing can just be any file. aslo maybe update the default datapack thingy
                try{
                    Files.createFile(Path.of(PACK_DIR_STRUCTURE + "/ignoredefault.nbt"));
                    context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.ignoredefaults.on")));
                }catch (FileAlreadyExistsException e){
                    context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.ignoredefaults.already_on")));
                    return 0;
                }
            }else{
                Files.deleteIfExists(Path.of(PACK_DIR_STRUCTURE + "/ignoredefault.nbt"));
                context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.ignoredefaults.off")));
            }
            return 1;
        }catch (Exception e){
            context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.ignoredefaults.failed")));
            e.printStackTrace();
            return 0;
        }
    }

    private int displayCurrent(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            generateDatapack(((MinecraftServerSessionAccessor) context.getSource().getServer()).ohmymeteors$getStorageSource().getLevelPath(LevelResource.DATAPACK_DIR));

            //Cheks and lists small meteors
            Path smalls = Path.of(PACK_DIR_STRUCTURE+"/small/");
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.small").append(Component.literal(": "))));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(smalls)) {
                for (Path file : stream) {
                    if(file.getFileName().toString().equals("special")){
                        context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.small").append(Component.literal(" ")).append(Component.translatable("command.ohmymeteors.custom.display.special")).append(Component.literal(": "))));
                        try (DirectoryStream<Path> stream_s = Files.newDirectoryStream(file)) {
                            for (Path file_s : stream_s) {
                                context.getSource().sendSystemMessage(Component.literal("§7-§r "+ file_s.getFileName()));
                            }
                        }
                    }else{
                        context.getSource().sendSystemMessage(Component.literal("§7-§r "+ file.getFileName()));
                    }

                }
            }


            //Medium ones
            Path mediums = Path.of(PACK_DIR_STRUCTURE+"/medium/");
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.medium").append(Component.literal(": "))));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(mediums)) {
                for (Path file : stream) {
                    if(file.getFileName().toString().equals("special")){
                        context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.medium").append(Component.literal(" ")).append(Component.translatable("command.ohmymeteors.custom.display.special")).append(Component.literal(": "))));
                        try (DirectoryStream<Path> stream_s = Files.newDirectoryStream(file)) {
                            for (Path file_s : stream_s) {
                                context.getSource().sendSystemMessage(Component.literal("§7-§r "+ file_s.getFileName()));
                            }
                        }
                    }else{
                        context.getSource().sendSystemMessage(Component.literal("§7-§r "+ file.getFileName()));
                    }
                }
            }

            Path bigs = Path.of(PACK_DIR_STRUCTURE+"/big/");
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.big").append(Component.literal(": "))));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(bigs)) {
                for (Path file : stream) {
                    if(file.getFileName().toString().equals("special")){
                        context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.big").append(Component.literal(" ")).append(Component.translatable("command.ohmymeteors.custom.display.special")).append(Component.literal(": "))));
                        try (DirectoryStream<Path> stream_s = Files.newDirectoryStream(file)) {
                            for (Path file_s : stream_s) {
                                context.getSource().sendSystemMessage(Component.literal("§7-§r "+ file_s.getFileName()));
                            }
                        }
                    }else{
                        context.getSource().sendSystemMessage(Component.literal("§7-§r "+ file.getFileName()));
                    }

                }
            }

            Path huges = Path.of(PACK_DIR_STRUCTURE+"/huge/");
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.huge").append(Component.literal(": "))));
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(huges)) {
                for (Path file : stream) {
                    if(file.getFileName().toString().equals("special")){
                        context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.huge").append(Component.literal(" ")).append(Component.translatable("command.ohmymeteors.custom.display.special")).append(Component.literal(": "))));
                        try (DirectoryStream<Path> stream_s = Files.newDirectoryStream(file)) {
                            for (Path file_s : stream_s) {
                                context.getSource().sendSystemMessage(Component.literal("§7-§r "+ file_s.getFileName()));
                            }
                        }
                    }else{
                        context.getSource().sendSystemMessage(Component.literal("§7-§r "+ file.getFileName()));
                    }

                }
            }

            //Ignoredefaults
            if(Files.exists(Path.of(PACK_DIR_STRUCTURE+"/ignoredefault.nbt"))){
                context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.ignoredefaults.on")));
            }else{
                context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.ignoredefaults.off")));
            }
            return 1;
        }catch (Exception e){
            context.getSource().sendFailure(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("command.ohmymeteors.custom.display.failed")));
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
    public LiteralCommandNode<CommandSourceStack> getNode(CommandBuildContext registryAccess) {
        return Commands
                .literal("custom")
                .requires(PermissionsChecker.hasPerms(OhMyMeteors.MOD_ID+".commands.custom", 2))
                .then(
                        Commands.literal("add")
                                .then(
                                        Commands.literal("structureblock").then(
                                                Commands.argument("structureId", ResourceLocationArgument.id()
                                                        )
                                                        .then(
                                                                Commands.argument("sizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                        .then(Commands.argument("special", BoolArgumentType.bool())
                                                                                .executes(this::addStructure)
                                                                        )
                                                        )
                                        )
                                )
                                .then(
                                        Commands.literal("worldedit_schematic")
                                                .then(
                                                        Commands.argument("schemId", StringArgumentType.string())
                                                                .then(
                                                                        Commands.argument("sizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                                .then(Commands.argument("special", BoolArgumentType.bool())
                                                                                        .executes(this::addStructureWE)
                                                                        )
                                                        )
                                        )

                                )
                                .then(
                                        Commands.literal("litematica_schematic")
                                                .then(
                                                        Commands.argument("schemId", StringArgumentType.string())
                                                                .then(
                                                                        Commands.argument("sizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                                .then(Commands.argument("special", BoolArgumentType.bool())
                                                                                        .executes(this::addStructureLitematica)
                                                                                )
                                                                )
                                                )

                                )
                )
                .then(
                        Commands.literal("remove")
                                .then(
                                        Commands.argument("structureId", ResourceLocationArgument.id()
                                                )
                                                .then(
                                                        Commands.argument("sizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                .then(Commands.argument("special", BoolArgumentType.bool())
                                                                        .executes(this::removeStructure)
                                                                )
                                                )
                                )
                )
                .then(
                        Commands.literal("ignoredefaults")
                                .then(
                                        Commands.argument("ignore", BoolArgumentType.bool())
                                                .executes(this::ignoreDefaults)
                                )
                )
                .then(
                        Commands.literal("display_current")
                                .executes(this::displayCurrent)
                )
                .then(
                        Commands.literal("edit")
                                .then(
                                        Commands.argument("originalName", StringArgumentType.string())
                                                .then(
                                                        Commands.argument("ogSizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                .then(Commands.argument("ogSpecial", BoolArgumentType.bool())
                                                                        .then(Commands.argument("newName", StringArgumentType.string())
                                                                                .then(Commands.argument("newSizeClass", MeteorSizeClassArgumentType.meteorSizeClass())
                                                                                        .then(Commands.argument("newSpecial", BoolArgumentType.bool())
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
