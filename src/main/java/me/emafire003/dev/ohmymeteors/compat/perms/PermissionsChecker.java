package me.emafire003.dev.ohmymeteors.compat.perms;

import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

//Based on Factions' code https://github.com/ickerio/factions (MIT license)
public class PermissionsChecker {

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
            //if(!permissions){
                return source.hasPermission(2);
            /*}else {
                return Permissions.check(source, permission, defaultValue);
            }*/
            //TODO figure out what to do with forge's permissions
        };
    }

    public static boolean hasPerms(Entity entity, String permission, boolean defValue){
        //if(!permissions){
            return defValue;
        /*}else {
            return Permissions.check(entity, permission, defValue);
        }*/
    }
}
