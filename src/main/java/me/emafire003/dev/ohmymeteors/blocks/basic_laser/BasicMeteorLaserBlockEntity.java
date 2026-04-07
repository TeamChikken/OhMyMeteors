package me.emafire003.dev.ohmymeteors.blocks.basic_laser;

import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

public class BasicMeteorLaserBlockEntity extends BlockEntity {
    public BasicMeteorLaserBlockEntity(BlockPos pos, BlockState state) {
        super(OMMBlocks.BASIC_METEOR_LASER_BLOCK_ENTITY.get(), pos, state);
    }
}
