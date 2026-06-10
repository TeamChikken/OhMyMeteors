package me.emafire003.dev.ohmymeteors.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import me.emafire003.dev.ohmymeteors.util.MeteorUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.CONFIG;

@Mixin(AtmosphericFogEnvironment.class)
public abstract class MeteorFogMixin {

    @ModifyReturnValue(method = "getBaseColor", at = @At("RETURN"))
    private static int modifyColor(int original, @Local(argsOnly = true, name = "camera") Camera camera, @Local(argsOnly = true, name = "level") ClientLevel clientLevel){
        if(areMeteorsNearby(camera, clientLevel)){
            //TODO maybe add a transition?
            //Default: Vec3(4,141,165) #048da5


            return toColorValue(CONFIG.visualsSection.meteor_skyglow_color.r(), CONFIG.visualsSection.meteor_skyglow_color.g(), CONFIG.visualsSection.meteor_skyglow_color.b());
        }
        return original;
    }

    @Unique
    private static int toColorValue(int r, int g, int b){
        return (r << 16) | (g << 8) | (b);
    }

    @Unique
    private static boolean areMeteorsNearby(Camera camera, ClientLevel clientLevel){
        if(!CONFIG.visualsSection.meteor_skyglow){
            return false;
        }
        //If the list isn't empty it measn at least a meteor has spawned and might be near the player
        if(!MeteorUtils.getAliveMeteors().isEmpty()){
            List<MeteorProjectileEntity> meteors_around = clientLevel.getEntitiesOfClass(MeteorProjectileEntity.class, new AABB(camera.blockPosition()).inflate(CONFIG.visualsSection.meteor_render_distance, CONFIG.visualsSection.meteor_render_distance, CONFIG.visualsSection.meteor_render_distance), (e -> true));
            return !meteors_around.isEmpty();
        }
        return false;
    }
}