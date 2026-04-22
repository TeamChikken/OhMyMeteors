package me.emafire003.dev.ohmymeteors.entities;

import com.google.common.annotations.VisibleForTesting;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.compat.flan.FlanCompat;
import me.emafire003.dev.ohmymeteors.compat.yawp.YawpCompat;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.particles.meteor_flash.FlashScaleParticleOptions;
import me.emafire003.dev.ohmymeteors.particles.meteor_smoke.MeteorSmokeScaledOptions;
import me.emafire003.dev.ohmymeteors.util.ExplosionUtils;
import me.emafire003.dev.ohmymeteors.util.MeteorSizeClass;
import me.emafire003.dev.ohmymeteors.util.MeteorUtils;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.CONFIG;
import static me.emafire003.dev.ohmymeteors.OhMyMeteors.METEOR_STRUCTURES;

/**
 * The projectile entity that gets spawned as a meteor.
 * Upon hitting a block which is not air, it will execute the on-hit actions
 * such as creating an explosion and spawning the structure of blocks of the meteor thing*/
public class MeteorProjectileEntity extends AbstractHurtingProjectile {

    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(MeteorProjectileEntity.class, EntityDataSerializers.INT);
    private static final TicketType<Vec3i> METEOR_CHUCK_TICKET = TicketType.create("meteor", Vec3i::compareTo, 5*20);

    public final AnimationState rotationState = new AnimationState();
    protected int rotationStateTimeout = 0;

    /// Aka a meteor that is a result of the {@link #detonateScatter()} method
    protected boolean isScatterMeteor = false;

    public double accelerationPower = 0.1;

    /// Weather or not the meteor should be announced in chat (used for example in the meteor showers)
    protected boolean isSilenced = false;

