package me.emafire003.dev.ohmymeteors.compat.perms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.fml.ModList;
import java.util.function.Predicate;

public class PermissionsChecker {

    public static final boolean permissions = ModList.get().isLoaded("luckperms");

    @SafeVarargs
    public static Predicate<CommandSourceStack> multiple(
            Predicate<CommandSourceStack>... args) {
        return source -> {
            for (Predicate<CommandSourceStack> predicate : args) {
                if (!predicate.test(source))
                    return false;
            }
            return true;
        };
    }

    public static Predicate<CommandSourceStack> hasPerms(String permission, int defaultValue) {
        return (source) -> {
            if(!permissions){
                return source.hasPermission(2);
            }else {
                //checks if they are an operator
                if(source.getEntity() != null && source.getEntity().hasPermissions(4)){
                    checkPermission(source.getEntity(), permission).asBoolean();
                    return true;
                }
                return checkPermission(source.getEntity(), permission).asBoolean();
            }
        };
    }
    public static LuckPerms luckPerms = null;

    public static Tristate checkPermission(Entity entity, String permission) {
        if (!(entity instanceof ServerPlayer)) {
            return Tristate.UNDEFINED;
        }

        var user = luckPerms.getPlayerAdapter(ServerPlayer.class).getUser((ServerPlayer) entity);
        var perms = user.getCachedData().getPermissionData();
        return perms.checkPermission(permission);
    }

    /*
    public static CompletableFuture<Tristate> checkPermission(UUID uuid, String permission) {
        var future = new CompletableFuture<Tristate>();
        if (luckPerms.getUserManager().isLoaded(uuid)) {
            var user = luckPerms.getUserManager().getUser(uuid);
            if (user == null) {
                future.complete(Tristate.UNDEFINED);
            } else {
                future.complete(user
                        .getCachedData()
                        .getPermissionData()
                        .checkPermission(permission));
            }
        } else {
            luckPerms.getUserManager().loadUser(uuid).thenAccept(user -> future
                    .complete(user
                            .getCachedData()
                            .getPermissionData()
                            .checkPermission(permission)));
        }
        return future;
    }

    public static boolean hasPerms(Entity entity, String permission, boolean defValue){
        //if(!permissions){
            return defValue;
        /*}else {
            return Permissions.check(entity, permission, defValue);
        }
    }*/
}
