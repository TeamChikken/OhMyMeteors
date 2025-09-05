package me.emafire003.dev.ohmymeteors.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.compat.flan.FlanCompat;
import me.emafire003.dev.ohmymeteors.compat.perms.PermissionsChecker;
import me.emafire003.dev.ohmymeteors.compat.yawp.YawpCompat;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class SpawnMeteorCommand implements OMMCommand {

    private int spawnRandom(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        try{

            if(!source.isExecutedByPlayer()){
                source.sendMessage(Text.literal("Must be executed by player"));
                return 0;
            }

            MeteorProjectileEntity meteorProjectile = new MeteorProjectileEntity(source.getWorld());
            meteorProjectile.setPos(
                    source.getPlayer().getX()+source.getPlayer().getRandom().nextBetween(0, 50)*source.getPlayer().getRandom().nextBetween(-1, 1), 
                    source.getPlayer().getEyeY()+source.getPlayer().getRandom().nextBetween(0, 50)*source.getPlayer().getRandom().nextBetween(-1, 1), 
                    source.getPlayer().getZ()+source.getPlayer().getRandom().nextBetween(0, 50)*source.getPlayer().getRandom().nextBetween(-1, 1)
            );

            source.sendMessage(Text.literal("Spawning meteor at " + meteorProjectile.getPos()));

            meteorProjectile.setSize(source.getPlayer().getRandom().nextBetween(0, 20));
            source.getWorld().spawnEntity(meteorProjectile);

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback( Text.literal("Error: " + e),false);
            return 0;
        }
    }

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
            source.sendFeedback( Text.literal("Error: " + e),false);
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
            //necessary in 1.19.2 apparently
            meteorProjectile.setVelocity(meteorProjectile.getVelocity().multiply(2));
            meteorProjectile.setSize(IntegerArgumentType.getInteger(context, "size"));
            source.getWorld().spawnEntity(meteorProjectile);

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback( Text.literal("Error: " + e),false);
            return 0;
        }
    }

    private boolean spawnChecks(ServerPlayerEntity p){
        if(FabricLoader.getInstance().isModLoaded("flan")){
            if(!FlanCompat.canSpawnHere(p, p.getBlockPos())){
                return false;
            }
        }

        if(FabricLoader.getInstance().isModLoaded("yawp")){
            //Checks the player pos and the place where the meteor would spawn
            if(!(YawpCompat.canSpawnHere(p.getServerWorld(), p.getBlockPos()) || YawpCompat.canSpawnHere(p.getServerWorld(), new BlockPos(p.getBlockPos().getX(), Config.METEOR_SPAWN_HEIGHT, p.getBlockPos().getZ())))){
                return false;
            }
        }

        RegistryEntry<DimensionType> current_dim = p.getWorld().getDimensionEntry();

        if(!MeteorProjectileEntity.canSpawnInDimension(current_dim)){
            return false;
        }

        RegistryEntry<Biome> current_biome = p.getWorld().getBiome(p.getBlockPos());

        if(!MeteorProjectileEntity.canSpawnInBiome(current_biome)){
            return false;
        }
        return true;
    }

    /**Spawns a meteor exactly like the natural spawns. Gives an error if there are no players online*/
    private int spawnNatural(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        try{

           if(source.getWorld().getPlayers().isEmpty()){
               source.sendFeedback( Text.literal("Could not spawn a natural meteor since there are no players online!"),true);
               return -1;
           }


           ServerPlayerEntity p = source.getWorld().getRandomAlivePlayer();
           if(spawnChecks(p)){
               MeteorProjectileEntity.spawnMeteor(source.getWorld(), p);
           }else{
               source.sendError(Text.literal(OhMyMeteors.PREFIX + "Could not spawn a meteor in the area around player: ").append(p.getName()));
               return 0;
           }

            return 1;
        }catch(Exception e){
            e.printStackTrace();
            source.sendFeedback( Text.literal("Error: " + e),false);
            return 0;
        }
    }


    public LiteralCommandNode<ServerCommandSource> getNode(CommandRegistryAccess registryAccess) {
        return CommandManager
                .literal("spawn")
                .requires(PermissionsChecker.hasPerms(OhMyMeteors.MOD_ID+".commands.spawn", 2))
                .then(
                        CommandManager.literal("random")
                                .executes(this::spawnRandom)
                )
                .then(
                        CommandManager.literal("natural")
                                .executes(this::spawnNatural)
                )
                .then(
                        CommandManager.argument("size", IntegerArgumentType.integer(0, 50))
                                .then(
                                        CommandManager.argument("speed", FloatArgumentType.floatArg(0, 10))
                                                .executes(this::spawnSpeed)
                                )
                                .executes(this::spawnSize)
                )
                .build();
    }
}
