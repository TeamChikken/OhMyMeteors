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
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SphereExplosion extends Explosion {

    protected static final ExplosionDamageCalculator DEFAULT_BEHAVIOR = new ExplosionDamageCalculator();
    protected final boolean createFire;
    protected final Explosion.BlockInteraction destructionType;
    protected final RandomSource random = RandomSource.create();
    protected final Level world;
    protected final double x;
    protected final double y;
    protected final double z;
    @Nullable
    protected final Entity entity;
    protected final float power;
    protected final DamageSource damageSource;
    protected final ExplosionDamageCalculator behavior;
    /*protected final ParticleEffect particle;
    protected final ParticleEffect emitterParticle;
    protected final RegistryEntry<SoundEvent> soundEvent;*/
    protected final ObjectArrayList<BlockPos> affectedBlocks = new ObjectArrayList<>();
    protected final Map<Player, Vec3> affectedPlayers = Maps.newHashMap();


    public SphereExplosion(Level world, @Nullable Entity entity, double x, double y, double z, float power, List<BlockPos> affectedBlocks) {
        this(world, entity, x, y, z, power, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY, affectedBlocks);
    }

    public SphereExplosion(
            Level world,
            @Nullable Entity entity,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            Explosion.BlockInteraction destructionType,
            List<BlockPos> affectedBlocks
    ) {
        this(world, entity, x, y, z, power, createFire, destructionType);
        this.affectedBlocks.addAll(affectedBlocks);
    }

    public SphereExplosion(
            Level world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, Explosion.BlockInteraction destructionType
    ) {
        this(world, entity, null, null, x, y, z, power, createFire, destructionType);
    }


    public SphereExplosion(
            Level world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            Explosion.BlockInteraction destructionType
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
        this.damageSource = damageSource == null ? world.damageSources().explosion(this) : damageSource;
        this.behavior = behavior == null ? this.chooseBehavior(entity) : behavior;
        /*this.particle = particle;
        this.emitterParticle = emitterParticle;
        this.soundEvent = soundEvent;*/
    }

    private ExplosionDamageCalculator chooseBehavior(@Nullable Entity entity) {
        return (entity == null ? DEFAULT_BEHAVIOR : new EntityBasedExplosionDamageCalculator(entity));
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
    public void explode() {
        this.world.gameEvent(this.entity, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
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
                        BlockPos blockPos = BlockPos.containing(m, n, o);
                        BlockState blockState = this.world.getBlockState(blockPos);
                        FluidState fluidState = this.world.getFluidState(blockPos);
                        if (!this.world.isInWorldBounds(blockPos)) {
                            break;
                        }

                        Optional<Float> optional = this.behavior.getBlockExplosionResistance(this, this.world, blockPos, blockState, fluidState);
                        if (optional.isPresent()) {
                            h -= (optional.get() + 0.3F) * 0.3F;
                        }

                        if (h > 0.0F && this.behavior.shouldBlockExplode(this, this.world, blockPos, blockState, h)) {
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
        int k = Mth.floor(this.x - q - 1.0);
        int lx = Mth.floor(this.x + q + 1.0);
        int r = Mth.floor(this.y - q - 1.0);
        int s = Mth.floor(this.y + q + 1.0);
        int t = Mth.floor(this.z - q - 1.0);
        int u = Mth.floor(this.z + q + 1.0);
        List<Entity> list = this.world.getEntities(this.entity, new AABB(k, r, t, lx, s, u));
        Vec3 vec3d = new Vec3(this.x, this.y, this.z);

        for (Entity entity : list) {
            if (!entity.ignoreExplosion()) {
                double v = Math.sqrt(entity.distanceToSqr(vec3d)) / q;
                if (v <= 1.0) {
                    double x = entity.getX() - this.x;
                    double y = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                    double z = entity.getZ() - this.z;
                    double aa = Math.sqrt(x * x + y * y + z * z);
                    if (aa != 0.0) {
                        x /= aa;
                        y /= aa;
                        z /= aa;
                        double ab = getSeenPercent(vec3d, entity);
                        double ac = (1.0 - v) * ab;
                        entity.hurt(this.getDamageSource(), (int)((ac * ac + ac) / 2.0 * 7.0 * q + 1.0));
                        double ad;
                        if (entity instanceof LivingEntity livingEntity) {
                            ad = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingEntity, ac);
                        } else {
                            ad = ac;
                        }

                        x *= ad;
                        y *= ad;
                        z *= ad;
                        Vec3 vec3d2 = new Vec3(x, y, z);
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vec3d2));
                        if (entity instanceof Player playerEntity && !playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                            this.affectedPlayers.put(playerEntity, vec3d2);
                        }
                    }
                }
            }
        }
    }

    public static void tryMergeStack(List<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
        for (int i = 0; i < stacks.size(); i++) {
            Pair<ItemStack, BlockPos> pair = (Pair<ItemStack, BlockPos>)stacks.get(i);
            ItemStack itemStack = pair.getFirst();
            if (ItemEntity.areMergable(itemStack, stack)) {
                stacks.set(i, Pair.of(ItemEntity.merge(itemStack, stack, 16), pair.getSecond()));
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        stacks.add(Pair.of(stack, pos));
    }

    @Override
    public void finalizeExplosion(boolean particles) {
        if (this.world.isClientSide) {
            this.world
                    .playLocalSound(
                            this.x,
                            this.y,
                            this.z,
                            SoundEvents.GENERIC_EXPLODE,
                            SoundSource.BLOCKS,
                            4.0F,
                            (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F,
                            false
                    );
        }else{
            world.playSound(
                    null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                    BlockPos.containing(x,y,z), // The position of where the sound will come from
                    SoundEvents.GENERIC_EXPLODE, // The sound that will play, in this case, the sound the anvil plays when it lands.
                    SoundSource.BLOCKS, // This determines which of the volume sliders affect this sound
                    4f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                    (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F
            );
        }


        boolean bl = this.interactsWithBlocks();
        if (particles) {
            if (!(this.power < 2.0F) && bl) {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            } else {
                this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            }
        }

        if (bl) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList<>();
            boolean bl2 = this.getIndirectSourceEntity() instanceof Player;
            Util.shuffle(this.affectedBlocks, this.world.random);

            for (BlockPos blockPos : this.affectedBlocks) {
                BlockState blockState = this.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (!blockState.isAir()) {
                    BlockPos blockPos2 = blockPos.immutable();
                    this.world.getProfiler().push("explosion_blocks");
                    if (block.dropFromExplosion(this) && this.world instanceof ServerLevel serverWorld) {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
                        LootParams.Builder builder = new LootParams.Builder(serverWorld)
                                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos))
                                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                                .withOptionalParameter(LootContextParams.THIS_ENTITY, this.entity);
                        if (this.destructionType == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                            builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.power);
                        }

                        blockState.spawnAfterBreak(serverWorld, blockPos, ItemStack.EMPTY, bl2);
                        blockState.getDrops(builder).forEach(stack -> tryMergeStack(objectArrayList, stack, blockPos2));
                    }

                    this.world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    block.wasExploded(this.world, blockPos, this);
                    this.world.getProfiler().pop();
                }
            }

            for (Pair<ItemStack, BlockPos> pair : objectArrayList) {
                Block.popResource(this.world, pair.getSecond(), pair.getFirst());
            }
        }

        if (this.createFire) {
            for (BlockPos blockPos3 : this.affectedBlocks) {
                if (this.random.nextInt(3) == 0
                        && this.world.getBlockState(blockPos3).isAir()
                        && this.world.getBlockState(blockPos3.below()).isSolidRender(this.world, blockPos3.below())) {
                    this.world.setBlockAndUpdate(blockPos3, BaseFireBlock.getState(this.world, blockPos3));
                }
            }
        }
    }
}
