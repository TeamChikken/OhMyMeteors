package me.emafire003.dev.ohmymeteors.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import me.emafire003.dev.ohmymeteors.util.MeteorUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.CONFIG;

@Debug(export = true)
@Mixin(AtmosphericFogEnvironment.class)
public abstract class MeteorFogMixin {

    @ModifyReturnValue(method = "getBaseColor", at = @At("RETURN"))
    private static Vector3fc modifyColor(Vector3fc original, @Local(argsOnly = true, name = "camera") Camera camera, @Local(argsOnly = true, name = "level") ClientLevel clientLevel){
        //TODO fix at some point
        /*if(areMeteorsNearby(camera, clientLevel)){
            //TODO maybe add a transition?
            //Default: Vec3(4,141,165) #048da5


            //TODO this makes EVERYTHING cyan. Also i need to update StructurePlacerAPI
            return new Vector3f(OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.r()*original.x(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.g()*original.y(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.b()*original.z());
        }*/
        return original;
    }

    /*@Unique
    private static int toColorValue(int r, int g, int b){
        return (r << 16) | (g << 8) | (b);
    }*/

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