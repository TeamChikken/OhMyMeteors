package me.emafire003.dev.ohmymeteors.entities;

import com.google.common.annotations.VisibleForTesting;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.events.MeteorSpawnEvent;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.METEOR_CHUCK_TICKET;


/**
 * The projectile entity that gets spawned as a meteor.
 * Upon hitting a block which is not air, it will execute the on-hit actions
 * such as creating an explosion and spawning the structure of blocks of the meteor thing*/
public class MeteorProjectileEntity extends ExplosiveProjectileEntity {
    private static final TrackedData<Integer> SIZE = DataTracker.registerData(MeteorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);

    /// Aka a meteor that is a result of the {@link #detonateScatter()} method
    protected boolean isScatterMeteor = false;


    public MeteorProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(OMMEntities.METEOR_PROJECTILE_ENTITY, world);
        initialize();
    }

    public MeteorProjectileEntity(World world) {
        super(OMMEntities.METEOR_PROJECTILE_ENTITY, world);
        initialize();
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SIZE, 1);
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
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putInt("Size", this.getSize() - 1);
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.setSize(view.getInt("Size", 0) + 1);
        super.readCustomData(view);
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

    /**
     * Initializes the meteor with a random size upon creation of the meteor object.
     * Called along with the constructor method
     * */
    public void initialize() {
        Random random = this.getRandom();
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

    private int chunksLoaded = 0;

    /**Gets called every tick and makes sure that when the meteor travels through a chunk it is loaded*/
    public void loadChunk(){
        //Safety feature so the meteor despawns if it gets too high, for example using commands and such
        if(this.getY() > Config.METEOR_SPAWN_HEIGHT+50){
            this.discard();
        }
        //Every 100 seconds or every time the meteor enters a new chunk, the meteor loads the chunk it's in for 10 seconds or 200 ticks
        if(this.getWorld() instanceof ServerWorld world){
            if(loadingChuckTicks > 0){
                if(currentlyLoadedChunk == null || !currentlyLoadedChunk.equals(this.getChunkPos())){
                    world.getChunkManager().addTicket(METEOR_CHUCK_TICKET, this.getChunkPos(), 3);
                    currentlyLoadedChunk = this.getChunkPos();
                    loadingChuckTicks = 10*20;
                    chunksLoaded++;
                }
                //So it avoids loading chunks forever and slowing down the game
                if(chunksLoaded > 100){
                    OhMyMeteors.LOGGER.warn("Discarded meteor projectile at " + this.getPos() + " after having loaded too many chunks (" + chunksLoaded + ")");
                    this.discard();
                }
                loadingChuckTicks++;
                return;
            }

            world.getChunkManager().addTicket(METEOR_CHUCK_TICKET, this.getChunkPos(), 3);
            currentlyLoadedChunk = this.getChunkPos();
            loadingChuckTicks = 5*20;
        }
    }

    @Override
    public void tick() {
        loadChunk();

        Entity entity = this.getOwner();
        if (this.getWorld().isClient || (entity == null || !entity.isRemoved()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
            Vec3d vec3d;
            if (hitResult.getType() != HitResult.Type.MISS) {
                vec3d = hitResult.getPos();
            } else {
                vec3d = this.getPos().add(this.getVelocity());
            }

            ProjectileUtil.setRotationFromVelocity(this, 0.2F);
            this.setPosition(vec3d);
            this.tickBlockCollision();
            super.tick();
            if (this.isBurning()) {
                this.setOnFireFor(1.0F);
            }

            if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
                this.hitOrDeflect(hitResult);
            }

            //TODO might need to be just the position
            particleAnimation(vec3d.x, vec3d.y + (double)0.5F, vec3d.z);
        } else {
            this.discard();
        }

        /*Entity entity = this.getOwner();
        if (this.getWorld().isClient || (entity == null || !entity.isRemoved()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
            super.tick();
            if (this.isBurning()) {
                this.setOnFireFor(1.0F);
            }

            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.hitOrDeflect(hitResult);
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

                h = this.getDragInWater();
            } else {
                h = this.getDrag();
            }

            this.setVelocity(vec3d.add(vec3d.normalize().multiply(this.accelerationPower)).multiply(h));

            particleAnimation(d, e, f);

            this.setPosition(d, e, f);
        } else {
            this.discard();
        }*/
    }

    //pal vortex minecraft:flame ~ ~ ~ 1 0.01 0.8 0.1 5 3 10 false 3
    /**
     * Spawns the particle effects behind the meteor*/
    public void particleAnimation(double d, double e, double f){
        this.getWorld().addParticleClient(ParticleTypes.FLASH, Config.USE_FORCED_PARTICLES, true, d, e + 0.5, f, 0.0, 0.0, 0.0);
        this.getWorld().addParticleClient(ParticleTypes.EXPLOSION, Config.USE_FORCED_PARTICLES, true, d, e + 0.5, f, 0.0, 0.0, 0.0);

        if(this.getWorld() instanceof ServerWorld world){
            world.getPlayers().forEach(p -> {
                world.spawnParticles(p, ParticleTypes.FLAME, Config.USE_FORCED_PARTICLES, Config.USE_FORCED_PARTICLES,  d,e,f, 30+this.getSize()*5, 0.02+ (double) this.getSize() /100, 0.02+ (double) this.getSize() /100, 0.02+ (double) this.getSize() /100, 0.1);
                world.spawnParticles(p, ParticleTypes.SMOKE, Config.USE_FORCED_PARTICLES, Config.USE_FORCED_PARTICLES, d,e,f, 30+this.getSize()*5, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.1);
                world.spawnParticles(p, ParticleTypes.CAMPFIRE_COSY_SMOKE, Config.USE_FORCED_PARTICLES, Config.USE_FORCED_PARTICLES, d,e,f, 10+this.getSize()*2, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.1);

            });
        }
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

        if(isScatterMeteor()){
            if(Config.SCATTER_METEOR_GRIEFING){
                this.getWorld().createExplosion(this, this.getDamageSources().explosion(this, this), explosionBehavior, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER, true, World.ExplosionSourceType.TNT);
            }else{
                this.getWorld().createExplosion(this, this.getDamageSources().explosion(this, this), safeExplosion, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER, false, World.ExplosionSourceType.TNT);
            }
            this.discard();
            return;
        }

        if(Config.METEOR_GRIEFING){
            //TODO add custom  explosion source type
            this.getWorld().createExplosion(this, this.getDamageSources().explosion(this, this), explosionBehavior, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER, true, World.ExplosionSourceType.TNT);
        }else{
            this.getWorld().createExplosion(this, this.getDamageSources().explosion(this, this), safeExplosion, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER, false, World.ExplosionSourceType.TNT);
        }

        if(!this.getWorld().isClient()){
            ((ServerWorld)this.getWorld()).getPlayers().forEach(serverPlayerEntity -> {
                //If it should play a sound for every player, do it, unless the player is close enough to the original one
                if(Config.GLOBAL_EXPLOSION_SOUND && (serverPlayerEntity.getPos().distanceTo(this.getPos()) > 60)){
                    serverPlayerEntity.playSoundToPlayer(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.WEATHER, 0.5f, 0.8f);
                }else if(Config.AREA_EXPLOSION_SOUND){
                    double dist = serverPlayerEntity.getPos().distanceTo(this.getPos());
                    if(dist < Config.AREA_EXPLOSION_SOUND_RADIUS && dist > 60){
                        serverPlayerEntity.playSoundToPlayer(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.WEATHER, 0.5f, 0.8f);
                    }
                }
                ((ServerWorld)this.getWorld()).spawnParticles(serverPlayerEntity, ParticleTypes.EXPLOSION_EMITTER, Config.USE_FORCED_PARTICLES, Config.USE_FORCED_PARTICLES,  this.getX(), this.getY(), this.getZ(), 1, 0.1, 0.1, 0.1, 0.1);
            });
        }

        //entity.getWorld().addParticle(ParticleTypes.FLASH, pos.getX(), pos.getY(), pos.getZ(), 0,0,0);
        this.discard();
    }

    /** Like {@link #detonateSimple()} but will also spawn the structure of the meteor*/
    //TODO maybe later make a better calculation of like the direction the meteor is travelling in to make it better embed into the terrain
    public void detonateWithStructure(){
        //this.getWorld().getServer().sendMessage(Text.literal("the movement direction is: " + this.getMovementDirection() + "\n The velocity is: " + this.getVelocity()));
        detonateSimple();
        if(!this.getWorld().isClient()){

            //If the dimension is even lower than 2, just spawn one block
            if(this.getSize() < 2){
                int r = this.getRandom().nextBetween(1,3);
                if(r == 1){
                    this.getWorld().setBlockState(BlockPos.ofFloored(this.getPos()), OMMBlocks.METEORIC_ROCK.getDefaultState());
                }else if(r == 2){
                    this.getWorld().setBlockState(BlockPos.ofFloored(this.getPos()), Blocks.SMOOTH_BASALT.getDefaultState());
                }else{
                    this.getWorld().setBlockState(BlockPos.ofFloored(this.getPos()), Blocks.BLACKSTONE.getDefaultState());
                }
                return;
            }

            BlockPos m_pos_offset = BlockPos.ofFloored(this.getVelocity()).add(-1, 0, -1);//new BlockPos(-1, -2, -1);
            //Checks for at most 5 blocks of Air below where the meteor should spawn, which could be a result of the explosion

            StructurePlacerAPI placer =
                    new StructurePlacerAPI((StructureWorldAccess) this.getWorld(), OhMyMeteors.getIdentifier("small/small_meteor_0"), this.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);

            if(this.getSize() <= Config.MAX_SMALL_METEOR_SIZE){
                Identifier tobeplaced = getStructureToPlace("small");
                m_pos_offset = getOffset(new BlockPos(-1, 0, -1), tobeplaced);


                placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(),
                        tobeplaced,
                        this.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);

                placer.loadStructure();
                return;
            }

            if(this.getSize() <= Config.MAX_MEDIUM_METEOR_SIZE){
                Identifier tobeplaced = getStructureToPlace("medium");
                m_pos_offset = getOffset(new BlockPos(-2, 0, -2), tobeplaced);
                /*Vec3d size_factors = Vec3d.of(StructurePlacerAPI.getTemplatePreview((ServerWorld) this.getWorld(), tobeplaced).get().getSize());
                //size_factors = size_factors.multiply(0.2, -0.2, 0.2);
                //size_factors = new Vec3d(size_factors.getZ(), size_factors.getY(), size_factors.getX());

                m_pos_offset = BlockPos.ofFloored(this.getVelocity())
                        //.add(-2, -2, -2); //TODO this works!
                        //.add(-2, (int) - (size_factors.getY()/3), -2); this is meh
                        .add(-2, 0, -2);

                if(this.getPitch() < 27){
                    OhMyMeteors.LOGGER.debug("meteor fell diagonally, embedding laterally!");
                    //TODO maybe i should take into account the terrain type
                    m_pos_offset = m_pos_offset.add(((int) this.getVelocity().getX()*2), 0,  ((int) this.getVelocity().getZ()*2));
                }else{
                    BlockPos nonair_pos = BlockPos.ofFloored(this.getPos()).add(0, -(int) size_factors.getY()/3, 0);
                    BlockState state = this.getWorld().getBlockState(nonair_pos);
                    int dist_to_floor = 0;
                    while(state.isAir()){
                        nonair_pos = nonair_pos.down();
                        state = this.getWorld().getBlockState(nonair_pos);
                        dist_to_floor++;
                    }
                    //get the distance to floor, get the height and stuff and the place half of it underground?

                    m_pos_offset = m_pos_offset.add(0, (int) (- dist_to_floor/*+(size_factors.getY()/2) /*), 0);

                }*/

                placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(),
                        tobeplaced,
                        this.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);

                placer.loadStructure();
                return;
            }

            if(this.getSize() <= Config.MAX_BIG_METEOR_SIZE){
                Identifier tobeplaced = getStructureToPlace("big");
                m_pos_offset = getOffset(new BlockPos(-3, 0, -3), tobeplaced);

                //m_pos_offset = new BlockPos(-4, -6, -3);

                placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(),
                        tobeplaced,
                        this.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);

                placer.loadStructure();
                return;
            }

            //If it's not in the sizes above, then it's a huge one:

            Identifier tobeplaced = getStructureToPlace("huge");
            m_pos_offset = getOffset(new BlockPos(-4, 0, -4), tobeplaced);
            //m_pos_offset = new BlockPos(-4, -10, -3);

            placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(),
                    tobeplaced,
                    this.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);

            placer.loadStructure();
        }
    }

    private BlockPos getOffset(BlockPos m_pos_offset, Identifier tobeplaced){
        Vec3d size_factors = Vec3d.of(StructurePlacerAPI.getTemplatePreview((ServerWorld) this.getWorld(), tobeplaced).get().getSize());
        //size_factors = size_factors.multiply(0.2, -0.2, 0.2);
        //size_factors = new Vec3d(size_factors.getZ(), size_factors.getY(), size_factors.getX());

        m_pos_offset = BlockPos.ofFloored(this.getVelocity()).add(m_pos_offset);

        if(this.getPitch() < 27){
            OhMyMeteors.LOGGER.debug("meteor fell diagonally, embedding laterally!");
            //TODO maybe i should take into account the terrain type
            m_pos_offset = m_pos_offset.add(((int) this.getVelocity().getX()*2), 0,  ((int) this.getVelocity().getZ()*2));
        }else{
            BlockPos nonair_pos = BlockPos.ofFloored(this.getPos()).add(0, -(int) size_factors.getY()/3, 0);
            BlockState state = this.getWorld().getBlockState(nonair_pos);
            int dist_to_floor = 0;
            while(state.isAir()){
                nonair_pos = nonair_pos.down();
                state = this.getWorld().getBlockState(nonair_pos);
                dist_to_floor++;
            }
            //get the distance to floor, get the height and stuff and the place half of it underground?

            m_pos_offset = m_pos_offset.add(0, (int) (- dist_to_floor/*+(size_factors.getY()/2)*/), 0);

        }
        return m_pos_offset;
    }

    /**
     * Returns the ID of the structure that is going to be spawned based the size class
     *
     * @param sizeClass The size of the meteors that we want to spawn, can be "small" "medium" "big" "huge"
     * */
    public Identifier getStructureToPlace(String sizeClass){
        Stream<Identifier> structures = ((ServerWorld) this.getWorld()).getStructureTemplateManager().streamTemplates().filter(
                identifier -> identifier.getNamespace().equals(OhMyMeteors.MOD_ID)
        );

        AtomicBoolean hasSpecial = new AtomicBoolean(false);
        List<Identifier> structs = structures.filter(identifier -> {
            if (!hasSpecial.get()) { //saves on checks
                //This allows me to see if this size has at least a special meteor
                if (identifier.getPath().startsWith(sizeClass+"/special")) {
                    hasSpecial.set(true);
                    return true;
                }
            }

            return identifier.getPath().startsWith(sizeClass);
        }).toList();

        Identifier structure_id = structs.get(this.getRandom().nextBetween(0,structs.size()-1));

        //If there is at least a special meteor structure, and the chance is hit, the structs list should only have those
        if(hasSpecial.get()){
            int i = random.nextBetween(1, Config.SPECIAL_METEORS_CHANCE);
            if(i == 1){
                List<Identifier> specials = structs.stream().filter(id -> id.getPath().startsWith(sizeClass+"/special")).toList();
                structure_id = specials.get(this.getRandom().nextBetween(0,specials.size()-1));
            }
        }
        return structure_id;
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
        int scatter_into = this.getRandom().nextBetween(1, Math.max(this.getSize()/2, 1));
        //this is used to determine the size of the new meteors, which will be smaller than the original
        //each new meteor is going to take up some of the "mass" of the parent one, leaving the rest for the next one
        //and so on.
        int remainingSize = this.getSize()/2+1;

        List<MeteorProjectileEntity> newMeteors = new ArrayList<>();
        for(int i = 0; i<scatter_into; i++){
            //Gets a random number between 1 and the remaining size, making sure to leave at least one size for each new meteor yet to generate)
            int size =  this.getRandom().nextBetween(1, Math.max(remainingSize-(scatter_into-i), 1));
            MeteorProjectileEntity m = getDownwardsMeteor(this.getPos(), (ServerWorld) this.getWorld(), 1, 10+this.getSize() /2, this.getPos().getY(), size, size, false);
            m.setScatterMeteor(true);
            newMeteors.add(m);
        }

        this.detonateSimple();

        newMeteors.forEach( meteorProjectileEntity -> this.getWorld().spawnEntity(meteorProjectileEntity));

    }

    /// Since it likes to explode more times instead of just one, i'll put this here so it won't explode twice
    private boolean exploded = false;

    /// This is the main method which does the meteor stuff on impact
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if(exploded){
            return;
        }
        //TODO make sure it is like 1/3 of its size inside a block maybe
        super.onBlockHit(blockHitResult);
        BlockState state = this.getWorld().getBlockState(blockHitResult.getBlockPos());

        //It also registers Air blocks as a collision so we need to avoid such cases
        if(!state.isAir()){
            //If bypass leaves is false skip directly to the other code
            //TODO maybe this should work like a tag instead of being just leaves
            if(Config.SHOULD_BYPASS_LEAVES && state.isIn(BlockTags.LEAVES)){
                //Early return so the rest of the code doesn't run if the meteor hits a leaves block and the config option is there
                if(Config.SHOULD_DESTROY_LEAVES){
                    Box box = this.getBoundingBox();
                    BlockPos.stream(box).forEach((blockPos -> {
                        if(this.getWorld().getBlockState(blockPos).isIn(BlockTags.LEAVES)){
                            this.getWorld().setBlockState(blockPos, Blocks.AIR.getDefaultState());
                            this.getWorld().addParticleClient(ParticleTypes.EXPLOSION, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, 0,0 );
                        }
                    }));
                }
                return;
            }
            //this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 10, World.ExplosionSourceType.NONE);

            this.discard(); //So it doesn't trigger again hitting the next block
            exploded = true;
            if(!this.getWorld().isClient()){
                if(this.isScatterMeteor()){
                    if(Config.SCATTER_METEOR_STRUCTURE){
                        this.detonateWithStructure();
                        return;
                    }else{
                        this.detonateSimple();
                        return;
                    }
                }

                if(Config.METEOR_STRUCTURE){
                    this.detonateWithStructure();
                }else{
                    this.detonateSimple();
                }
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

        meteor.setVelocity((world.getRandom().nextFloat()/2)*invert_x, -1.0f*(world.getRandom().nextFloat()+Config.DOWNWARDS_SPEED_MODIFIER), (world.getRandom().nextFloat()/2)*invert_y);

        if(homing){
            //TODO maybe just go with 1,1,1 as velocity multiplier
            meteor.setVelocity(originPos.subtract(meteor.getPos()).normalize().multiply(meteor.getVelocity().getX(), meteor.getVelocity().getY()*-1, meteor.getVelocity().getZ()));
        }

        return meteor;
    }

    /**Spawns a meteor around a random alive online player*/
    public static void spawnMeteor(ServerWorld world, PlayerEntity p){

        if(p == null){
            //for some reason it won't detect that there is player online sometimes
            return;
        }
        MeteorProjectileEntity meteor = MeteorProjectileEntity.getDownwardsMeteor(p.getPos(), world.toServerWorld(),
                Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE, Config.METEOR_SPAWN_HEIGHT, Config.NATURAL_METEOR_MIN_SIZE, Config.NATURAL_METEOR_MAX_SIZE, Config.HOMING_METEORS);

        String message;

        if(Config.SPAWN_HUGE_METEORS){
            if(world.getRandom().nextBetween(0, Config.HUGE_METEOR_CHANCE) == 0){
                meteor = MeteorProjectileEntity.getDownwardsMeteor(p.getPos(), world.toServerWorld(),
                        Config.MIN_METEOR_SPAWN_DISTANCE, Config.MAX_METEOR_SPAWN_DISTANCE, Config.METEOR_SPAWN_HEIGHT, Config.MAX_BIG_METEOR_SIZE, Config.HUGE_METEOR_SIZE_LIMIT, Config.HOMING_METEORS);

                message = "message.ohmymeteors.meteor_spawned.huge";
            } else {
                //world mess is because it needs a final variable btw
                message = "message.ohmymeteors.meteor_spawned";
            }
        } else {
            message = "message.ohmymeteors.meteor_spawned";
        }

        if(Config.ANNOUNCE_METEOR_SPAWN){
            world.getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable(message).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
        }


        world.spawnEntity(meteor);
    }

    public boolean isScatterMeteor() {
        return isScatterMeteor;
    }

    public void setScatterMeteor(boolean scatterMeteor) {
        isScatterMeteor = scatterMeteor;
    }
}