    public MeteorProjectileEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level world) {
        super(entityType, world);
        initialize();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIZE, 1);
    }

    @VisibleForTesting
    public void setSize(int size) {
        int i = Mth.clamp(size, 1, 50);
        this.entityData.set(SIZE, i);
        this.reapplyPosition();
        this.refreshDimensions();
    }

    public int getSize() {
        return this.entityData.get(SIZE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Size", this.getSize() - 1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        this.setSize(nbt.getInt("Size") + 1);
        super.readAdditionalSaveData(nbt);
    }
    @Override
    public void refreshDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.refreshDimensions();
        this.setPos(d, e, f);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (SIZE.equals(data)) {
            this.refreshDimensions();
            this.setYRot(this.getYRot());
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }

        super.onSyncedDataUpdated(data);
    }

    @Override
    protected float getInertia() {
        return 1f;
    }

    /**
     * Initializes the meteor with a random size upon creation of the meteor object.
     * Called along with the constructor method
     * */
    public void initialize() {
        RandomSource random = this.level().getRandom();
        int i = random.nextInt(3);
        if (i < 2 && random.nextFloat() < 0.5f) {
            i++;
        }
        int j = 1 << i;
        this.setSize(j);
        calculateTextureChangePositions();

        if(this.level().isClientSide()){
            MeteorUtils.addAliveMeteor(this.getUUID());
        }
        lastPos = this.blockPosition();
    }


    @Override
    public final @NotNull EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getSize());
    }

    @Override
    public void remove(RemovalReason reason) {
        //this.travelledBlocks = 0;
        super.remove(reason);
    }

    /// these things are used to keep track of a chunk load, in order to not send a loading ticket each tick
    private int loadingChuckTicks = 0;
    private ChunkPos currentlyLoadedChunk;

    private int chunksLoaded = 0;

    /**Gets called every tick and makes sure that when the meteor travels through a chunk it is loaded*/
    public void loadChunk(){
        //Safety feature so the meteor despawns if it gets too high, for example using commands and such
        if(this.getY() > level().getHeight()+50){
            this.discard();
        }
        //Every 100 seconds or every time the meteor enters a new chunk, the meteor loads the chunk it's in for 10 seconds or 200 ticks
        if(this.level() instanceof ServerLevel world){
            if(loadingChuckTicks > 0){
                if(currentlyLoadedChunk == null || !currentlyLoadedChunk.equals(this.chunkPosition())){
                    world.getChunkSource().addRegionTicket(METEOR_CHUCK_TICKET,  this.chunkPosition(), 3, this.blockPosition());
                    currentlyLoadedChunk = this.chunkPosition();
                    loadingChuckTicks = 10*20;
                    chunksLoaded++;
                }
                //So it avoids loading chunks forever and slowing down the game
                if(chunksLoaded > CONFIG.meteorBehaviourSection.chunk_loading_limit){
                    OhMyMeteors.LOGGER.warn("Discarded meteor projectile at " + this.position() + " after having loaded too many chunks (" + chunksLoaded + ")");
                    this.discard();
                }
                loadingChuckTicks++;
                return;
            }

            world.getChunkSource().addRegionTicket(METEOR_CHUCK_TICKET,  this.chunkPosition(), 2, this.blockPosition());
            currentlyLoadedChunk = this.chunkPosition();
            loadingChuckTicks = 5*20;
        }
    }


    private Vec3 collisionPos = null;

    @Override
    public void tick() {
        if(CONFIG.meteorBehaviourSection.meteors_load_chunks){
            loadChunk();
        }
        if(blockPosition().getY() > 2000){
            this.discard();
            OhMyMeteors.LOGGER.warn("A meteor somehow reached 2000 blocks of height, it has been discarded");
        }
        if(this.level().isClientSide()){
            setupAnimationStates();
        }
        if(!lastPos.equals(blockPosition())){
            travelledBlocks++;
            lastPos = blockPosition();
        }

        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            //super.tick(); //TODO use a bunch of accessor for the supertick maybe
            if (this.shouldBurn()) {
                this.setSecondsOnFire(1);
            }
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }

            this.checkInsideBlocks();
            Vec3 vec3d = this.getDeltaMovement();
            double d = this.getX() + vec3d.x;
            double e = this.getY() + vec3d.y;
            double f = this.getZ() + vec3d.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float h;
            if (this.isInWater()) {
                for (int i = 0; i < 4; i++) {
                    float g = 0.25F;
                    this.level().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
                }

                h = 0.8f;
            } else {
                h = this.getInertia();
            }
            this.setDeltaMovement(vec3d.add(vec3d.normalize().scale(this.accelerationPower)).scale(h));

            switch (CONFIG.visualsSection.particles_mode){
                case FANCY -> particleAnimation(d, e,f);
                case MINIMAL -> minimalParticleAnimation(d, e, f);
                case LESS -> lessParticleAnimation(d,e,f);
            }

            this.setPos(d, e, f);
        } else {
            this.discard();
        }
    }

    public void setupAnimationStates() {
        if (this.rotationStateTimeout <= 0) {
            this.rotationStateTimeout = 40;
            this.rotationState.start(this.tickCount);
        } else {
            --this.rotationStateTimeout;
        }
    }

    //TODO move these to the render state in 1.21.11+
    public int groundLevel = -1;
    public int distFromGround = -1;
    public int moltenPos = -1;
    public int midPos = -1;
    public int travelledBlocks = 0;
    public BlockPos lastPos;

    protected void calculateTextureChangePositions(){
        Optional<BlockPos> ground;
        ground = BlockPos.MutableBlockPos.findClosestMatch(blockPosition(), 1, level().getHeight(), blockPos -> !level().getBlockState(blockPos).isAir());
        groundLevel = ground.orElse(new BlockPos(0, 64, 0)).getY();
                distFromGround = OhMyMeteors.CONFIG.meteorSpawning.meteor_spawn_height-groundLevel;
        moltenPos = (distFromGround/3)+groundLevel;
        midPos = (distFromGround*2/3)+groundLevel;
    }


    /*public void setupAnimationStates() {
        this.rotationState.startIfStopped(this.tickCount);
    }*/

    Vec3 prevPos = Vec3.ZERO;

    //pal vortex minecraft:flame ~ ~ ~ 1 0.01 0.8 0.1 5 3 10 false 3
    /**
     * Spawns the particle effects behind the meteor*/
    public void particleAnimation(double d, double e, double f){
        if(this.tickCount % 5 == 0){
            prevPos = new Vec3(d,e,f);
        }
        this.level().addParticle(new FlashScaleParticleOptions(this.getSize()), CONFIG.visualsSection.use_forced_particles, d, e + 0.5, f, 0.0, 0.0, 0.0);
        this.level().addParticle(ParticleTypes.EXPLOSION, CONFIG.visualsSection.use_forced_particles, d, e + 0.5, f, 0.0, 0.0, 0.0);

        if(this.level() instanceof ServerLevel world && !this.getDeltaMovement().equals(Vec3.ZERO)){
            world.players().forEach(p -> {
                world.sendParticles(p, ParticleTypes.FLAME, CONFIG.visualsSection.use_forced_particles, d,e,f, 15+this.getSize()*5, 0.02+this.getSize()/100, 0.02+this.getSize()/100, 0.02+this.getSize()/100, 0.1);
                world.sendParticles(p, ParticleTypes.SMOKE, CONFIG.visualsSection.use_forced_particles, d,e,f, 15+this.getSize()*5, 0.02+this.getSize()/100, 0.02+this.getSize()/100, 0.02+this.getSize()/100, 0.1);
                world.sendParticles(p, new MeteorSmokeScaledOptions(3f), CONFIG.visualsSection.use_forced_particles, d,e,f, 10+this.getSize()*2, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.12);
                world.sendParticles(p, new MeteorSmokeScaledOptions((float) getSize()*2/3), CONFIG.visualsSection.use_forced_particles, d,e,f, 1, 0,0,0, 0.12);
                if(this.level().getRandom().nextInt(10) == 5){
                    world.sendParticles(p, ParticleTypes.LAVA, CONFIG.visualsSection.use_forced_particles, d,e,f, 10+this.getSize()*2, 0.2+(double) this.getSize()/100, 0.2+(double) this.getSize()/100, 0.2+(double) this.getSize()/100, 0.12);
                }
                if(!prevPos.equals(Vec3.ZERO)){
                    world.sendParticles(p, ParticleTypes.CAMPFIRE_COSY_SMOKE, CONFIG.visualsSection.use_forced_particles, prevPos.x(), prevPos.y(), prevPos.z(), 1+this.getSize(), 0.02+this.getSize()/100, 0.02+this.getSize()/100, 0.02+this.getSize()/100, 0.1);
                }
            });
        }
    }

    public void lessParticleAnimation(double d, double e, double f){
        this.level().addParticle(new FlashScaleParticleOptions(this.getSize()), CONFIG.visualsSection.use_forced_particles, d, e + 0.5, f, 0.0, 0.0, 0.0);
        this.level().addParticle(ParticleTypes.EXPLOSION, CONFIG.visualsSection.use_forced_particles, d, e + 0.5, f, 0.0, 0.0, 0.0);

        if(this.level() instanceof ServerLevel world && !this.getDeltaMovement().equals(Vec3.ZERO)){
            world.players().forEach(p -> {
                world.sendParticles(p, ParticleTypes.FLAME, CONFIG.visualsSection.use_forced_particles, d,e,f, 5+this.getSize()*2, 0.02+ (double) this.getSize() /100, 0.02+ (double) this.getSize() /100, 0.02+ (double) this.getSize() /100, 0.1);
                world.sendParticles(p, ParticleTypes.SMOKE, CONFIG.visualsSection.use_forced_particles, d,e,f, 5+this.getSize()*2, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.1);
                world.sendParticles(p, new MeteorSmokeScaledOptions(3f), CONFIG.visualsSection.use_forced_particles, d,e,f, (int) (2+this.getSize()*1.5), 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.12);
                world.sendParticles(p, new MeteorSmokeScaledOptions((float) getSize()*2/3), CONFIG.visualsSection.use_forced_particles, d,e,f, 1, 0,0,0, 0.12);
                if(this.level().getRandom().nextInt(10) == 5){
                    world.sendParticles(p, ParticleTypes.LAVA, CONFIG.visualsSection.use_forced_particles, d,e,f, (int) (2+this.getSize()*1.5), 0.2+(double) this.getSize()/100, 0.2+(double) this.getSize()/100, 0.2+(double) this.getSize()/100, 0.12);
                }
            });
        }
    }

    public void minimalParticleAnimation(double d, double e, double f){
        this.level().addParticle(new FlashScaleParticleOptions(this.getSize()), CONFIG.visualsSection.use_forced_particles, d, e + 0.5, f, 0.0, 0.0, 0.0);
        if(this.level() instanceof ServerLevel world && !this.getDeltaMovement().equals(Vec3.ZERO)){
            world.players().forEach(p -> {
                world.sendParticles(p, new MeteorSmokeScaledOptions((float) getSize()*2/3), CONFIG.visualsSection.use_forced_particles, d,e,f, 1, 0,0,0, 0.12);
            });
        }
    }

    /** The sphere explosion is a little weaker than the vanilla one, so adjustment may be needed to have a niceer effect*/
    protected int sphereExplosionAdjuster(){
        int adjust = 0;
        if(this.getSize() > 3){
            adjust = 2;
        }
        if(this.getSize() > 10){
            adjust = 3;
        }
        return adjust;
    }

    /** Makes this entity explode without creating any structures on impact
     * and then discards this entity*/
    public void detonateSimple(){
        detonateSimple(0);
    }


    public void detonateSimple(int extraPower){
        /// Excludes all blocks from destruction
        ExplosionDamageCalculator safeExplosion = new ExplosionDamageCalculator() {
            @Override
            public @NotNull Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                return Optional.of(Blocks.BEDROCK.getExplosionResistance());
            }
        };

        /// Excludes blocks tagged with "meteor_explosion_safe" by default
        ExplosionDamageCalculator explosionBehavior = new ExplosionDamageCalculator() {
            @Override
            public @NotNull Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                if(blockState.is(OhMyMeteors.METEOR_EXPLOSION_SAFE)){
                    return Optional.of(Blocks.BEDROCK.getExplosionResistance());
                }
                return new ExplosionDamageCalculator().getBlockExplosionResistance(explosion, world, pos, blockState, fluidState);
            }
        };


        if(isScatterMeteor()){
            if(!CONFIG.meteorBehaviourSection.scatter_meteor_griefing){
                explosionBehavior = safeExplosion;
            }

            announceSpawn();
        }

        if(!CONFIG.meteorBehaviourSection.meteor_griefing){
            explosionBehavior = safeExplosion;
        }


        if(CONFIG.meteorBehaviourSection.use_better_explosions){
            ExplosionUtils.createExplosion(this.level(), this, this.damageSources().explosion(this, this), explosionBehavior, this.position(), (this.getSize()+CONFIG.meteorBehaviourSection.explosion_power_modifier+sphereExplosionAdjuster()+extraPower)*CONFIG.meteorBehaviourSection.explosion_power_multiplier, CONFIG.meteorBehaviourSection.spawn_fire_with_meteor, Level.ExplosionInteraction.TNT);
        }else{
            this.level().explode(this, this.damageSources().explosion(this, this), explosionBehavior, this.position(), (this.getSize()+CONFIG.meteorBehaviourSection.explosion_power_modifier)*CONFIG.meteorBehaviourSection.explosion_power_multiplier, CONFIG.meteorBehaviourSection.spawn_fire_with_meteor, Level.ExplosionInteraction.TNT);
        }

        if(!this.level().isClientSide()){
            ((ServerLevel)this.level()).players().forEach(serverPlayerEntity -> {
                //If it should play a sound for every player, do it, unless the player is close enough to the original one
                if(CONFIG.notificationSection.global_explosion_sound && (serverPlayerEntity.position().distanceTo(this.position()) > 60)){
                    serverPlayerEntity.playNotifySound(SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5f, 0.8f);
                }else if(CONFIG.notificationSection.area_explosion_sound){
                    double dist = serverPlayerEntity.position().distanceTo(this.position());
                    if(dist < CONFIG.notificationSection.area_explosion_sound_radius && dist > 60){
                        serverPlayerEntity.playNotifySound(SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5f, 0.8f);
                    }
                }
                ((ServerLevel)this.level()).sendParticles(serverPlayerEntity, ParticleTypes.EXPLOSION_EMITTER, true, this.getX(), this.getY(), this.getZ(), 1, 0.1, 0.1, 0.1, 0.1);
            });
        }

        announceSpawn();
        //entity.getWorld().addParticle(ParticleTypes.FLASH, pos.getX(), pos.getY(), pos.getZ(), 0,0,0);
        this.discard();
    }

    public void announceSpawn() {
        if(CONFIG.notificationSection.announce_meteor_spawn && !this.isSilenced()){
            if(CONFIG.notificationSection.announce_location){
                String meteorPos = this.blockPosition().getX() + " x, " + this.blockPosition().getZ() + " z!";
                this.level().players().forEach(player -> player.displayClientMessage(Component.literal(OhMyMeteors.PREFIX).append(Component.translatable("message.ohmymeteors.meteor_impacted.localized", meteorPos).withStyle(ChatFormatting.RED)), CONFIG.notificationSection.actionbar_announcements));
            }
        }
    }


    /** Like {@link #detonateSimple()} but will also spawn the structure of the meteor*/
    public void detonateWithStructure(){
        detonateSimple();
        if(!this.level().isClientSide()){
            StructurePlacerAPI placer = getPlacer();
            //it means the meteor was too small for the structure, placing a single block instead
            if(placer == null){
                int r = this.level().getRandom().nextIntBetweenInclusive(1,3);
                if(r == 1){
                    this.level().setBlockAndUpdate(BlockPos.containing(this.position()), OMMBlocks.METEORIC_ROCK.get().defaultBlockState());
                }else if(r == 2){
                    this.level().setBlockAndUpdate(BlockPos.containing(this.position()), Blocks.SMOOTH_BASALT.defaultBlockState());
                }else{
                    this.level().setBlockAndUpdate(BlockPos.containing(this.position()), Blocks.BLACKSTONE.defaultBlockState());
                }
                return;
            }
            placer.setPreventReplacementOfTaggedBlocks(true, OhMyMeteors.METEOR_EXPLOSION_SAFE);
            placer.loadStructure();
        }
    }

    /** Like {@link #detonateWithStructure()} but will only replace air blocks*/
    public void detonateWithStructureOnlyAir(){
        detonateSimple();
        if(!this.level().isClientSide()){
            StructurePlacerAPI placer = getPlacer();
            //it means the meteor was too small for the structure, a single block has been placed instead
            if(placer == null){
                if(level().getBlockState(this.blockPosition()).isAir()){
                    int r = this.level().getRandom().nextIntBetweenInclusive(1,3);
                    if(r == 1){
                        this.level().setBlockAndUpdate(BlockPos.containing(this.position()), OMMBlocks.METEORIC_ROCK.get().defaultBlockState());
                    }else if(r == 2){
                        this.level().setBlockAndUpdate(BlockPos.containing(this.position()), Blocks.SMOOTH_BASALT.defaultBlockState());
                    }else{
                        this.level().setBlockAndUpdate(BlockPos.containing(this.position()), Blocks.BLACKSTONE.defaultBlockState());
                    }
                }
                return;
            }
            placer.setOnlyReplaceTaggedBlocks(true, OhMyMeteors.AIR_BLOCKS);
            placer.loadStructure();
        }
    }

    /**If the meteor is micro size (less than 2) will just spawn a block and return null*/
    public StructurePlacerAPI getPlacer(MeteorSizeClass sizeClass, String filter){
        //If the dimension is even lower than 2, just spawn one block
        if(this.getSize() < 2){
            return null;
        }
        BlockPos m_pos_offset = BlockPos.containing(this.getDeltaMovement()).offset(-1, 0, -1);//new BlockPos(-1, -2, -1);
        //Checks for at most 5 blocks of Air below where the meteor should spawn, which could be a result of the explosion

        StructurePlacerAPI placer =
                new StructurePlacerAPI((WorldGenLevel) this.level(), OhMyMeteors.getIdentifier("small/small_meteor_0"), this.blockPosition(), Mirror.NONE, Rotation.NONE, false, 1f, m_pos_offset);

        switch (sizeClass){
            case SMALL -> {
                ResourceLocation tobeplaced = getStructureToPlace(MeteorSizeClass.SMALL, filter);
                m_pos_offset = getOffset(MeteorSizeClass.SMALL, tobeplaced);


                placer = new StructurePlacerAPI((WorldGenLevel) this.level(),
                        tobeplaced,
                        this.blockPosition(), Mirror.NONE, Rotation.NONE, false, 1f, m_pos_offset);

                if(CONFIG.meteorBehaviourSection.only_replace_air){
                    placer.setOnlyReplaceTaggedBlocks(true, BlockTags.AIR);
                }

                return placer;
            }
            case MEDIUM -> {
                ResourceLocation tobeplaced = getStructureToPlace(MeteorSizeClass.MEDIUM, filter);
                m_pos_offset = getOffset(MeteorSizeClass.MEDIUM, tobeplaced);


                placer = new StructurePlacerAPI((WorldGenLevel) this.level(),
                        tobeplaced,
                        this.blockPosition(), Mirror.NONE, Rotation.NONE, false, 1f, m_pos_offset);
            }
            case BIG -> {
                ResourceLocation tobeplaced = getStructureToPlace(MeteorSizeClass.BIG, filter);
                m_pos_offset = getOffset(MeteorSizeClass.BIG, tobeplaced);

                //m_pos_offset = new BlockPos(-4, -6, -3);

                placer = new StructurePlacerAPI((WorldGenLevel) this.level(),
                        tobeplaced,
                        this.blockPosition(), Mirror.NONE, Rotation.NONE, false, 1f, m_pos_offset);

            } //If it's not in the sizes above, then it's a huge one:
            default -> {
                ResourceLocation tobeplaced = getStructureToPlace(MeteorSizeClass.HUGE, filter);
                m_pos_offset = getOffset(MeteorSizeClass.HUGE, tobeplaced);
                //m_pos_offset = new BlockPos(-4, -10, -3);

                placer = new StructurePlacerAPI((WorldGenLevel) this.level(),
                        tobeplaced,
                        this.blockPosition(), Mirror.NONE, Rotation.NONE, false, 1f, m_pos_offset);

            }
        }
        if(Config.ONLY_REPLACE_AIR){
            placer.setOnlyReplaceTaggedBlocks(true, OhMyMeteors.AIR_BLOCKS);
        }
        return placer;
    }

    public StructurePlacerAPI getPlacer(MeteorSizeClass sizeClass){
        return getPlacer(sizeClass, "");
    }

    public StructurePlacerAPI getPlacer(){
        //If the dimension is even lower than 2, just spawn one block
        if(this.getSize() < 2){
            return null;
        }

        if(this.getSize() <= CONFIG.meteorSpawning.max_small_meteor_size){
            return getPlacer(MeteorSizeClass.SMALL);
        }

        if(this.getSize() <= CONFIG.meteorSpawning.max_medium_meteor_size){
            return getPlacer(MeteorSizeClass.MEDIUM);
        }

        if(this.getSize() <= CONFIG.meteorSpawning.max_big_meteor_size){
            return getPlacer(MeteorSizeClass.BIG);
        }

        //If it's not in the sizes above, then it's a huge one:
        return getPlacer(MeteorSizeClass.HUGE);
    }



    /** Returns the offset of the meteor structure, aka how much it's going to be embedded in the terrain.
     * it's based on its size and the distance from the terrain that would be left from the imapct point, and
     * the direction of the meteor
     *
     * @param sizeClass The staring sizeClass to determine the starting offset
     * @param tobeplaced the id of the meteor that is going to be placed
     * @return the blockpos offset
     */
    protected BlockPos getOffset(MeteorSizeClass sizeClass, ResourceLocation tobeplaced){
        BlockPos offset;
        Optional<StructureTemplate> template = StructurePlacerAPI.getTemplatePreview((ServerLevel) this.level(), tobeplaced);
        if(template.isEmpty() || tobeplaced.getPath().startsWith("error")){
            return new BlockPos(0, 5, 0);
        }
        Vec3 size_factors = Vec3.atLowerCornerOf(template.get().getSize());
        BlockPos nonair_pos = BlockPos.containing(this.position()).offset(0, -(int) size_factors.y()/3, 0);
        switch (sizeClass){
            case SMALL -> offset = new BlockPos(-1, 0, -1);
            case MEDIUM -> {
                offset = new BlockPos(-2, +1, -2);
                if(this.getXRot() < 27){
                    nonair_pos.offset((int) (this.getDeltaMovement().x()*2), 0, (int) (this.getDeltaMovement().z()*2));
                }
            }case BIG -> {
                offset = new BlockPos(-3, 0, -3);

                nonair_pos = BlockPos.containing(this.position()).offset(0, -(int) size_factors.y()/10, 0);
                if(this.getXRot() < 27){
                    nonair_pos.offset((int) (this.getDeltaMovement().x()*5), 0, (int) (this.getDeltaMovement().z()*5));
                }
            }default -> {
                offset = new BlockPos(-4, 0, -4);
                nonair_pos = BlockPos.containing(this.position()).offset(0, -(int) size_factors.y()/37, 0);
            }
        }

        BlockState state = this.level().getBlockState(nonair_pos);
        int dist_to_floor = 0;
        while(state.isAir() || state.is(Blocks.FIRE)){
            nonair_pos = nonair_pos.below();
            state = this.level().getBlockState(nonair_pos);
            dist_to_floor++;
        }

        offset = BlockPos.containing(this.getDeltaMovement()).offset(offset);
        offset = offset.offset(0, - dist_to_floor, 0);
        return offset;
    }

    /** Returns the offset of the meteor structure, aka how much it's going to be embedded in the terrain.
     * it's based on its size and the distance from the terrain that would be left from the imapct point, and
     * the direction of the meteor
     *
     * @param m_pos_offset The staring offset
     * @param tobeplaced the id of the meteor that is going to be placed
     * @return the blockpos offset
     */
    /*protected BlockPos getOffset(BlockPos m_pos_offset, ResourceLocation tobeplaced){
        //If it's an error structure it should be as visible as possible
        if(tobeplaced.getPath().startsWith("error")){
            return m_pos_offset.offset(0, 5, 0);
        }

        Vec3 size_factors = Vec3.atLowerCornerOf(StructurePlacerAPI.getTemplatePreview((ServerLevel) this.level(), tobeplaced).get().getSize());
        //size_factors = size_factors.multiply(0.2, -0.2, 0.2);
        //size_factors = new Vec3d(size_factors.getZ(), size_factors.getY(), size_factors.getX());

        m_pos_offset = BlockPos.containing(this.getDeltaMovement()).offset(m_pos_offset);

        /*if(false && this.getPitch() < 25 && !(tobeplaced.getPath().startsWith("big") || tobeplaced.getPath().startsWith("huge"))){
            OhMyMeteors.LOGGER.error("Meteor fell diagonally, embedding laterally, the pitch is: " + this.getPitch());
            m_pos_offset = m_pos_offset.add(((int) this.getVelocity().getX()*2), 0,  ((int) this.getVelocity().getZ()*2));
        }else{

        }*/
