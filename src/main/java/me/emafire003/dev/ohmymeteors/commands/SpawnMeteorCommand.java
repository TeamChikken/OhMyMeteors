package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SpawnMeteorCommand implements OMMCommand {

    private int spawnSize(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        try{

            if(!source.isExecutedByPlayer()){
                source.sendMessage(Text.literal("Must be executed by player"));
                return 0;
            }

            MeteorProjectileEntity meteorProjectile = new MeteorProjectileEntity(source.getWorld());
            meteorProjectile.setPos(source.getPlayer().getX(), source.getPlayer().getEyeY(), source.getPlayer().getZ());

            meteorProjectile.setVelocity(source.getPlayer(), source.getPlayer().getPitch(), source.getPlayer().getYaw(), 0f, 0.2f, 0f);
            meteorProjectile.setSize(IntegerArgumentType.getInteger(context, "size"));
            source.getWorld().spawnEntity(meteorProjectile);

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(Text.literal("Error: " + e),false);
            return 0;
        }
    }

    private int spawnSpeed(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        try{

            if(!source.isExecutedByPlayer()){
                source.sendMessage(Text.literal("Must be executed by player"));
                return 0;
            }

            MeteorProjectileEntity meteorProjectile = new MeteorProjectileEntity(source.getWorld());
            meteorProjectile.setPos(source.getPlayer().getX(), source.getPlayer().getEyeY(), source.getPlayer().getZ());

            meteorProjectile.setVelocity(source.getPlayer(), source.getPlayer().getPitch(), source.getPlayer().getYaw(), 0f, FloatArgumentType.getFloat(context, "speed"), 0f);
            meteorProjectile.setSize(IntegerArgumentType.getInteger(context, "size"));
            source.getWorld().spawnEntity(meteorProjectile);

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback(Text.literal("Error: " + e),false);
            return 0;
        }
    }


    public LiteralCommandNode<ServerCommandSource> getNode(CommandRegistryAccess registryAccess) {
        return CommandManager
                .literal("spawn")
                .then(
                        CommandManager.argument("size", IntegerArgumentType.integer(0, 50))
                                .then(
                                        CommandManager.argument("speed", FloatArgumentType.floatArg(0, 10))
                                                .executes(this::spawnSpeed)
                                ).executes(this::spawnSize)
                )
                .build();
    }
}
