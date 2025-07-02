package me.emafire003.dev.ohmymeteors.blocks.basic_laser;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.blocks.OMMProperties;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import me.emafire003.dev.ohmymeteors.items.OMMItems;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import me.emafire003.dev.ohmymeteors.sounds.OMMSounds;
import me.emafire003.dev.particleanimationlib.effects.CuboidEffect;
import me.emafire003.dev.particleanimationlib.effects.LineEffect;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//Ah remeber that the whole chunk is loaded when a meteor enters it so this will be loaded as well no need for fancy stuff
public class BasicMeteorLaserBlock extends BlockWithEntity implements BlockEntityProvider {

    /// This is used when interacting with the block. With a normal click the checking area will get highlited by particles
    public static final BooleanProperty SHOW_AREA = OMMProperties.SHOW_AREA;
    /// Used to display the "firing" texture of the laser model. Is true when it has just shot down a meteor
    public static final BooleanProperty FIRING = OMMProperties.FIRING;

    public static final BooleanProperty IN_COOLDOWN = OMMProperties.IN_COOLDOWN;

/*
    ///Is able to detect and destroy meteors this many blocks up from its position
    protected static final int Y_LEVEL_AREA_COVERAGE = 64;
    /// The radius in blocks that this type of laser can cover aka how fare on the xz plane it can detect and shoot meteors
    protected static final int RADIUS_AREA_COVERAGE = 48; //Which is around 3x3 chunks
*/
    /// Only awakens when a meteor is spawned somewhere in the world, to save up on checks
    //TODO migrate this to a property maybe
    private static boolean AWAKE = false;
    /// Used to determine for how long it should stay actively searching
    private static int tickCounterAwakening = -1;
    private static final int AWAKE_TIME_LIMIT = 20*25; //Should remain awake for 25 seconds after a meteor has spawned in

