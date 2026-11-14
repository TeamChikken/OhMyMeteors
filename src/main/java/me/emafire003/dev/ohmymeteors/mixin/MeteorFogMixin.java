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
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.CONFIG;

@Debug(export = true)
@Mixin(AtmosphericFogEnvironment.class)
public abstract class MeteorFogMixin {

    @ModifyExpressionValue(method = "getBaseColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/environment/AtmosphericFogEnvironment;applyWeatherDarken(Lorg/joml/Vector3fc;FF)Lorg/joml/Vector3fc;", ordinal = 0))
    private static Vector3fc modifyColorV2(Vector3fc original, @Local(argsOnly = true, name = "camera") Camera camera, @Local(argsOnly = true, name = "level") ClientLevel clientLevel, @Local(argsOnly = true, name = "partialTicks") float partialTicks){
        if(areMeteorsNearby(camera, clientLevel)){
            Vector3fc skyColor = new Vector3f(OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.r(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.g(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.b());

            Vector4fc color = camera.attributeProbe().getValue(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, partialTicks);
            float alpha = 0.95f;
            skyColor = ARGB.srgbLerp(alpha, skyColor, new Vector3f().set(color));
            return skyColor;
        }
        return original;
    }
    //TODO this also works
    /*@ModifyReturnValue(method = "getBaseColor", at = @At("RETURN"))
    private static Vector3fc modifyColorV2(Vector3fc original, @Local(argsOnly = true, name = "camera") Camera camera, @Local(argsOnly = true, name = "level") ClientLevel clientLevel, @Local(argsOnly = true, name = "partialTicks") float partialTicks){
        //TODO fix at some point
        if(areMeteorsNearby(camera, clientLevel)){
            //TODO maybe add a transition?
            //Default: Vec3(4,141,165) #048da5
            //Vector3f fogColor = new Vector3f(OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.r()*original.x(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.g()*original.y(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.b()*original.z());
            Vector3fc fogColor = new Vector3f(OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.r(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.g(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.b());
            //Vector3f fogColor = new Vector3f(OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.r()/original.x(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.g()/original.y(), OhMyMeteors.CONFIG.visualsSection.meteor_skyglow_color.b()/original.z());

            Vector4fc color = camera.attributeProbe().getValue(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, partialTicks);
            float alpha = 1f;
            fogColor = ARGB.srgbLerp(alpha, fogColor, new Vector3f().set(color));
            OhMyMeteors.LOGGER.info("the fog color is: " + fogColor + " the alfa: " + alpha);
            return fogColor;
        }
        return original;
    }*/

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