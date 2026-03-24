package me.emafire003.dev.ohmymeteors.entities.client;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class MeteorProjectileAnimations {
    public static final AnimationDefinition METEOR_ROTATION =  AnimationDefinition.Builder.withLength(2.0F).looping()
            .addAnimation("main", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(360.0F, -360.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();
}
