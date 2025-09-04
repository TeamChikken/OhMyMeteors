//Credit to Explodee mod, Author: KnownSH https://github.com/KnownSH/Explodee
//The code is based around their mixin of the Explosion class
//MIT-0 License
/*
MIT No Attribution

Copyright 2025 KnownSH

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.emafire003.dev.ohmymeteors.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SphereExplosion extends Explosion {

    protected static final ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
    protected final boolean createFire;
    protected final Explosion.DestructionType destructionType;
    protected final Random random = Random.create();
    protected final World world;
    protected final double x;
    protected final double y;
    protected final double z;
    @Nullable
    protected final Entity entity;
    protected final float power;
    protected final DamageSource damageSource;
    protected final ExplosionBehavior behavior;
    protected final ParticleEffect particle;
    protected final ParticleEffect emitterParticle;
    protected final RegistryEntry<SoundEvent> soundEvent;
    protected final ObjectArrayList<BlockPos> affectedBlocks = new ObjectArrayList<>();
    protected final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.<PlayerEntity, Vec3d>newHashMap();
    

    public SphereExplosion(
            World world,
            @Nullable Entity entity,
            double x,
            double y,
            double z,
            float power,
            List<BlockPos> affectedBlocks,
            Explosion.DestructionType destructionType,
            ParticleEffect particle,
            ParticleEffect emitterParticle,
            RegistryEntry<SoundEvent> soundEvent
    ) {
        this(world, entity, createDamageSource(world, entity), null, x, y, z, power, false, destructionType, particle, emitterParticle, soundEvent);
        this.affectedBlocks.addAll(affectedBlocks);
    }

    public SphereExplosion(
            World world,
            @Nullable Entity entity,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            Explosion.DestructionType destructionType,
            List<BlockPos> affectedBlocks
    ) {
        this(world, entity, x, y, z, power, createFire, destructionType);
        this.affectedBlocks.addAll(affectedBlocks);
    }

    public SphereExplosion(
            World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType
    ) {
        this(
                world,
                entity,
                createDamageSource(world, entity),
                null,
                x,
                y,
                z,
                power,
                createFire,
                destructionType,
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.ENTITY_GENERIC_EXPLODE
        );
    }

    public SphereExplosion(
            World world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            Explosion.DestructionType destructionType,
            ParticleEffect particle,
            ParticleEffect emitterParticle,
            RegistryEntry<SoundEvent> soundEvent
    ) {
        super(world, entity, damageSource, behavior, x,y,z,power,createFire,destructionType,particle,emitterParticle,soundEvent);
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.x = x;
        this.y = y;
        this.z = z;
        this.createFire = createFire;
        this.destructionType = destructionType;
        this.damageSource = damageSource == null ? world.getDamageSources().explosion(this) : damageSource;
        this.behavior = behavior == null ? this.chooseBehavior(entity) : behavior;
        this.particle = particle;
        this.emitterParticle = emitterParticle;
        this.soundEvent = soundEvent;
    }

    private ExplosionBehavior chooseBehavior(@Nullable Entity entity) {
        return (entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity));
    }

    protected double thetaRef;
    protected double phiRef;

    private static final int MAX_POWER = 255;

    public double calcDakaXdirection(int l){
        double phi = Math.acos(1 - 2.0 * l / (int) Math.max(power * power, MAX_POWER));
        double theta = Math.PI * (1 + Math.sqrt(5)) * l;

        thetaRef = theta;
        phiRef = phi;
        return Math.sin(phi) * Math.cos(theta);
    }


    @Override
    public void collectBlocksAndDamageEntities() {
        this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, new Vec3d(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();

        for (int j = 15; j < 16; j++) {
            for (int k = 15; k < 16; k++) {
                for (int l = 0; l < (int) Math.max(power * power, MAX_POWER); l++) {
                    double d = calcDakaXdirection(l);
                    double e = Math.sin(phiRef) * Math.sin(thetaRef);
                    double f = Math.cos(phiRef);
                    double g = 1; //Math.sqrt(d * d + e * e + f * f);
                    d /= g;
                    e /= g;
                    f /= g;
                    float h = this.power * (0.7F + this.world.random.nextFloat() * 0.6F);
                    double m = this.x;
                    double n = this.y;
                    double o = this.z;

                    for (float p = 0.3F; h > 0.0F; h -= 0.22500001F) {
                        BlockPos blockPos = BlockPos.ofFloored(m, n, o);
                        BlockState blockState = this.world.getBlockState(blockPos);
                        FluidState fluidState = this.world.getFluidState(blockPos);
                        if (!this.world.isInBuildLimit(blockPos)) {
                            break;
                        }

                        Optional<Float> optional = this.behavior.getBlastResistance(this, this.world, blockPos, blockState, fluidState);
                        if (optional.isPresent()) {
                            h -= (optional.get() + 0.3F) * 0.3F;
                        }

                        if (h > 0.0F && this.behavior.canDestroyBlock(this, this.world, blockPos, blockState, h)) {
                            set.add(blockPos);
                        }

                        m += d * 0.3F;
                        n += e * 0.3F;
                        o += f * 0.3F;
                    }
                }
            }
        }

        this.affectedBlocks.addAll(set);
        float q = this.power * 2.0F;
        int k = MathHelper.floor(this.x - q - 1.0);
        int lx = MathHelper.floor(this.x + q + 1.0);
        int r = MathHelper.floor(this.y - q - 1.0);
        int s = MathHelper.floor(this.y + q + 1.0);
        int t = MathHelper.floor(this.z - q - 1.0);
        int u = MathHelper.floor(this.z + q + 1.0);
        List<Entity> list = this.world.getOtherEntities(this.entity, new Box(k, r, t, lx, s, u));
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

        for (Entity entity : list) {
            if (!entity.isImmuneToExplosion(this)) {
                double v = Math.sqrt(entity.squaredDistanceTo(vec3d)) / q;
                if (v <= 1.0) {
                    double w = entity.getX() - this.x;
                    double x = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
                    double y = entity.getZ() - this.z;
                    double z = Math.sqrt(w * w + x * x + y * y);
                    if (z != 0.0) {
                        w /= z;
                        x /= z;
                        y /= z;
                        if (this.behavior.shouldDamage(this, entity)) {
                            entity.damage(this.damageSource, this.behavior.calculateDamage(this, entity));
                        }

                        double aa = (1.0 - v) * getExposure(vec3d, entity) * this.behavior.getKnockbackModifier(entity);
                        double ab;
                        if (entity instanceof LivingEntity livingEntity) {
                            ab = aa * (1.0 - livingEntity.getAttributeValue(EntityAttributes.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE));
                        } else {
                            ab = aa;
                        }

                        w *= ab;
                        x *= ab;
                        y *= ab;
                        Vec3d vec3d2 = new Vec3d(w, x, y);
                        entity.setVelocity(entity.getVelocity().add(vec3d2));
                        if (entity instanceof PlayerEntity playerEntity && !playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                            this.affectedPlayers.put(playerEntity, vec3d2);
                        }

                        entity.onExplodedBy(this.entity);
                    }
                }
            }
        }
    }

    public static void tryMergeStack(List<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
        for (int i = 0; i < stacks.size(); i++) {
            Pair<ItemStack, BlockPos> pair = (Pair<ItemStack, BlockPos>)stacks.get(i);
            ItemStack itemStack = pair.getFirst();
            if (ItemEntity.canMerge(itemStack, stack)) {
                stacks.set(i, Pair.of(ItemEntity.merge(itemStack, stack, 16), pair.getSecond()));
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        stacks.add(Pair.of(stack, pos));
    }

    @Override
    public void affectWorld(boolean particles) {
        if (this.world.isClient) {
            this.world
                    .playSound(
                            this.x,
                            this.y,
                            this.z,
                            this.soundEvent.value(),
                            SoundCategory.BLOCKS,
                            4.0F,
                            (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
                            false
                    );
        }

        boolean bl = this.shouldDestroy();
        if (particles) {
            ParticleEffect particleEffect;
            if (!(this.power < 2.0F) && bl) {
                particleEffect = this.emitterParticle;
            } else {
                particleEffect = this.particle;
            }

            this.world.addParticle(particleEffect, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        }

        if (bl) {
            this.world.getProfiler().push("explosion_blocks");
            List<Pair<ItemStack, BlockPos>> list = new ArrayList();
            Util.shuffle(this.affectedBlocks, this.world.random);

            for (BlockPos blockPos : this.affectedBlocks) {
                this.world.getBlockState(blockPos).onExploded(this.world, blockPos, this, (stack, pos) -> tryMergeStack(list, stack, pos));
            }

            for (Pair<ItemStack, BlockPos> pair : list) {
                Block.dropStack(this.world, pair.getSecond(), pair.getFirst());
            }

            this.world.getProfiler().pop();
        }

        if (this.createFire) {
            for (BlockPos blockPos2 : this.affectedBlocks) {
                if (this.random.nextInt(3) == 0
                        && this.world.getBlockState(blockPos2).isAir()
                        && this.world.getBlockState(blockPos2.down()).isOpaqueFullCube(this.world, blockPos2.down())) {
                    this.world.setBlockState(blockPos2, AbstractFireBlock.getState(this.world, blockPos2));
                }
            }
        }
    }
}