    public BasicMeteorLaserBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SHOW_AREA, false).with(IN_COOLDOWN, false).with(FIRING, false));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BasicMeteorLaserBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient && world.getDimension().hasSkyLight() ? checkType(type, OMMBlocks.BASIC_METEOR_LASER_BLOCK_ENTITY, BasicMeteorLaserBlock::tick) : null;
    }

        /**
         * Wakes up all the lasers to check for meteors above them.
         * They automatically go back to sleep after {@link #AWAKE_TIME_LIMIT} ticks*/
    public static void awakeLasers(){
        AWAKE = true;
        tickCounterAwakening = 0;
    }

    public static boolean areLasersAwake(){
        return AWAKE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        //Note: sneaking won't work since it disables this interaction
        if(stack.isOf(OMMItems.FOCUSING_LENSES)){
            BlockState blockState = state.cycle(SHOW_AREA);
            if(blockState.get(SHOW_AREA)){
                world.playSound(null, pos, OMMSounds.LASER_AREA_ON, SoundCategory.BLOCKS, 0.7f, 1f);
            }else{
                world.playSound(null, pos, OMMSounds.LASER_AREA_OFF, SoundCategory.BLOCKS, 0.7f, 1f);
            }
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    /// Yes it's very hacky, but only a small amount of blocks are going to be in cooldown at the same time, if any.
    /// In this case a block is identified by its blockentity, which is unique unlike its blockstate
    private static final ConcurrentHashMap<BlockEntity, Integer> BLOCKS_IN_COOLDOWN = new ConcurrentHashMap<>();

    /**Puts a laser block in cooldown for some time*/
    public static void putInCooldown(BlockEntity entity){
        BLOCKS_IN_COOLDOWN.put(entity, 0);
    }

    public static void removeCooldown(BlockEntity entity, BlockState state, World world, BlockPos pos){
        BLOCKS_IN_COOLDOWN.remove(entity);
        world.setBlockState(pos, state.with(IN_COOLDOWN, false));
    }

    /** This is the main logic of the block. Will check every tick the space around the y level where meteors spawn
     * to see if a meteor has spawned. If it has, it shoots it down.
     */
    protected static void tick(World world, BlockPos pos, BlockState state, BasicMeteorLaserBlockEntity blockEntity) {
        if(world instanceof ServerWorld serverWorld && world.isSkyVisible(pos.up())){

            if(Config.SHOULD_BASIC_LASER_COOLDOWN && BLOCKS_IN_COOLDOWN.containsKey(blockEntity)){
                //The cooldown is ended, keep on with the rest
                if(BLOCKS_IN_COOLDOWN.get(blockEntity) > Config.BASIC_LASER_COOLDOWN*20){
                    removeCooldown(blockEntity, state, world, pos);

                }else{//Increases the cooldown timer
                    BLOCKS_IN_COOLDOWN.put(blockEntity, BLOCKS_IN_COOLDOWN.getOrDefault(blockEntity, 0)+1);
                    return;
                }
            }

            //Checks if either the laser is awake or if it needs to show the area. If none of this are true, returns early
            if(!state.get(SHOW_AREA) && !AWAKE){
                return;
            }

            Box box = new Box(new BlockPos(pos.getX(), Math.min(pos.getY()+getYLevelAreaCoverage(), Config.METEOR_SPAWN_HEIGHT), pos.getZ())).expand(getRadiusAreaCoverage(), 1, getRadiusAreaCoverage());

            //useful to see where the box is, gets shown when the the show area blockstate property is true
            if(state.get(SHOW_AREA)){
                CuboidEffect cuboidEffect = CuboidEffect.builder(serverWorld, ParticleTypes.BUBBLE_POP, new Vec3d(box.minX, box.minY, box.minZ))
                        .particles(30).targetPos(new Vec3d(box.maxX, box.maxY, box.maxZ)).iterations(1)
                        .build();
                cuboidEffect.run();

                Vec3d lowerPos = new Vec3d(new Vec3d(box.maxX, box.maxY, box.maxZ).getX(), pos.getY(), new Vec3d(box.maxX, box.maxY, box.maxZ).getZ());

                //The two vertical lines at the angles
                LineEffect line = LineEffect
                        .builder(serverWorld, ParticleTypes.BUBBLE_POP, new Vec3d(box.maxX, box.maxY, box.maxZ))
                        .targetPos(lowerPos)
                        .particles((int) (lowerPos.distanceTo(new Vec3d(box.maxX, box.maxY, box.maxZ))))
                        .iterations(1)
                        .forced(true)
                        .build();
                line.run();

                lowerPos = new Vec3d(new Vec3d(box.minX, box.minY, box.minZ).getX(), pos.getY(), new Vec3d(box.minX, box.minY, box.minZ).getZ());
                line.setTargetPos(lowerPos);
                line.setOriginPos(new Vec3d(box.minX, box.minY, box.minZ));
                line.setParticles((int) (lowerPos.distanceTo(new Vec3d(box.minX, box.minY, box.minZ))));
                line.run();

                //The vertical line in the middle

                lowerPos = new Vec3d(box.getCenter().getX(), pos.getY(), box.getCenter().getZ());
                line.setTargetPos(lowerPos);
                line.setOriginPos(box.getCenter());
                line.setParticles((int) (lowerPos.distanceTo(box.getCenter())));
                line.run();

                //The horizontal lines at the top which point to the corner of the box
                lowerPos = new Vec3d(box.maxX, box.maxY, box.maxZ);
                line.setTargetPos(lowerPos);
                line.setOriginPos(box.getCenter());
                line.setParticles((int) (lowerPos.distanceTo(box.getCenter())));
                line.run();

                lowerPos = new Vec3d(box.minX, box.minY, box.minZ);
                line.setTargetPos(lowerPos);
                line.setOriginPos(box.getCenter());
                line.setParticles((int) (lowerPos.distanceTo(box.getCenter())));
                line.run();


            }

            if(!AWAKE){
                return;
            }
            //Makes sure this is awake
            if(tickCounterAwakening > AWAKE_TIME_LIMIT){
                tickCounterAwakening = 0;
                AWAKE = false;
                return;
            }

            List<MeteorProjectileEntity> meteors = world.getEntitiesByClass(MeteorProjectileEntity.class, box, (meteorProjectileEntity -> true));
            if(meteors == null || meteors.isEmpty()){
                return;
            }
            //From here it means there is at least one meteor, so activate the laser with the firing texture and stuff
            BlockState blockState = state.with(FIRING, true);
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);

            meteors.forEach( meteorProjectileEntity -> {

                if(meteorProjectileEntity.getSize() > Config.NATURAL_METEOR_MAX_SIZE/1.5){
                    meteorProjectileEntity.detonateScatter();
                }else{
                    meteorProjectileEntity.detonateSimple();
                }

                serverWorld.spawnParticles(OMMParticles.LASER_FLASH_PARTICLE, pos.up().up().getX(), pos.up().up().getY(), pos.up().up().getZ(), 2, 0.01, 0.01, 0.01, 0.1);

                LineEffect lineEffect = LineEffect
                        .builder(serverWorld, OMMParticles.LASER_PARTICLE, Vec3d.of(pos).add(0, 0.5, 0))
                        .targetPos(meteorProjectileEntity.getPos())
                        .forced(true)
                        .particles((int) (Vec3d.of(pos).distanceTo(meteorProjectileEntity.getPos())*3))
                        .build();
                lineEffect.runFor(1, (effect, t) -> {
                    //If the ticks are 19 it means the effect is about to end (1 second = 20 ticks), so revert back the state
                    if(t >= 19){
                        world.setBlockState(pos, state.with(FIRING, false).with(IN_COOLDOWN, true), Block.NOTIFY_LISTENERS);
                        putInCooldown(blockEntity);
                    }
                });


                //Plays the "pew" laser firing sound
                world.playSound(null, pos, OMMSounds.LASER_FIRE, SoundCategory.BLOCKS, 1f, 1.25f);


                if(Config.ANNOUNCE_METEOR_DESTROYED){
                    if(meteorProjectileEntity.isHuge()){
                        serverWorld.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("message.ohmymeteors.meteor_destroyed.huge").formatted(Formatting.GREEN)), Config.ACTIONBAR_ANNOUNCEMENTS));
                    }else{
                        serverWorld.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("message.ohmymeteors.meteor_destroyed").formatted(Formatting.GREEN)), Config.ACTIONBAR_ANNOUNCEMENTS));
                    }
                }
            });

            tickCounterAwakening++;
        }

    }

    protected static int getYLevelAreaCoverage(){
        return Config.BASIC_LASER_AREA_RADIUS;
    }

    protected static int getRadiusAreaCoverage(){
        return Config.BASIC_LASER_HEIGHT;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {

        builder.add(SHOW_AREA, FIRING, IN_COOLDOWN);
    }

    public VoxelShape makeShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0, 0, 0, 1, 0.625, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.4375, 0.625, 0.4375, 0.5625, 1, 0.5625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0, 0.8125, 0, 1, 0.875, 0.4375), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0, 0.625, 0, 0.0625, 0.8125, 0.0625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.9375, 0.625, 0, 1, 0.8125, 0.0625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0, 0.625, 0.9375, 0.0625, 0.8125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.9375, 0.625, 0.9375, 1, 0.8125, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0, 0.8125, 0.4375, 0.4375, 0.875, 0.5625), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0, 0.8125, 0.5625, 1, 0.875, 1), BooleanBiFunction.OR);
        shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0.5625, 0.8125, 0.4375, 1, 0.875, 0.5625), BooleanBiFunction.OR);

        return shape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return makeShape();
    }
}