/*
        //This is the one used for "small" meteors
        BlockPos nonair_pos = BlockPos.containing(this.position()).offset(0, -(int) size_factors.y()/3, 0);
        if(tobeplaced.getPath().startsWith("big")){
            nonair_pos = BlockPos.containing(this.position()).offset(0, -(int) size_factors.y()/10, 0);
            if(this.getXRot() < 27){
                nonair_pos.offset((int) (this.getDeltaMovement().x()*5), 0, (int) (this.getDeltaMovement().z()*5));
            }
        }

        if(tobeplaced.getPath().startsWith("medium")){
            if(this.getXRot() < 27){
                nonair_pos.offset((int) (this.getDeltaMovement().x()*2), 0, (int) (this.getDeltaMovement().z()*2));
            }
        }
        if(tobeplaced.getPath().startsWith("huge")){
            nonair_pos = BlockPos.containing(this.position()).offset(0, -(int) size_factors.y()/37, 0);
        }
        BlockState state = this.level().getBlockState(nonair_pos);
        int dist_to_floor = 0;
        while(state.isAir() || state.is(Blocks.FIRE)){
            nonair_pos = nonair_pos.below();
            state = this.level().getBlockState(nonair_pos);
            dist_to_floor++;
        }

        //get the distance to floor, get the height and stuff and the place half of it underground?

        +(size_factors.getY()/2)
        m_pos_offset = m_pos_offset.offset(0, - dist_to_floor, 0);
        return m_pos_offset;
    }*/

    /**
     * Returns the ID of the structure that is going to be spawned based the size class
     *
     * @param sizeClass The size of the meteors that we want to spawn, can be "small" "medium" "big" "huge"
     * */
    public ResourceLocation getStructureToPlace(MeteorSizeClass sizeClass, String filter){
        AtomicBoolean hasSpecial = new AtomicBoolean(false);

        if(METEOR_STRUCTURES.isEmpty() && !this.level().isClientSide()){
            OhMyMeteors.reInitStructures((ServerLevel) this.level());
        }

        //In case there was a problem and the only meteor spawnable is that one
        if(METEOR_STRUCTURES.size() == 1 && METEOR_STRUCTURES.get(0).getPath().equals("error")){
            return METEOR_STRUCTURES.get(0);
        }

        List<ResourceLocation> structs = METEOR_STRUCTURES.stream().filter(identifier -> {

            if(!identifier.getPath().startsWith(sizeClass.getSerializedName())){
                return false;
            }

            //also checks to see that it has the filter, if the filter is enabled
            if((filter != null && !filter.isEmpty()) && !identifier.getPath().startsWith(sizeClass.getSerializedName()+"/"+filter)){
                return false;
            }

            if (!hasSpecial.get()) { //saves on checks
                //This allows me to see if this size has at least a special meteor
                if (identifier.getPath().startsWith(sizeClass.getSerializedName()+"/special")) {
                    hasSpecial.set(true);
                    return true;
                }
            }

            return true;
        }).toList();

        if (structs.isEmpty()){
            OhMyMeteors.LOGGER.error("The list of structures for size class '" + sizeClass.getSerializedName() + "' is empty! Check that your structures are valid ones!");
            structs = List.of(OhMyMeteors.getIdentifier("error"));
        }

        ResourceLocation structure_id = structs.get(this.level().getRandom().nextIntBetweenInclusive(0,structs.size()-1));
        //This is to prevent special structures from spawning "before" they should
        //TODO make sure it's not too much of a performance issue
        while(structure_id.getPath().startsWith(sizeClass.getSerializedName()+"/special")){
            structure_id = structs.get(this.level().getRandom().nextIntBetweenInclusive(0,structs.size()-1));
        }

        //If there is at least a special meteor structure, and the chance is hit, the structs list should only have those
        if(hasSpecial.get()){
            int i = random.nextIntBetweenInclusive(0, CONFIG.meteorSpawning.special_meteors_chance);
            if(i == 1){
                List<ResourceLocation> specials = structs.stream().filter(id -> id.getPath().startsWith(sizeClass.getSerializedName()+"/special")).toList();
                structure_id = specials.get(this.level().getRandom().nextIntBetweenInclusive(0,specials.size()-1));
            }
        }
        return structure_id;
    }

    public ResourceLocation getStructureToPlace(MeteorSizeClass sizeClass){
        return getStructureToPlace(sizeClass, "");
    }

    /**This will detonate the meteor with an explosion like {@link #detonateSimple()}
     * but will also spawn other meteors based on the size of this meteor.
     * <p>
     * Meteors will be smaller and be oriented randomly from that point on, but will still go down.
     * */
    public void detonateScatter(){
        if(this.level().isClientSide() || this.getSize() <= 1){
            return;
        }

        //Can generate a minimum of 1 new meteor up to a number equal half of the size of this meteor
        int scatter_into = this.level().getRandom().nextIntBetweenInclusive(1, Math.max(this.getSize()/2, 1));
        //this is used to determine the size of the new meteors, which will be smaller than the original
        //each new meteor is going to take up some of the "mass" of the parent one, leaving the rest for the next one
        //and so on.
        int remainingSize = this.getSize()/2+1;

        List<MeteorProjectileEntity> newMeteors = new ArrayList<>();
        for(int i = 0; i<scatter_into; i++){
            //Gets a random number between 1 and the remaining size, making sure to leave at least one size for each new meteor yet to generate)
            int size =  this.level().getRandom().nextIntBetweenInclusive(1, Math.max(remainingSize-(scatter_into-i), 1));
            MeteorProjectileEntity m = MeteorUtils.getDownwardsMeteor(this.position(), (ServerLevel) this.level(), 1, 10+this.getSize() /2, this.position().y(), size, size, false);
            m.setScatterMeteor(true);
            newMeteors.add(m);
        }

        this.detonateSimple();

        newMeteors.forEach( meteorProjectileEntity -> this.level().addFreshEntity(meteorProjectileEntity));

    }

    /// Since it likes to explode more times instead of just one, i'll put this here so it won't explode twice
    protected boolean exploded = false;
    protected int travelledBlocksAfterHit = 0;
    protected Vec3 explosionPos = null;

    /// This is the main method which does the meteor stuff on impact
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        if(exploded){
            return;
        }
        super.onHitBlock(blockHitResult);
        BlockState state = this.level().getBlockState(blockHitResult.getBlockPos());

        //It also registers Air blocks as a collision so we need to avoid such cases
        if(!state.isAir()){

            if(!level().isClientSide() && !MeteorUtils.canSpawnInModdedRegion((ServerLevel) level(), blockPosition())){
                this.discard();
                return;
            }

            //Checks if the block should be bypassed or not
            if(state.is(OhMyMeteors.METEOR_BYPASSES)){
                //Early return so the rest of the code doesn't run if the meteor hits a leaves block and the config option is there
                if(state.is(OhMyMeteors.METEOR_BYPASSES_AND_DESTROY)){
                    AABB box = this.getBoundingBox();
                    BlockPos.betweenClosedStream(box).forEach((blockPos -> {
                        if(this.level().getBlockState(blockPos).is(OhMyMeteors.METEOR_BYPASSES_AND_DESTROY)){
                            this.level().setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                            this.level().addParticle(ParticleTypes.EXPLOSION, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, 0,0 );
                        }
                    }));
                }
                return;
            }
            collisionPos = Vec3.atCenterOf(blockPosition());
            //this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 10, World.ExplosionSourceType.NONE);

            //this section makes the meteor entity appear as if it's going into the terrain, which is nicer instead of exploding as soon as the hitbox gets a block especially at high dimensions
            if(explosionPos == null){
                explosionPos = this.position();
            }
            travelledBlocksAfterHit++;
            if(this.getSize()/2 > travelledBlocksAfterHit){
                return;
            }

            this.setPosRaw(explosionPos.x, explosionPos.y, explosionPos.z);

            explodeMeteor();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if(exploded || !CONFIG.meteorBehaviourSection.explode_on_entity_collision){
            return;
        }
        BlockPos collisionPos = entityHitResult.getEntity().getOnPos();
        if(!level().isClientSide() && MeteorUtils.canSpawnInModdedRegion((ServerLevel) level(), collisionPos)){
            this.discard();
            return;
        }
        super.onHitEntity(entityHitResult);
        explodeMeteor();
    }

    //TODO make configurable?
    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) || target instanceof Projectile;
    }

    /**Actually makes the meteor explode and disappear, and spawn stuff if it needs to. It's used when hitting a block or an entity*/
    public void explodeMeteor(){
        this.discard(); //So it doesn't trigger again hitting the next block
        exploded = true;
        if(!this.level().isClientSide()){
            if(this.isScatterMeteor()){
                if(CONFIG.meteorBehaviourSection.scatter_meteor_structure){
                    if(CONFIG.meteorBehaviourSection.scatter_only_replace_air){
                        this.detonateWithStructureOnlyAir();
                    }else{
                        this.detonateWithStructure();
                    }
                    return;
                }else{
                    this.detonateSimple();
                    return;
                }
            }

            if(CONFIG.meteorBehaviourSection.meteor_structure){
                if(CONFIG.meteorBehaviourSection.only_replace_air){
                    this.detonateWithStructureOnlyAir();
                }else{
                    this.detonateWithStructure();
                }

            }else{
                this.detonateSimple();
                this.discard();
            }
        }
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        MeteorUtils.removeAliveMeteor(this.getUUID());
    }
