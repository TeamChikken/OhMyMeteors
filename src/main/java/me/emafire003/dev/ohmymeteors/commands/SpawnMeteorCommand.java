package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.commands.argument.MeteorShowerTypeArgumentType;
import me.emafire003.dev.ohmymeteors.compat.perms.PermissionsChecker;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import me.emafire003.dev.ohmymeteors.entities.OMMEntities;
import me.emafire003.dev.ohmymeteors.util.MeteorShowerType;
import me.emafire003.dev.ohmymeteors.util.MeteorUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class SpawnMeteorCommand implements OMMCommand {

    @Deprecated
    private int spawnRandom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        try{

            if(!source.isPlayer()){
                source.sendSystemMessage(Component.literal("Must be executed by player"));
                return 0;
            }

            MeteorProjectileEntity meteorProjectile = new MeteorProjectileEntity(OMMEntities.METEOR_PROJECTILE_ENTITY, source.getLevel());
            meteorProjectile.setPosRaw(
                    source.getPlayer().getX()+source.getPlayer().getRandom().nextIntBetweenInclusive(0, 50)*source.getPlayer().getRandom().nextIntBetweenInclusive(-1, 1),
                    source.getPlayer().getEyeY()+source.getPlayer().getRandom().nextIntBetweenInclusive(0, 50)*source.getPlayer().getRandom().nextIntBetweenInclusive(-1, 1),
                    source.getPlayer().getZ()+source.getPlayer().getRandom().nextIntBetweenInclusive(0, 50)*source.getPlayer().getRandom().nextIntBetweenInclusive(-1, 1)
            );

            source.sendSystemMessage(Component.literal("Spawning meteor at " + meteorProjectile.position()));
            meteorProjectile.setSize(source.getPlayer().getRandom().nextIntBetweenInclusive(0, 20));
            source.getLevel().addFreshEntity(meteorProjectile);

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendSuccess( () -> Component.literal("Error: " + e),false);
            return 0;
        }
    }

    private int spawnSize(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        try{

            if(!source.isPlayer()){
                source.sendSystemMessage(Component.literal("Must be executed by player"));
                return 0;
            }

            MeteorProjectileEntity meteorProjectile = new MeteorProjectileEntity(OMMEntities.METEOR_PROJECTILE_ENTITY, source.getLevel());
            meteorProjectile.setPosRaw(source.getPlayer().getX(), source.getPlayer().getEyeY(), source.getPlayer().getZ());

            meteorProjectile.shootFromRotation(source.getPlayer(), source.getPlayer().getXRot(), source.getPlayer().getYRot(), 0f, 0.2f, 0f);
            meteorProjectile.setSize(IntegerArgumentType.getInteger(context, "size"));
            source.getLevel().addFreshEntity(meteorProjectile);

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendSuccess( () -> Component.literal("Error: " + e),false);
            return 0;
        }
    }

    private int spawnSpeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        try{

            if(!source.isPlayer()){
                source.sendSystemMessage(Component.literal("Must be executed by player"));
                return 0;
            }

            MeteorProjectileEntity meteorProjectile = new MeteorProjectileEntity(OMMEntities.METEOR_PROJECTILE_ENTITY, source.getLevel());
            meteorProjectile.setPosRaw(source.getPlayer().getX(), source.getPlayer().getEyeY(), source.getPlayer().getZ());

            meteorProjectile.shootFromRotation(source.getPlayer(), source.getPlayer().getXRot(), source.getPlayer().getYRot(), 0f, FloatArgumentType.getFloat(context, "speed"), 0f);
            meteorProjectile.setSize(IntegerArgumentType.getInteger(context, "size"));
            source.getLevel().addFreshEntity(meteorProjectile);

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendSuccess( () -> Component.literal("Error: " + e),false);
            return 0;
        }
    }


    /**Spawns a meteor exactly like the natural spawns. Gives an error if there are no players online*/
    private int spawnNatural(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        try{

           if(source.getLevel().players().isEmpty()){
               source.sendSuccess( () -> Component.literal("Could not spawn a natural meteor since there are no players online!"),true);
               return -1;
           }


           ServerPlayer p = source.getLevel().getRandomPlayer();
           if(MeteorUtils.canMeteorSpawnVerbose(p, source)){
               MeteorUtils.spawnMeteor(source.getLevel(), p, false);
           }else{
               return 0;
           }

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendSuccess( () -> Component.literal("Error: " + e),false);
            return 0;
        }
    }

    /**Spawns a meteor shower exactly like the natural spawns. Gives an error if there are no players online*/
    private int spawnShower(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        MeteorShowerType type = MeteorShowerTypeArgumentType.getMeteorShowerType(context, "type");
        try{
            if(source.getLevel().players().isEmpty()){
                source.sendSuccess( () -> Component.literal("Could not spawn a natural meteor since there are no players online!"),true);
                return -1;
            }
            ServerPlayer p = source.getLevel().getRandomPlayer();
            if(MeteorUtils.canMeteorSpawnVerbose(p, source)){
                if(type.equals(MeteorShowerType.DELAYED)){
                    MeteorUtils.spawnMeteorShowerDelayed(source.getLevel(), p);
                }else if(type.equals(MeteorShowerType.DELAYED_DIRECTION)){
                    MeteorUtils.spawnMeteorShowerDelayedDirection(source.getLevel(), Objects.requireNonNull(p));
                }else{
                    MeteorUtils.spawnMeteorShowerInstant(source.getLevel(), p);
                }
            }else{
                return 0;
            }

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendSuccess( () -> Component.literal("Error: " + e),false);
            return 0;
        }
    }


    public LiteralCommandNode<CommandSourceStack> getNode(CommandBuildContext registryAccess) {
        return Commands
                .literal("spawn")
                .requires(PermissionsChecker.hasPerms(OhMyMeteors.MOD_ID+".commands.spawn", 2))
                .then(
                        Commands.literal("natural")
                                .executes(this::spawnNatural)
                )
                .then(
                        Commands.argument("size", IntegerArgumentType.integer(0, 50))
                                .then(
                                        Commands.argument("speed", FloatArgumentType.floatArg(0, 10))
                                                .executes(this::spawnSpeed)
                                )
                                .executes(this::spawnSize)
                ).then(
                        Commands.literal("shower")
                                .then(Commands.argument("type", MeteorShowerTypeArgumentType.meteorShowerType())
                                        .executes(this::spawnShower)
                                )

                )
                .build();
    }
}
