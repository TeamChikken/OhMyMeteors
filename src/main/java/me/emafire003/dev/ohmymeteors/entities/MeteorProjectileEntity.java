package me.emafire003.dev.ohmymeteors.entities;

import com.google.common.annotations.VisibleForTesting;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.events.MeteorSpawnEvent;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.particleanimationlib.effects.AnimatedCircleEffect;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * The projectile entity that gets spawned as a meteor.
 * Upon hitting a block which is not air, it will execute the on-hit actions
 * such as creating an explosion and spawning the structure of blocks of the meteor thing*/
public class MeteorProjectileEntity extends ExplosiveProjectileEntity {
    private static final TrackedData<Integer> SIZE = DataTracker.registerData(MeteorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    //TODO needs proper testing
    private static final ChunkTicketType<Vec3i> METEOR_CHUCK_TICKET = ChunkTicketType.create("meteor", Vec3i::compareTo, 5*20);

    /// Aka a meteor that is a result of the {@link #detonateScatter()} method
    protected boolean isScatterMeteor = false;

    public double accelerationPower = 0.1;


    public MeteorProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(OMMEntities.METEOR_PROJECTILE_ENTITY, world);
        initialize();
    }

    public MeteorProjectileEntity(World world) {
        super(OMMEntities.METEOR_PROJECTILE_ENTITY, world);
        initialize();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SIZE, 1);
    }


    @VisibleForTesting
    public void setSize(int size) {
        int i = MathHelper.clamp(size, 1, 50);
        this.dataTracker.set(SIZE, i);
        this.refreshPosition();
        this.calculateDimensions();
    }

    public int getSize() {
        return this.dataTracker.get(SIZE);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Size", this.getSize() - 1);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.setSize(nbt.getInt("Size") + 1);
        super.readCustomDataFromNbt(nbt);
    }
    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.setPosition(d, e, f);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SIZE.equals(data)) {
            this.calculateDimensions();
            this.setYaw(this.getYaw());
            if (this.isTouchingWater() && this.random.nextInt(20) == 0) {
                this.onSwimmingStart();
            }
        }

        super.onTrackedDataSet(data);
    }

    @Override
    protected float getDrag() {
        return 1f;
    }

    /**
     * Initializes the meteor with a random size upon creation of the meteor object.
     * Called along with the constructor method
     * */
    public void initialize() {
        Random random = this.getWorld().getRandom();
        int i = random.nextInt(3);
        if (i < 2 && random.nextFloat() < 0.5f) {
            i++;
        }
        int j = 1 << i;
        this.setSize(j);
        MeteorSpawnEvent.EVENT.invoker().meteorSpawned(this);
    }


    @Override
    public final EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(this.getSize());
    }

    /// these things are used to keep track of a chunk load, in order to not send a loading ticket each tick
    private int loadingChuckTicks = 0;
    private ChunkPos currentlyLoadedChunk;

    /**Gets called every tick and makes sure that when the meteor travels through a chunk it is loaded*/
    public void loadChunk(){
        //Every 100 seconds or every time the meteor enters a new chuck, the meteor loads the chunk it's in for 5 seconds or 100 ticks
        if(this.getWorld() instanceof ServerWorld world){
            if(loadingChuckTicks > 0){
                if(currentlyLoadedChunk == null || !currentlyLoadedChunk.equals(this.getChunkPos())){
                    world.getChunkManager().addTicket(METEOR_CHUCK_TICKET,  this.getChunkPos(), 2, this.getBlockPos());
                    currentlyLoadedChunk = this.getChunkPos();
                    loadingChuckTicks = 5*20;
                }
                loadingChuckTicks++;
                return;
            }

            world.getChunkManager().addTicket(METEOR_CHUCK_TICKET,  this.getChunkPos(), 2, this.getBlockPos());
            currentlyLoadedChunk = this.getChunkPos();
            loadingChuckTicks = 5*20;
        }
    }
    
    
    /* TODO maybe add accessors to avoid having to tick twice
    private void superTick(){
        if (!this.shot) {
            this.emitGameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.shot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.shouldLeaveOwner();
        }
    }*/
    
    private Vec3d collisionPos = null;
    
    @Override
    public void tick() {
        loadChunk();
        Entity entity = this.getOwner();
        if (this.getWorld().isClient || (entity == null || !entity.isRemoved()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
            //super.tick(); //TODO use a bunch of accessor for the supertick maybe
            if (this.isBurning()) {
                this.setOnFireFor(1);
            }
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }

            this.checkBlockCollision();
            Vec3d vec3d = this.getVelocity();
            double d = this.getX() + vec3d.x;
            double e = this.getY() + vec3d.y;
            double f = this.getZ() + vec3d.z;
            ProjectileUtil.setRotationFromVelocity(this, 0.2F);
            float h;
            if (this.isTouchingWater()) {
                for (int i = 0; i < 4; i++) {
                    float g = 0.25F;
                    this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
                }

                h = 0.8f;
            } else {
                h = this.getDrag();
            }
            this.setVelocity(vec3d.add(vec3d.normalize().multiply(this.accelerationPower)).multiply(h));


            particleAnimation(d, e, f);

            this.setPosition(d, e, f);
        } else {
            this.discard();
        }
    }
    /*@Override
    public void tick() {
        loadChunk();
        particleAnimation(this.getX()+this.getVelocity().getX(), this.getY()+this.getVelocity().getY(), this.getZ()+this.getVelocity().getZ());
        super.tick();
        //TODO if this works port it overo to 1.21
        Entity entity = this.getOwner();
        if (this.world.isClient || (entity == null || !entity.isRemoved()) && this.world.isChunkLoaded(this.getBlockPos())) {
            super.tick();
            if (this.isBurning()) {
                this.setOnFireFor(1);
            }

            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }

            this.checkBlockCollision();
            Vec3d vec3d = this.getVelocity();
            double d = this.getX() + vec3d.x;
            double e = this.getY() + vec3d.y;
            double f = this.getZ() + vec3d.z;
            ProjectileUtil.setRotationFromVelocity(this, 0.2F);
            float g = this.getDrag();
            if (this.isTouchingWater()) {
                for (int i = 0; i < 4; i++) {
                    float h = 0.25F;
                    this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
                }

                g = 0.8F;
            }

            this.setVelocity(vec3d.add(this.powerX, this.powerY, this.powerZ).multiply(g));
            particleAnimation(d,e,f);
            this.setPosition(d, e, f);
        } else {
            this.discard();
        }
    }*/

    //pal vortex minecraft:flame ~ ~ ~ 1 0.01 0.8 0.1 5 3 10 false 3
    /**
     * Spawns the particle effects behind the meteor*/
    public void particleAnimation(double d, double e, double f){
        this.getWorld().addParticle(ParticleTypes.FLASH, true, d, e + 0.5, f, 0.0, 0.0, 0.0);
        this.getWorld().addParticle(ParticleTypes.EXPLOSION, true, d, e + 0.5, f, 0.0, 0.0, 0.0);

        if(this.getWorld().isClient()){
            return;
        }

        ///pal animatedcircle minecraft:soul_fire_flame ~ ~ ~ 100 2 0 3.14 false false true 2.0 0.0 0.0 0.0 0.0 0.0 5

        AnimatedCircleEffect circle = AnimatedCircleEffect
                .builder((ServerWorld) this.getWorld(), ParticleTypes.FLAME, new Vec3d(d,e,f))
                .particles(100).radius(this.getSize()).wholeCircle(false).maxAngle(3.14)
                .forced(true)
                .enableRotation(true).angularVelocityX(2)
                .build();

        circle.setIterations(10);
        circle.run();
    }


    /** Makes this entity explode without creating any structures on impact
     * and then discards this entity*/
    public void detonateSimple(){
        ExplosionBehavior explosionBehavior = new ExplosionBehavior();

        ExplosionBehavior safeExplosion = new ExplosionBehavior() {
            @Override
            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                return Optional.of(Blocks.BEDROCK.getBlastResistance());
            }
        };
        //Only needed for the damage source
        Explosion e = new Explosion(this.getWorld(), this, this.getX(), this.getY(), this.getZ(), this.getSize(), true, Explosion.DestructionType.DESTROY);

        if(isScatterMeteor()){
            if(Config.SCATTER_METEOR_GRIEFING){
                
                this.getWorld().createExplosion(this, DamageSource.explosion(e), explosionBehavior, this.getX(), this.getY(), this.getZ(), this.getSize(), true, Explosion.DestructionType.DESTROY);
            }else{
                this.getWorld().createExplosion(this, DamageSource.explosion(e), safeExplosion, this.getX(), this.getY(), this.getZ(), this.getSize(), false, Explosion.DestructionType.DESTROY);
            }
            this.discard();
            return;
        }

        if(Config.METEOR_GRIEFING){
            //TODO add custom  explosion source type
            this.getWorld().createExplosion(this, DamageSource.explosion(e), explosionBehavior, this.getX(), this.getY(), this.getZ(), this.getSize(), true, Explosion.DestructionType.DESTROY);
        }else{
            this.getWorld().createExplosion(this, DamageSource.explosion(e), safeExplosion, this.getX(), this.getY(), this.getZ(), this.getSize(), false, Explosion.DestructionType.DESTROY);
        }
        //entity.getWorld().addParticle(ParticleTypes.FLASH, pos.getX(), pos.getY(), pos.getZ(), 0,0,0);
        this.discard();
    }

    /** Like {@link #detonateSimple()} but will also spawn the structure of the meteor*/
    public void detonateWithStructure(){
        detonateSimple();
        if(!this.getWorld().isClient()){

            //If the dimension is even lower than 2, just spawn one block
            if(this.getSize() < 2){
                int r = this.getWorld().getRandom().nextBetween(1,3);
                if(r == 1){
                    this.getWorld().setBlockState(new BlockPos(collisionPos), OMMBlocks.METEORIC_ROCK.getDefaultState());
                }else if(r == 2){
                    this.getWorld().setBlockState(new BlockPos(collisionPos), Blocks.SMOOTH_BASALT.getDefaultState());
                }else{
                    this.getWorld().setBlockState(new BlockPos(collisionPos), Blocks.BLACKSTONE.getDefaultState());
                }
                return;
            }

            BlockPos m_pos_offset = new BlockPos(-1, -2, -1);
            //Checks for at most 5 blocks of Air below where the meteor should spawn, which could be a result of the explosion

            StructurePlacerAPI placer =
                    new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("small/small_meteor_0"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
            //between 2 and 5 (inclusive) the meteor is considered small
            if(this.getSize() <= Config.MAX_SMALL_METEOR_SIZE){
                int r = this.getWorld().getRandom().nextBetween(1,3);
                if(r == 1){
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("small/small_meteor_0"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                }else if(r==2){
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("small/small_meteor_1"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                }else{
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("small/small_meteor_2"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                }

                placer.loadStructure();
                return;
            }

            if(this.getSize() <= Config.MAX_MEDIUM_METEOR_SIZE){
                m_pos_offset = new BlockPos(-2, -3, -3);

                int r = this.getWorld().getRandom().nextBetween(1,19);

                if(r == 9){
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("medium/medium_meteor_99"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                    placer.loadStructure();
                    return;
                }
                r = this.getWorld().getRandom().nextBetween(1,3);
                if(r == 1){
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("medium/medium_meteor_0"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                }else if(r==2){
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("medium/medium_meteor_1"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                }else{
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("medium/medium_meteor_2"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                }

                placer.loadStructure();
            }

            if(this.getSize() <= Config.MAX_BIG_METEOR_SIZE){
                m_pos_offset = new BlockPos(-4, -6, -3);

                int r = this.getWorld().getRandom().nextBetween(1,10);

                if(r == 9){
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("big/big_meteor_cat"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                    placer.loadStructure();
                    return;
                }
                r = this.getWorld().getRandom().nextBetween(1,3);
                if(r == 1){
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("big/big_meteor_0"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                }else if(r==2){
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("big/big_meteor_1"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                }else{
                    placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("big/big_meteor_2"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
                }

                placer.loadStructure();
            }

            //If it's not in the sizes above, then it's a huge one:
            //TODO maybe later make a better calculation of like the direction the metor is travelling in to make it better embed into the terrain
            m_pos_offset = new BlockPos(-4, -10, -3);

            int r = this.getWorld().getRandom().nextBetween(1,3);

            if(r == 1){
                placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("huge/huge_meteor_0"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
            }else if(r==2){
                placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("huge/huge_meteor_1"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
            }else{
                placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("huge/huge_meteor_2"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
            }

            placer.loadStructure();



            //TODO read the filenames of the files of the /structure/ folder thing and check the folders that have like small medium big ecc
            // maybe in a future update
            /*StructurePlacerAPI placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("proto_meteor"), new BlockPos(collisionPos), BlockMirror.NONE, BlockRotation.NONE, false, 1f, new BlockPos(0, 0, 0));
            placer.loadStructure();*/

        }
    }

    /**This will detonate the meteor with an explosion like {@link #detonateSimple()}
     * but will also spawn other meteors based on the size of this meteor.
     *
     * Meteors will be smaller and be oriented randomly from that point on, but will still go down.
     * */
    public void detonateScatter(){
        if(this.getWorld().isClient() || this.getSize() <= 1){
            return;
        }

        //Can generate a minimum of 1 new meteor up to a number equal half of the size of this meteor
        int scatter_into = this.getWorld().getRandom().nextBetween(1, Math.max(this.getSize()/2, 1));
        //this is used to determine the size of the new meteors, which will be smaller than the original
        //each new meteor is going to take up some of the "mass" of the parent one, leaving the rest for the next one
        //and so on.
        int remainingSize = this.getSize()/2+1;

        List<MeteorProjectileEntity> newMeteors = new ArrayList<>();
        for(int i = 0; i<scatter_into; i++){
            //Gets a random number between 1 and the remaining size, making sure to leave at least one size for each new meteor yet to generate)
            int size =  this.getWorld().getRandom().nextBetween(1, Math.max(remainingSize-(scatter_into-i), 1));
            MeteorProjectileEntity m = getDownwardsMeteor(this.getPos(), (ServerWorld) this.getWorld(), 1, 10+this.getSize() /2, this.getPos().getY(), size, size, false);
            m.setScatterMeteor(true);
            newMeteors.add(m);
        }

        this.detonateSimple();

        newMeteors.forEach( meteorProjectileEntity -> this.getWorld().spawnEntity(meteorProjectileEntity));

    }

    /// This is the main method which does the meteor stuff on impact
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        BlockState state = this.getWorld().getBlockState(blockHitResult.getBlockPos());

        //It also registers Air blocks as a collision so we need to avoid such cases
        if(!state.isAir()){
            //If bypass leaves is false skip directly to the other code
            if(Config.SHOULD_BYPASS_LEAVES && state.isIn(BlockTags.LEAVES)){
                //Early return so the rest of the code doesn't run if the meteor hits a leaves block and the config option is there
                return;
            }
            collisionPos = Vec3d.ofCenter(getBlockPos());
            //this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 10, World.ExplosionSourceType.NONE);

            if(this.isScatterMeteor()){
                if(Config.SCATTER_METEOR_STRUCTURE){
                    this.detonateWithStructure();
                }else{
                    this.detonateSimple();
                }
                return;
            }

            if(Config.METEOR_STRUCTURE){
                this.detonateWithStructure();
            }else{
                this.detonateSimple();
            }

        }
    }

    /**Returns true if this meteor is classified as huge, as in bigger than the biggest "big" size*/
    public boolean isHuge(){
        return this.getSize() > Config.MAX_BIG_METEOR_SIZE;
    }

    /**
     * Gets a meteor object to be spawned in, with a velocity oriented dowards and a spawn position already set up
     * */
    public static MeteorProjectileEntity getDownwardsMeteor(Vec3d originPos, ServerWorld world, int min_spawn_d, int max_spawn_d, double spawn_height, int min_size, int max_size, boolean homing){
        MeteorProjectileEntity meteor = new MeteorProjectileEntity(world);

        //The invert is to also have a chance at having negative coordinates, otherwise they would always be positive
        int invert_x = 1;
        if(world.getRandom().nextBoolean()){
            invert_x = -1;
        }

        int invert_y = 1;
        if(world.getRandom().nextBoolean()){
            invert_y = -1;
        }

        meteor.setPos(originPos.getX()+world.getRandom().nextBetween(min_spawn_d, max_spawn_d)*invert_x,
                spawn_height,
                originPos.getZ()+world.getRandom().nextBetween(min_spawn_d, max_spawn_d)*invert_y
        );

        invert_x = 1;
        if(world.getRandom().nextBoolean()){
            invert_x = -1;
        }

        invert_y = 1;
        if(world.getRandom().nextBoolean()){
            invert_y = -1;
        }

        meteor.setSize(world.getRandom().nextBetween(Math.max(0, min_size), Math.min(50, max_size)));

        //The multiply is necessary in 1.19.2 beacuse of the drag, which i can't apparently remove withou messing everything up
        meteor.setVelocity(new Vec3d((world.getRandom().nextFloat()/2)*invert_x, -1.0f+world.getRandom().nextFloat(), (world.getRandom().nextFloat()/2)*invert_y).multiply(2));

        if(homing){
            //TODO maybe just go with 1,1,1 as velocity multiplier
            meteor.setVelocity(originPos.subtract(meteor.getPos()).normalize().multiply(meteor.getVelocity().getX(), meteor.getVelocity().getY()*-1, meteor.getVelocity().getZ()));
        }

        return meteor;
    }

    public boolean isScatterMeteor() {
        return isScatterMeteor;
    }

    public void setScatterMeteor(boolean scatterMeteor) {
        isScatterMeteor = scatterMeteor;
    }
}
