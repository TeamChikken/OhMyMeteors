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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class SphereExplosion implements Explosion {
    private static final ExplosionDamageCalculator DEFAULT_BEHAVIOR = new ExplosionDamageCalculator();
    private static final int field_52618 = 16;
    private static final float field_52619 = 2.0F;
    private final boolean createFire;
    private final Explosion.BlockInteraction destructionType;
    private final ServerLevel world;
    private final Vec3 pos;
    @Nullable
    private final Entity entity;
    private final float power;
    private final DamageSource damageSource;
    private final ExplosionDamageCalculator behavior;
    private final Map<Player, Vec3> knockbackByPlayer = new HashMap<>();

    public SphereExplosion(
            ServerLevel world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionDamageCalculator behavior,
            Vec3 pos,
            float power,
            boolean createFire,
            Explosion.BlockInteraction destructionType
    ) {
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.pos = pos;
        this.createFire = createFire;
        this.destructionType = destructionType;
        this.damageSource = damageSource == null ? world.damageSources().explosion(this) : damageSource;
        this.behavior = behavior == null ? this.makeBehavior(entity) : behavior;
    }

    private ExplosionDamageCalculator makeBehavior(@Nullable Entity entity) {
        return (ExplosionDamageCalculator)(entity == null ? DEFAULT_BEHAVIOR : new EntityBasedExplosionDamageCalculator(entity));
    }

    public static float calculateReceivedDamage(Vec3 pos, Entity entity) {
        AABB box = entity.getBoundingBox();
        double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
        if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
            int i = 0;
            int j = 0;

            for (double k = 0.0; k <= 1.0; k += d) {
                for (double l = 0.0; l <= 1.0; l += e) {
                    for (double m = 0.0; m <= 1.0; m += f) {
                        double n = Mth.lerp(k, box.minX, box.maxX);
                        double o = Mth.lerp(l, box.minY, box.maxY);
                        double p = Mth.lerp(m, box.minZ, box.maxZ);
                        Vec3 vec3d = new Vec3(n + g, o, p + h);
                        if (entity.level().clip(new ClipContext(vec3d, pos, net.minecraft.world.level.ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType()
                                == HitResult.Type.MISS) {
                            i++;
                        }

                        j++;
                    }
                }
            }

            return (float)i / j;
        } else {
            return 0.0F;
        }
    }

    @Override
    public float radius() {
        return this.power;
    }

    @Override
    public Vec3 center() {
        return this.pos;
    }

    private List<BlockPos> getBlocksToDestroy() {
        Set<BlockPos> set = new HashSet();
        int i = 16;

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
                    double m = this.pos.x;
                    double n = this.pos.y;
                    double o = this.pos.z;

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

        return new ObjectArrayList<>(set);
    }

    private void damageEntities() {
        float f = this.power * 2.0F;
        int i = Mth.floor(this.pos.x - f - 1.0);
        int j = Mth.floor(this.pos.x + f + 1.0);
        int k = Mth.floor(this.pos.y - f - 1.0);
        int l = Mth.floor(this.pos.y + f + 1.0);
        int m = Mth.floor(this.pos.z - f - 1.0);
        int n = Mth.floor(this.pos.z + f + 1.0);

        for (Entity entity : this.world.getEntities(this.entity, new AABB(i, k, m, j, l, n))) {
            if (!entity.ignoreExplosion(this)) {
                double d = Math.sqrt(entity.distanceToSqr(this.pos)) / f;
                if (d <= 1.0) {
                    double e = entity.getX() - this.pos.x;
                    double g = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.pos.y;
                    double h = entity.getZ() - this.pos.z;
                    double o = Math.sqrt(e * e + g * g + h * h);
                    if (o != 0.0) {
                        e /= o;
                        g /= o;
                        h /= o;
                        boolean bl = this.behavior.shouldDamageEntity(this, entity);
                        float p = this.behavior.getKnockbackMultiplier(entity);
                        float q = !bl && p == 0.0F ? 0.0F : calculateReceivedDamage(this.pos, entity);
                        if (bl) {
                            entity.hurtServer(this.world, this.damageSource, this.behavior.getEntityDamageAmount(this, entity, q));
                        }

                        double r = (1.0 - d) * q * p;
                        double s;
                        if (entity instanceof LivingEntity livingEntity) {
                            s = r * (1.0 - livingEntity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
                        } else {
                            s = r;
                        }

                        e *= s;
                        g *= s;
                        h *= s;
                        Vec3 vec3d = new Vec3(e, g, h);
                        entity.push(vec3d);
                        if (entity instanceof Player playerEntity && !playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                            this.knockbackByPlayer.put(playerEntity, vec3d);
                        }

                        entity.onExplosionHit(this.entity);
                    }
                }
            }
        }
    }

    private void destroyBlocks(List<BlockPos> positions) {
        List<DroppedItem> list = new ArrayList();
        Util.shuffle(positions, this.world.random);

        for (BlockPos blockPos : positions) {
            this.world.getBlockState(blockPos).onExplosionHit(this.world, blockPos, this, (item, pos) -> addDroppedItem(list, item, pos));
        }

        for (DroppedItem droppedItem : list) {
            Block.popResource(this.world, droppedItem.pos, droppedItem.item);
        }
    }

    private void createFire(List<BlockPos> positions) {
        for (BlockPos blockPos : positions) {
            if (this.world.random.nextInt(3) == 0 && this.world.getBlockState(blockPos).isAir() && this.world.getBlockState(blockPos.below()).isSolidRender()) {
                this.world.setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.world, blockPos));
            }
        }
    }


    private static void addDroppedItem(List<DroppedItem> droppedItemsOut, ItemStack item, BlockPos pos) {
        for (DroppedItem droppedItem : droppedItemsOut) {
            droppedItem.merge(item);
            if (item.isEmpty()) {
                return;
            }
        }

        droppedItemsOut.add(new DroppedItem(pos, item));
    }

    private boolean shouldDestroyBlocks() {
        return this.destructionType != Explosion.BlockInteraction.KEEP;
    }

    public Map<Player, Vec3> getKnockbackByPlayer() {
        return this.knockbackByPlayer;
    }

    @Override
    public ServerLevel level() {
        return this.world;
    }

    @Nullable
    @Override
    public LivingEntity getIndirectSourceEntity() {
        return Explosion.getIndirectSourceEntity(this.entity);
    }

    @Nullable
    @Override
    public Entity getDirectSourceEntity() {
        return this.entity;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    @Override
    public Explosion.BlockInteraction getBlockInteraction() {
        return this.destructionType;
    }

    @Override
    public boolean canTriggerBlocks() {
        if (this.destructionType != Explosion.BlockInteraction.TRIGGER_BLOCK) {
            return false;
        } else {
            return this.entity != null && this.entity.getType() == EntityType.BREEZE_WIND_CHARGE
                    ? this.world.getGameRules().get(GameRules.MOB_GRIEFING)
                    : true;
        }
    }

    @Override
    public boolean shouldAffectBlocklikeEntities() {
        boolean bl = this.world.getGameRules().get(GameRules.MOB_GRIEFING);
        boolean bl2 = this.entity == null || this.entity.getType() != EntityType.BREEZE_WIND_CHARGE && this.entity.getType() != EntityType.WIND_CHARGE;
        return bl ? bl2 : this.destructionType.shouldAffectBlocklikeEntities() && bl2;
    }

    public boolean isSmall() {
        return this.power < 2.0F || !this.shouldDestroyBlocks();
    }

    static class DroppedItem {
        final BlockPos pos;
        ItemStack item;

        DroppedItem(BlockPos pos, ItemStack item) {
            this.pos = pos;
            this.item = item;
        }

        public void merge(ItemStack other) {
            if (ItemEntity.areMergable(this.item, other)) {
                this.item = ItemEntity.merge(this.item, other, 16);
            }
        }
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

    public int explode() {
        this.world.gameEvent(this.entity, GameEvent.EXPLODE, this.pos);
        List<BlockPos> list = this.getBlocksToDestroy();
        this.damageEntities();
        if (this.shouldDestroyBlocks()) {
            ProfilerFiller profiler = Profiler.get();
            profiler.push("explosion_blocks");
            this.destroyBlocks(list);
            profiler.pop();
        }

        if (this.createFire) {
            this.createFire(list);
        }

        return list.size();
    }
}
