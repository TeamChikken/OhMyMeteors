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
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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
    /*protected final ParticleEffect particle;
    protected final ParticleEffect emitterParticle;
    protected final RegistryEntry<SoundEvent> soundEvent;*/
    protected final ObjectArrayList<BlockPos> affectedBlocks = new ObjectArrayList<>();
    protected final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.newHashMap();


    public SphereExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power) {
        this(world, entity, x, y, z, power, false, Explosion.DestructionType.DESTROY);
    }

    public SphereExplosion(World world, @Nullable Entity entity, double x, double y, double z, float power, List<BlockPos> affectedBlocks) {
        this(world, entity, x, y, z, power, false, Explosion.DestructionType.DESTROY, affectedBlocks);
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
        this(world, entity, null, null, x, y, z, power, createFire, destructionType);
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
            Explosion.DestructionType destructionType
    ) {
        super(world, entity, damageSource, behavior, x,y,z, power, createFire, destructionType);
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.x = x;
        this.y = y;
        this.z = z;
        this.createFire = createFire;
        this.destructionType = destructionType;
        this.damageSource = damageSource == null ? DamageSource.explosion(this) : damageSource;
        this.behavior = behavior == null ? this.chooseBehavior(entity) : behavior;
    }

    private ExplosionBehavior chooseBehavior(@Nullable Entity entity) {
        return (ExplosionBehavior)(entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity));
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
                        BlockPos blockPos = new BlockPos(m, n, o);
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
            if (!entity.isImmuneToExplosion()) {
                double v = Math.sqrt(entity.squaredDistanceTo(vec3d)) / q;
                if (v <= 1.0) {
                    double x = entity.getX() - this.x;
                    double y = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
                    double z = entity.getZ() - this.z;
                    double aa = Math.sqrt(x * x + y * y + z * z);
                    if (aa != 0.0) {
                        x /= aa;
                        y /= aa;
                        z /= aa;
                        double ab = getExposure(vec3d, entity);
                        double ac = (1.0 - v) * ab;
                        entity.damage(this.getDamageSource(), (int)((ac * ac + ac) / 2.0 * 7.0 * q + 1.0));
                        double ad;
                        if (entity instanceof LivingEntity livingEntity) {
                            ad = ProtectionEnchantment.transformExplosionKnockback(livingEntity, ac);
                        } else {
                            ad = ac;
                        }

                        x *= ad;
                        y *= ad;
                        z *= ad;
                        Vec3d vec3d2 = new Vec3d(x, y, z);
                        entity.setVelocity(entity.getVelocity().add(vec3d2));
                        if (entity instanceof PlayerEntity playerEntity && !playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                            this.affectedPlayers.put(playerEntity, vec3d2);
                        }
                    }
                }
            }
        }
    }

    public void affectWorld(boolean particles) {
        if (this.world.isClient) {
            this.world
                    .playSound(
                            this.x,
                            this.y,
                            this.z,
                            SoundEvents.ENTITY_GENERIC_EXPLODE,
                            SoundCategory.BLOCKS,
                            4.0F,
                            (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
                            false
                    );
        } else{
            world.playSound(
                    null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                    new BlockPos(x,y,z), // The position of where the sound will come from
                    SoundEvents.ENTITY_GENERIC_EXPLODE, // The sound that will play, in this case, the sound the anvil plays when it lands.
                    SoundCategory.BLOCKS, // This determines which of the volume sliders affect this sound
                    4f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                    (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F
            );
        }

        boolean bl = this.destructionType != Explosion.DestructionType.NONE;
        if (particles) {
            if (!(this.power < 2.0F) && bl) {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            } else {
                this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            }
        }

        if (bl) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList<>();
            boolean bl2 = this.getCausingEntity() instanceof PlayerEntity;
            Util.shuffle(this.affectedBlocks, this.world.random);

            for (BlockPos blockPos : this.affectedBlocks) {
                BlockState blockState = this.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (!blockState.isAir()) {
                    BlockPos blockPos2 = blockPos.toImmutable();
                    this.world.getProfiler().push("explosion_blocks");
                    if (block.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld serverWorld) {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
                        LootContext.Builder builder = new LootContext.Builder(serverWorld)
                                .random(this.world.random)
                                .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
                                .parameter(LootContextParameters.TOOL, ItemStack.EMPTY)
                                .optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity)
                                .optionalParameter(LootContextParameters.THIS_ENTITY, this.entity);
                        if (this.destructionType == Explosion.DestructionType.DESTROY) {
                            builder.parameter(LootContextParameters.EXPLOSION_RADIUS, this.power);
                        }

                        blockState.onStacksDropped(serverWorld, blockPos, ItemStack.EMPTY, bl2);
                        blockState.getDroppedStacks(builder).forEach(stack -> tryMergeStack(objectArrayList, stack, blockPos2));
                    }

                    this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    block.onDestroyedByExplosion(this.world, blockPos, this);
                    this.world.getProfiler().pop();
                }
            }

            for (Pair<ItemStack, BlockPos> pair : objectArrayList) {
                Block.dropStack(this.world, pair.getSecond(), pair.getFirst());
            }
        }

        if (this.createFire) {
            for (BlockPos blockPos3 : this.affectedBlocks) {
                if (this.random.nextInt(3) == 0
                        && this.world.getBlockState(blockPos3).isAir()
                        && this.world.getBlockState(blockPos3.down()).isOpaqueFullCube(this.world, blockPos3.down())) {
                    this.world.setBlockState(blockPos3, AbstractFireBlock.getState(this.world, blockPos3));
                }
            }
        }
    }

    private static void tryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
        int i = stacks.size();

        for (int j = 0; j < i; j++) {
            Pair<ItemStack, BlockPos> pair = stacks.get(j);
            ItemStack itemStack = pair.getFirst();
            if (ItemEntity.canMerge(itemStack, stack)) {
                ItemStack itemStack2 = ItemEntity.merge(itemStack, stack, 16);
                stacks.set(j, Pair.of(itemStack2, pair.getSecond()));
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        stacks.add(Pair.of(stack, pos));
    }

}
