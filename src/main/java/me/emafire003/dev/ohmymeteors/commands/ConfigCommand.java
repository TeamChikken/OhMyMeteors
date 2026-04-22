package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.compat.perms.PermissionsChecker;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.util.ParticleMode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.neoforged.fml.loading.FMLEnvironment;

public class ConfigCommand implements OMMCommand {

    private int openConfig(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            CommandSourceStack source = context.getSource();
            source.sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.literal("Please open the config file from Modmenu!").withStyle(ChatFormatting.GOLD).append(Component.literal(Config.FILEPATH.toFile().toString()).withStyle(ChatFormatting.LIGHT_PURPLE).append(Component.literal("'").withStyle(ChatFormatting.GOLD)))));
            /*if(source.getEntity().level().isClientSide()){
                ConfigApi.INSTANCE.openScreen(OhMyMeteors.MOD_ID);
                return 1;
            }*/ //TODO if i need to implement networking for something, also add this
            /*if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)){
                source.sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.literal("Warning! The config file is located on the server, go to your server's config folder and edit it or use the Modmenu buttons!'").withStyle(ChatFormatting.GOLD).append(Component.literal(Config.FILEPATH.toFile().toString()).withStyle(ChatFormatting.LIGHT_PURPLE).append(Component.literal("'").withStyle(ChatFormatting.GOLD)))));
                return 2;
            }

            Util.getPlatform().openFile(Config.FILEPATH.toFile());

            source.sendSystemMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.literal("Make sure to use ").append(Component.literal("/omm config reload").withStyle(ChatFormatting.BLUE).append(Component.literal(" when you have finished editing the config file!").withStyle(ChatFormatting.RESET)))));
            */return 1;
        }catch (Exception e){
            context.getSource().sendFailure(Component.literal("[Oh My, Meteors!] ").append("§cThere has been an error while reloading the config, check the logs"));
            e.printStackTrace();
            return 0;
        }
    }

    private int reloadConfig(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            OhMyMeteors.CONFIG.save();
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX+"§rConfig reloaded!"));
            return 1;
        }catch (Exception e){
            context.getSource().sendFailure(Component.literal("[Oh My, Meteors!] ").append("§cThere has been an error while reloading the config, check the logs"));
            e.printStackTrace();
            return 0;
        }
    }

    private int presetPerformance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            OhMyMeteors.CONFIG.visualsSection.use_forced_particles = false;
            OhMyMeteors.CONFIG.visualsSection.particles_mode = ParticleMode.MINIMAL;
            OhMyMeteors.CONFIG.meteorShowerSection.meteor_showers_enabled = false;
            OhMyMeteors.CONFIG.meteorBehaviourSection.meteors_load_chunks = false;
            OhMyMeteors.CONFIG.meteorBehaviourSection.use_better_explosions = false;
            OhMyMeteors.CONFIG.meteorBehaviourSection.spawn_scatter_meteors = false;
            OhMyMeteors.CONFIG.save();
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX+"§rConfig settings adjusted to have more performance!"));
            return 1;
        }catch (Exception e){
            context.getSource().sendFailure(Component.literal("[Oh My, Meteors!] ").append("§cThere has been an error while reloading the config, check the logs"));
            e.printStackTrace();
            return 0;
        }
    }

    private int presetNoGriefing(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try{
            OhMyMeteors.CONFIG.meteorBehaviourSection.meteor_structure = false;
            OhMyMeteors.CONFIG.meteorBehaviourSection.meteor_griefing = false;
            OhMyMeteors.CONFIG.meteorBehaviourSection.scatter_meteor_structure = false;
            OhMyMeteors.CONFIG.meteorBehaviourSection.scatter_meteor_griefing = false;
            OhMyMeteors.CONFIG.meteorBehaviourSection.spawn_fire_with_meteor = false;
            OhMyMeteors.CONFIG.save();
            context.getSource().sendSystemMessage(Component.literal(OhMyMeteors.PREFIX+"§rConfig settings adjusted to have no griefing!"));
            return 1;
        }catch (Exception e){
            context.getSource().sendFailure(Component.literal("[Oh My, Meteors!] ").append("§cThere has been an error while reloading the config, check the logs"));
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getNode(CommandBuildContext registryAccess) {
        return Commands
                .literal("config")
                .requires(PermissionsChecker.hasPerms(OhMyMeteors.MOD_ID+".commands.config", 2))
                .then(
                        Commands.literal("reload").executes(this::reloadConfig)
                ).then(
                        Commands.literal("open").executes(this::openConfig)
                )
                .then(
                        Commands.literal("preset")
                                .then(
                                        Commands.literal("performance").executes(this::presetPerformance)
                                ).then(
                                        Commands.literal("no_griefing").executes(this::presetNoGriefing)
                                )
                )

                .build();
    }
}
