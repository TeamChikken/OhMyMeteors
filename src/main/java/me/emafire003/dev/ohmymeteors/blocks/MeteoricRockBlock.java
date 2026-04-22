package me.emafire003.dev.ohmymeteors.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.CONFIG;

public class MeteoricRockBlock extends Block {
    public MeteoricRockBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(ROCK_TEMPERATURE, RockTemperature.HOT).setValue(PRESERVED, false));

    }
    //could be used later to add functionality to this block, and in fact, it is

    public static EnumProperty<RockTemperature> ROCK_TEMPERATURE = OMMProperties.ROCK_TEMPERATURE;
    public static BooleanProperty PRESERVED = OMMProperties.PRESERVED;

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if(state.getValue(ROCK_TEMPERATURE).equals(RockTemperature.HOT)){
            damageOnStep(entity, level);
        }else if(state.getValue(ROCK_TEMPERATURE).equals(RockTemperature.MID)){
            if(level.getRandom().nextInt(1, 15) == 2){
                damageOnStep(entity, level);
            }
        }

        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide()){
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if(stack.is(Items.ECHO_SHARD)){
            if(state.getValue(PRESERVED)){
                ((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.WAX_OFF, CONFIG.visualsSection.use_forced_particles,
                        pos.getCenter().x(), pos.getCenter().y(), pos.getCenter().z(),
                        30, 0.5, 0.5, 0.5, 1.0);
                level.setBlockAndUpdate(pos, state.setValue(PRESERVED, false));
            }else{
                ((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.WAX_ON, CONFIG.visualsSection.use_forced_particles,
                        pos.getCenter().x(), pos.getCenter().y(), pos.getCenter().z(),
                        30, 0.5, 0.5, 0.5, 1.0);
                level.setBlockAndUpdate(pos, state.setValue(PRESERVED, true));
            }
            return ItemInteractionResult.SUCCESS;
        }
        if(state.getValue(PRESERVED)){
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if(stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE)){
            ((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.SMOKE, CONFIG.visualsSection.use_forced_particles,
                    pos.getCenter().x(), pos.getCenter().y(), pos.getCenter().z(),
                    30, 0.05, 0.05, 0.05, 0.2);
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1f, 1.4f);
            promoteHotness(state, pos, level);
            return ItemInteractionResult.CONSUME;
        }
        if(stack.is(Items.SNOWBALL)){
            ((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.SMOKE, CONFIG.visualsSection.use_forced_particles,
                    pos.getCenter().x(), pos.getCenter().y(), pos.getCenter().z(),
                    30, 0.05, 0.05, 0.05, 0.5);
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1f, 1.4f);
            demoteHotness(state, pos, level);
            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    protected void promoteHotness(BlockState state, BlockPos pos, Level level){
        switch (state.getValue(ROCK_TEMPERATURE)){
            case HOT -> {
                return;
            }case MID -> level.setBlockAndUpdate(pos, state.setValue(ROCK_TEMPERATURE, RockTemperature.HOT));
            case NORMAL -> level.setBlockAndUpdate(pos, state.setValue(ROCK_TEMPERATURE, RockTemperature.MID));
            default -> throw new IllegalStateException("Unexpected value: " + state.getValue(ROCK_TEMPERATURE));
        }

    }

    protected void demoteHotness(BlockState state, BlockPos pos, Level level){
        switch (state.getValue(ROCK_TEMPERATURE)){
            case NORMAL -> {
                return;
            }case MID -> level.setBlockAndUpdate(pos, state.setValue(ROCK_TEMPERATURE, RockTemperature.NORMAL));
            case HOT -> level.setBlockAndUpdate(pos, state.setValue(ROCK_TEMPERATURE, RockTemperature.MID));
            default -> throw new IllegalStateException("Unexpected value: " + state.getValue(ROCK_TEMPERATURE));
        }
    }

    protected void damageOnStep(Entity entity, Level level){
        if (!entity.isSteppingCarefully() && entity instanceof LivingEntity) {
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
        }
    }



    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, Mth.nextInt(level.getRandom(), 60, 120));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(state.getValue(PRESERVED)){
            return;
        }
        if ((random.nextInt(100) == 0 || (this.fewerNeigboursThan(level, pos, 4) && random.nextInt(25) == 0))) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            demoteHotness(state, pos, level);

            for (Direction direction : Direction.values()) {
                mutableBlockPos.setWithOffset(pos, direction);
                BlockState blockState = level.getBlockState(mutableBlockPos);
                if (blockState.is(this)) {
                    level.scheduleTick(mutableBlockPos, this, Mth.nextInt(random, 20, 40));
                }
            }
        } else {
            level.scheduleTick(pos, this, Mth.nextInt(random, 20, 40));
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (neighborBlock.defaultBlockState().is(this) && this.fewerNeigboursThan(level, pos, 2)) {
            this.demoteHotness(state, pos, level);
        }

        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    private boolean fewerNeigboursThan(BlockGetter level, BlockPos pos, int neighborsRequired) {
        int i = 0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            mutableBlockPos.setWithOffset(pos, direction);
            if (level.getBlockState(mutableBlockPos).is(this)) {
                if (++i >= neighborsRequired) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROCK_TEMPERATURE, PRESERVED);
    }
}
