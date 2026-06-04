package me.emafire003.dev.ohmymeteors.blocks.advanced_laser;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.blocks.basic_laser.BasicMeteorLaserBlock;
import me.emafire003.dev.ohmymeteors.entities.MeteorProjectileEntity;
import me.emafire003.dev.ohmymeteors.particles.OMMParticles;
import me.emafire003.dev.ohmymeteors.sounds.OMMSounds;
import me.emafire003.dev.particleanimationlib.effects.CuboidEffect;
import me.emafire003.dev.particleanimationlib.effects.LineEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.CONFIG;

//Ah remeber that the whole chunk is loaded when a meteor enters it so this will be loaded as well no need for fancy stuff
public class AdvancedMeteorLaserBlock extends BasicMeteorLaserBlock {

/*    ///Is able to detect and destroy meteors this many blocks up from its position
    protected static final int Y_LEVEL_AREA_COVERAGE = 64;
    /// The radius in blocks that this type of laser can cover aka how fare on the xz plane it can detect and shoot meteors
    protected static final int RADIUS_AREA_COVERAGE = 80; //Which is around 5x5 chunks
*/
    /// Only awakens when a meteor is spawned somewhere in the world, to save up on checks
    private static boolean AWAKE = false;
    /// Used to determine for how long it should stay actively searching
    private static int tickCounterAwakening = -1;
    private static final int AWAKE_TIME_LIMIT = 20*25; //Should remain awake for 25 seconds after a meteor has spawned in

    public AdvancedMeteorLaserBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(SHOW_AREA, false).setValue(IN_COOLDOWN, false).setValue(FIRING, false));
    }

    /*@Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(AdvancedMeteorLaserBlock::new);
    }*/

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedMeteorLaserBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return !world.isClientSide && world.dimensionType().hasSkyLight() ? createTickerHelper(type, OMMBlocks.ADVANCED_METEOR_LASER_BLOCK_ENTITY, AdvancedMeteorLaserBlock::tick) : null;
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

    protected static int getYLevelAreaCoverage(){
        return CONFIG.lasersSection.advanced_laser_height;
    }

    protected static int getRadiusAreaCoverage(){
        return CONFIG.lasersSection.advanced_laser_area_radius;
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
    private static void tick(Level world, BlockPos pos, BlockState state, AdvancedMeteorLaserBlockEntity blockEntity) {
        if(world instanceof ServerLevel serverWorld && world.canSeeSky(pos.above())){
            if(CONFIG.lasersSection.should_advanced_laser_cooldown && BLOCKS_IN_COOLDOWN.containsKey(blockEntity)){
                //The cooldown is ended, keep on with the rest
                if(BLOCKS_IN_COOLDOWN.get(blockEntity) > CONFIG.lasersSection.advanced_laser_cooldown*20){
                    removeCooldown(blockEntity, state, world, pos);

                }else{//Increases the cooldown timer
                    BLOCKS_IN_COOLDOWN.put(blockEntity, BLOCKS_IN_COOLDOWN.getOrDefault(blockEntity, 0)+1);
                    return;
                }
            }
            
            if(!state.getValue(SHOW_AREA) && !AWAKE){
                return;
            }

            AABB box = new AABB(new BlockPos(pos.getX(), Math.min(pos.getY()+getYLevelAreaCoverage(), CONFIG.meteorSpawning.meteor_spawn_height), pos.getZ())).inflate(getRadiusAreaCoverage(), 1, getRadiusAreaCoverage());
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
                line.setForced(CONFIG.visualsSection.use_forced_particles);
                line.run();

                //The horizontal lines at the top which point to the corner of the box
                line.setForced(false);
                lowerPos = new Vec3(box.maxX, box.maxY, box.maxZ);
                line.setTargetPos(lowerPos);
                line.setOriginPos(box.getCenter());
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

                meteorProjectileEntity.detonateSimple();

                serverWorld.sendParticles(OMMParticles.LASER_FLASH_PARTICLE, pos.above().above().getX(), pos.above().above().getY(), pos.above().above().getZ(), 2, 0.01, 0.01, 0.01, 0.1);

                //BUBBLE_POP could also work?
                LineEffect lineEffect = LineEffect
                        .builder(serverWorld, OMMParticles.LASER_PARTICLE, Vec3.atLowerCornerOf(pos.above()))
                        .targetPos(meteorProjectileEntity.position())
                        .particles((int) (Vec3.atLowerCornerOf(pos).distanceTo(meteorProjectileEntity.position())*2))
                        .forced(CONFIG.visualsSection.use_forced_particles)
                        .build();

                lineEffect.setParticle(OMMParticles.LASER_PARTICLE_SMALL);
                lineEffect.setOriginPos(Vec3.atLowerCornerOf(pos.above()).add(0.5, -0.5, 0));
                lineEffect.setParticles((int) (Vec3.atLowerCornerOf(pos.above()).add(0.5, -0.5, 0).distanceTo(meteorProjectileEntity.position())*2));
                lineEffect.runFor(1);

                lineEffect.setOriginPos(Vec3.atLowerCornerOf(pos.above()).add(-0.5, -0.5, 0));
                lineEffect.setParticles((int) (Vec3.atLowerCornerOf(pos.above()).add(-0.5, -0.5, 0).distanceTo(meteorProjectileEntity.position())*2));
                lineEffect.runFor(1);

                lineEffect.setOriginPos(Vec3.atLowerCornerOf(pos.above()).add(0, -0.5, 0.5));
                lineEffect.setParticles((int) (Vec3.atLowerCornerOf(pos.above()).add(0, -0.5, 0.5).distanceTo(meteorProjectileEntity.position())*2));
                lineEffect.runFor(1);

                lineEffect.setOriginPos(Vec3.atLowerCornerOf(pos.above()).add(0, -0.5, -0.5));
                lineEffect.setParticles((int) (Vec3.atLowerCornerOf(pos.above()).add(0, -0.5, -0.5).distanceTo(meteorProjectileEntity.position())*2));
                lineEffect.runFor(1);

                lineEffect.setParticle(OMMParticles.LASER_PARTICLE);
                lineEffect.setOriginPos(Vec3.atLowerCornerOf(pos.above()));
                lineEffect.setTargetPos(meteorProjectileEntity.position());
                lineEffect.setParticles((int) (Vec3.atLowerCornerOf(pos).distanceTo(meteorProjectileEntity.position())*2));
                putInCooldown(blockEntity);
                lineEffect.runFor(1, (effect, t) -> {
                    //If the ticks are 19/20 it means the effect is about to end (1 second = 20 ticks), so revert back the state
                    if(t >= 19){
                        world.setBlock(pos, state.setValue(FIRING, false).setValue(IN_COOLDOWN, true), Block.UPDATE_CLIENTS);

                    }
                });

                //Plays the "pew" laser firing sound
                world.playSound(null, pos, OMMSounds.LASER_FIRE, SoundSource.BLOCKS, 1f, 1.4f);


                if(CONFIG.notificationSection.announce_meteor_destroyed){
                    if(CONFIG.notificationSection.announce_location){
                        String meteorPos = meteorProjectileEntity.blockPosition().getX() + " x, " + String.valueOf(meteorProjectileEntity.blockPosition().getZ()) + " z!";
                        if(meteorProjectileEntity.isHuge()){
                            serverWorld.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_destroyed.huge.localized", meteorPos).withStyle(ChatFormatting.GREEN)), CONFIG.notificationSection.actionbar_announcements));
                        }else{
                            serverWorld.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_destroyed.localized", meteorPos).withStyle(ChatFormatting.GREEN)), CONFIG.notificationSection.actionbar_announcements));
                        }
                    }else{
                        if(meteorProjectileEntity.isHuge()){
                            serverWorld.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_destroyed.huge").withStyle(ChatFormatting.GREEN)), CONFIG.notificationSection.actionbar_announcements));
                        }else{
                            serverWorld.players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_destroyed").withStyle(ChatFormatting.GREEN)), CONFIG.notificationSection.actionbar_announcements));
                        }
                    }

                }

            });

            tickCounterAwakening++;
        }

    }

}
