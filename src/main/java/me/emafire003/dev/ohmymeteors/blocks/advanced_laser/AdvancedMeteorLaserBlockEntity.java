package me.emafire003.dev.ohmymeteors.blocks.advanced_laser;

import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

public class AdvancedMeteorLaserBlockEntity extends BlockEntity {
    public AdvancedMeteorLaserBlockEntity(BlockPos pos, BlockState state) {
        super(OMMBlocks.ADVANCED_METEOR_LASER_BLOCK_ENTITY, pos, state);
    }
}
