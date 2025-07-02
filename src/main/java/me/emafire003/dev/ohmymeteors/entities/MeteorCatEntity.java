package me.emafire003.dev.ohmymeteors.entities;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.mixin.CatCollarInvoker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
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
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }


    @Override
    public boolean damage(DamageSource source, float amount) {
        if(source.isFire()){
            return false;
        }
        return super.damage(source, amount);
    }

    //Compat stuff aka removing variants and such.
    // The idea is, always return the same texture but keep the tracked data there in order not to cause too many issues.
    // The only other way would be copying over the methods of the cat to a new Entity without extending the cat
    @Override
    public Identifier getTexture() {
        return OhMyMeteors.getIdentifier("textures/entity/meteor_cat.png");
    }

    /*@Override
    public RegistryEntry<CatVariant> getVariant() {
        return null;
    }
*/

    //TODO if to breed with cats, just needs to remove the "meteor" part
    //also should not need overriding
    @Nullable
    public MeteorCatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        MeteorCatEntity catEntity = OMMEntities.METEOR_KITTY_CAT.create(serverWorld);
        //EntityType.CAT.create(serverWorld);
        if (catEntity != null && passiveEntity instanceof MeteorCatEntity catEntity2) {


            if (this.isTamed()) {
                catEntity.setOwnerUuid(this.getOwnerUuid());
                catEntity.setTamed(true);
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
