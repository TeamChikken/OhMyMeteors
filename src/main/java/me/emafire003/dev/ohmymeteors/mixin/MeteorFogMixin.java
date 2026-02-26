package me.emafire003.dev.ohmymeteors.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import me.emafire003.dev.ohmymeteors.util.MeteorUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.*;
import java.util.List;

@Debug(export = true)
@Mixin(FogRenderer.class)
public abstract class MeteorFogMixin {


    @ModifyExpressionValue(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 modifySkyColor(Vec3 original, @Local(argsOnly = true) Camera camera, @Local(argsOnly = true) ClientLevel clientLevel){
        if(areMeteorsNearby(camera, clientLevel)){
            //TODO maybe add a transition?
            //Default: Vec3(4,141,165) #048da5
            Color color = Color.decode("#" + Config.METEOR_SKYGLOW_COLOR.replaceAll("#", ""));
            camera.getEntity().sendSystemMessage(Component.literal("The color: " + new Vec3(color.getRed(), color.getBlue(), color.getGreen())));
            return new Vec3(color.getRed(), color.getBlue(), color.getGreen());
        }
        return original;
    }

    @Unique
    private static boolean areMeteorsNearby(Camera camera, ClientLevel clientLevel){
        if(!Config.METEOR_SKYGLOW){
            return false;
        }
        //If the list isn't empty it measn at least a meteor has spawned and might be near the player
        if(!MeteorUtils.getAliveMeteors().isEmpty()){
            List<MeteorProjectileEntity> meteors_around = clientLevel.getEntitiesOfClass(MeteorProjectileEntity.class, new AABB(camera.getBlockPosition()).inflate(Config.METEOR_RENDER_DISTANCE, Config.METEOR_RENDER_DISTANCE, Config.METEOR_RENDER_DISTANCE), (e -> true));
            return !meteors_around.isEmpty();
        }
        return false;
    }
}