/*
    @Override
    public void remove(RemovalReason removalReason) {
        if(this.level().isClientSide()){
            OhMyMeteors.LOGGER.error("Removing meteor");
            OhMyMeteors.LOGGER.error("Ok client side remove");
            MeteorUtils.addAliveMeteor(this.getUUID());
        }
        super.remove(removalReason);
    }
*/
    /**Returns true if this meteor is classified as huge, as in bigger than the biggest "big" size*/
    public boolean isHuge(){
        return this.getSize() > CONFIG.meteorSpawning.max_big_meteor_size;
    }


    public boolean isScatterMeteor() {
        return isScatterMeteor;
    }

    public void setScatterMeteor(boolean scatterMeteor) {
        isScatterMeteor = scatterMeteor;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    public boolean isSilenced() {
        return isSilenced;
    }

    public void setSilenced(boolean silenced) {
        isSilenced = silenced;
    }

    /** This method can be used by other mods to add their own custom meteors and spawn their version of the meteors*/
    @SuppressWarnings("unused")
    public MeteorProjectileEntity copy(MeteorProjectileEntity newMeteor){
        newMeteor.setPos(this.position());
        newMeteor.setDeltaMovement(this.getDeltaMovement());
        newMeteor.setSize(this.getSize());
        newMeteor.setScatterMeteor(this.isScatterMeteor());
        newMeteor.setSilenced(this.isSilenced());
        return newMeteor;
    }

    /** Returns the size class of this meteor, based on the values of the config file*/
    @SuppressWarnings("unused")
    public MeteorSizeClass getSizeClass(){
        if(this.getSize() <= CONFIG.meteorSpawning.max_small_meteor_size){
            return MeteorSizeClass.SMALL;
        }
        if(this.getSize() <= CONFIG.meteorSpawning.max_medium_meteor_size){
            return MeteorSizeClass.MEDIUM;
        }
        if(this.getSize() <= CONFIG.meteorSpawning.max_big_meteor_size){
            return MeteorSizeClass.BIG;
        }
        return MeteorSizeClass.HUGE;
    }
}