package me.emafire003.dev.ohmymeteors.entities;

import me.emafire003.dev.ohmymeteors.mixin.CatCollarInvoker;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MeteorCatEntity extends Cat {

    public MeteorCatEntity(EntityType<? extends Cat> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    public static AttributeSupplier.Builder createCatAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 15.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.BURNING_TIME, 0); //So even if it gets on fire it won't last
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        if(source.is(DamageTypeTags.IS_FIRE)){
            return false;
        }
        return super.hurtServer(world, source, amount);
    }

    //TODO if to breed with cats, just needs to remove the "meteor" part
    //also should not need overriding
    @Nullable
    public MeteorCatEntity getBreedOffspring(ServerLevel serverWorld, AgeableMob passiveEntity) {
        MeteorCatEntity catEntity = OMMEntities.METEOR_KITTY_CAT.create(serverWorld, EntitySpawnReason.BREEDING);
        //EntityType.CAT.create(serverWorld);
        if (catEntity != null && passiveEntity instanceof MeteorCatEntity catEntity2) {


            if (this.isTame()) {
                catEntity.setOwner(this.getOwner());
                catEntity.setTame(true, true);
                if (this.random.nextBoolean()) {
                    ((CatCollarInvoker) catEntity).invokeSetCollarColor(this.getCollarColor());
                } else {
                    ((CatCollarInvoker) catEntity).invokeSetCollarColor(this.getCollarColor());
                }
            }
        }

        return catEntity;
    }

    @Override
    public boolean canMate(Animal other) {
        if (!this.isTame()) {
            return false;
        } else {
            return other instanceof MeteorCatEntity catEntity && catEntity.isTame() && super.canMate(other);
        }
    }

}
