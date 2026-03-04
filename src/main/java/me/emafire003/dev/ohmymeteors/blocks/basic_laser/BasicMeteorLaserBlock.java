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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//Ah remeber that the whole chunk is loaded when a meteor enters it so this will be loaded as well no need for fancy stuff
public class BasicMeteorLaserBlock extends BaseEntityBlock implements EntityBlock {

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

    public BasicMeteorLaserBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(SHOW_AREA, false).setValue(IN_COOLDOWN, false).setValue(FIRING, false));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicMeteorLaserBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return !world.isClientSide && world.dimensionType().hasSkyLight() ? createTickerHelper(type, OMMBlocks.BASIC_METEOR_LASER_BLOCK_ENTITY, BasicMeteorLaserBlock::tick) : null;
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        //Note: sneaking won't work since it disables this interaction
        if(stack.is(OMMItems.FOCUSING_LENSES)){
            BlockState blockState = state.cycle(SHOW_AREA);
            if(blockState.getValue(SHOW_AREA)){
                world.playSound(null, pos, OMMSounds.LASER_AREA_ON, SoundSource.BLOCKS, 0.7f, 1f);
            }else{
                world.playSound(null, pos, OMMSounds.LASER_AREA_OFF, SoundSource.BLOCKS, 0.7f, 1f);
            }
            world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    /// Yes it's very hacky, but only a small amount of blocks are going to be in cooldown at the same time, if any.
    /// In this case a block is identified by its blockentity, which is unique unlike its blockstate
    private static final ConcurrentHashMap<BlockEntity, Integer> BLOCKS_IN_COOLDOWN = new ConcurrentHashMap<>();

    /**Puts a laser block in cooldown for some time*/
    public static void putInCooldown(BlockEntity entity){
        BLOCKS_IN_COOLDOWN.put(entity, 0);
    }

    public static void removeCooldown(BlockEntity entity, BlockState state, Level world, BlockPos pos){
        BLOCKS_IN_COOLDOWN.remove(entity);
        world.setBlockAndUpdate(pos, state.setValue(IN_COOLDOWN, false));
    }

    /** This is the main logic of the block. Will check every tick the space around the y level where meteors spawn
     * to see if a meteor has spawned. If it has, it shoots it down.
     */
    protected static void tick(Level world, BlockPos pos, BlockState state, BasicMeteorLaserBlockEntity blockEntity) {
        if(world instanceof ServerLevel serverWorld && world.canSeeSky(pos.above())){

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
            if(!state.getValue(SHOW_AREA) && !AWAKE){
                return;
            }

            AABB box = new AABB(new BlockPos(pos.getX(), Math.min(pos.getY()+getYLevelAreaCoverage(), Config.METEOR_SPAWN_HEIGHT), pos.getZ())).inflate(getRadiusAreaCoverage(), 1, getRadiusAreaCoverage());

            //useful to see where the box is, gets shown when the the show area blockstate property is true
            if(state.getValue(SHOW_AREA)){
                CuboidEffect cuboidEffect = CuboidEffect.builder(serverWorld, ParticleTypes.BUBBLE_POP, new Vec3(box.minX, box.minY, box.minZ))
                        .particles(30).targetPos(new Vec3(box.maxX, box.maxY, box.maxZ)).iterations(1)
                        .build();
                cuboidEffect.run();

                Vec3 lowerPos = new Vec3(new Vec3(box.maxX, box.maxY, box.maxZ).x(), pos.getY(), new Vec3(box.maxX, box.maxY, box.maxZ).z());

                //The two vertical lines at the angles
                LineEffect line = LineEffect
                        .builder(serverWorld, ParticleTypes.BUBBLE_POP, new Vec3(box.maxX, box.maxY, box.maxZ))
                        .targetPos(lowerPos)
                        .particles((int) (lowerPos.distanceTo(new Vec3(box.maxX, box.maxY, box.maxZ))))
                        .iterations(1)
                        .build();
                line.run();

                lowerPos = new Vec3(new Vec3(box.minX, box.minY, box.minZ).x(), pos.getY(), new Vec3(box.minX, box.minY, box.minZ).z());
                line.setTargetPos(lowerPos);
                line.setOriginPos(new Vec3(box.minX, box.minY, box.minZ));
                line.setParticles((int) (lowerPos.distanceTo(new Vec3(box.minX, box.minY, box.minZ))));
                line.run();

                //The vertical line in the middle

                lowerPos = new Vec3(box.getCenter().x(), pos.getY(), box.getCenter().z());
                line.setTargetPos(lowerPos);
                line.setOriginPos(box.getCenter());
                line.setParticles((int) (lowerPos.distanceTo(box.getCenter())));
                line.setForced(Config.USE_FORCED_PARTICLES);
                line.run();

                //The horizontal lines at the top which point to the corner of the box
                lowerPos = new Vec3(box.maxX, box.maxY, box.maxZ);
                line.setTargetPos(lowerPos);
                line.setOriginPos(box.getCenter());
                line.setForced(false);
                line.setParticles((int) (lowerPos.distanceTo(box.getCenter())));
                line.run();

                lowerPos = new Vec3(box.minX, box.minY, box.minZ);
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

            List<MeteorProjectileEntity> meteors = world.getEntitiesOfClass(MeteorProjectileEntity.class, box, (meteorProjectileEntity -> true));
            if(meteors == null || meteors.isEmpty()){
                return;
            }
            //From here it means there is at least one meteor, so activate the laser with the firing texture and stuff
            BlockState blockState = state.setValue(FIRING, true);
            world.setBlock(pos, blockState, Block.UPDATE_CLIENTS);


            meteors.forEach( meteorProjectileEntity -> {

                if(meteorProjectileEntity.getSize() > Config.NATURAL_METEOR_MAX_SIZE/1.5){
                    meteorProjectileEntity.detonateScatter();
                }else{
                    meteorProjectileEntity.detonateSimple();
                }

                serverWorld.sendParticles(OMMParticles.LASER_FLASH_PARTICLE, pos.above().above().getX(), pos.above().above().getY(), pos.above().above().getZ(), 2, 0.01, 0.01, 0.01, 0.1);

                LineEffect lineEffect = LineEffect
                        .builder(serverWorld, OMMParticles.LASER_PARTICLE, Vec3.atLowerCornerOf(pos).add(0, 0.5, 0))
                        .targetPos(meteorProjectileEntity.position())
                        .forced(Config.USE_FORCED_PARTICLES)
                        .particles((int) (Vec3.atLowerCornerOf(pos).distanceTo(meteorProjectileEntity.position())*3))
                        .build();
                putInCooldown(blockEntity);
                lineEffect.runFor(1, (effect, t) -> {
                    //If the ticks are 19 it means the effect is about to end (1 second = 20 ticks), so revert back the state
                    if(t >= 19){
                        world.setBlock(pos, state.setValue(FIRING, false).setValue(IN_COOLDOWN, true), Block.UPDATE_CLIENTS);
                    }
                });


                //Plays the "pew" laser firing sound
                world.playSound(null, pos, OMMSounds.LASER_FIRE, SoundSource.BLOCKS, 1f, 1.25f);


                if(Config.ANNOUNCE_METEOR_DESTROYED){
                    if(Config.ANNOUNCE_LOCATION){
                        String meteorPos = String.valueOf(meteorProjectileEntity.blockPosition().getX()) + " x, " + String.valueOf(meteorProjectileEntity.blockPosition().getZ()) + " z!";
                        if(meteorProjectileEntity.isHuge()){
                            serverWorld.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_destroyed.huge.localized", meteorPos).withStyle(ChatFormatting.GREEN)), Config.ACTIONBAR_ANNOUNCEMENTS));
                        }else{
                            serverWorld.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_destroyed.localized", meteorPos).withStyle(ChatFormatting.GREEN)), Config.ACTIONBAR_ANNOUNCEMENTS));
                        }
                    }else{
                        if(meteorProjectileEntity.isHuge()){
                            serverWorld.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_destroyed.huge").withStyle(ChatFormatting.GREEN)), Config.ACTIONBAR_ANNOUNCEMENTS));
                        }else{
                            serverWorld.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_destroyed").withStyle(ChatFormatting.GREEN)), Config.ACTIONBAR_ANNOUNCEMENTS));
                        }
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(SHOW_AREA, FIRING, IN_COOLDOWN);
    }

    public VoxelShape makeShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0, 0, 1, 0.625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.4375, 0.625, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.8125, 0, 1, 0.875, 0.4375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.625, 0, 0.0625, 0.8125, 0.0625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.9375, 0.625, 0, 1, 0.8125, 0.0625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.625, 0.9375, 0.0625, 0.8125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.9375, 0.625, 0.9375, 1, 0.8125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.8125, 0.4375, 0.4375, 0.875, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.8125, 0.5625, 1, 0.875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.5625, 0.8125, 0.4375, 1, 0.875, 0.5625), BooleanOp.OR);

        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return makeShape();
    }
}
