package me.emafire003.dev.ohmymeteors.entities;

import me.emafire003.dev.ohmymeteors.mixin.CatCollarInvoker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MeteorCatEntity extends CatEntity {

    public MeteorCatEntity(EntityType<? extends CatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    public static DefaultAttributeContainer.Builder createCatAttributes() {
        return AnimalEntity.createAnimalAttributes()
                .add(EntityAttributes.MAX_HEALTH, 15.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.35F)
                .add(EntityAttributes.ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.BURNING_TIME, 0); //So even if it gets on fire it won't last
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if(source.isIn(DamageTypeTags.IS_FIRE)){
            return false;
        }
        return super.damage(world, source, amount);
    }

    //TODO if to breed with cats, just needs to remove the "meteor" part
    //also should not need overriding
    @Nullable
    public MeteorCatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        MeteorCatEntity catEntity = OMMEntities.METEOR_KITTY_CAT.create(serverWorld, SpawnReason.BREEDING);
        //EntityType.CAT.create(serverWorld);
        if (catEntity != null && passiveEntity instanceof MeteorCatEntity catEntity2) {


            if (this.isTamed()) {
                catEntity.setOwner(this.getOwner());
                catEntity.setTamed(true, true);
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
    public boolean canBreedWith(AnimalEntity other) {
        if (!this.isTamed()) {
            return false;
        } else {
            return other instanceof MeteorCatEntity catEntity && catEntity.isTamed() && super.canBreedWith(other);
        }
    }

}
