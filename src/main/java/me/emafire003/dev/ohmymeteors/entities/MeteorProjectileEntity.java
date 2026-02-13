package me.emafire003.dev.ohmymeteors.entities;

import com.google.common.annotations.VisibleForTesting;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.blocks.OMMBlocks;
import me.emafire003.dev.ohmymeteors.compat.flan.FlanCompat;
import me.emafire003.dev.ohmymeteors.compat.yawp.YawpCompat;
import me.emafire003.dev.ohmymeteors.events.MeteorSpawnEvent;
import me.emafire003.dev.ohmymeteors.config.Config;
import me.emafire003.dev.ohmymeteors.util.ExplosionUtils;
import me.emafire003.dev.ohmymeteors.util.MeteorUtils;
import me.emafire003.dev.structureplacerapi.StructurePlacerAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.emafire003.dev.ohmymeteors.OhMyMeteors.METEOR_STRUCTURES;

/**
 * The projectile entity that gets spawned as a meteor.
 * Upon hitting a block which is not air, it will execute the on-hit actions
 * such as creating an explosion and spawning the structure of blocks of the meteor thing*/
public class MeteorProjectileEntity extends ExplosiveProjectileEntity {

    private static final TrackedData<Integer> SIZE = DataTracker.registerData(MeteorProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final ChunkTicketType<Vec3i> METEOR_CHUCK_TICKET = ChunkTicketType.create("meteor", Vec3i::compareTo, 5*20);

    /// Aka a meteor that is a result of the {@link #detonateScatter()} method
    protected boolean isScatterMeteor = false;

    /// Weather or not the meteor should be announced in chat (used for example in the meteor showers)
    protected boolean isSilenced = false;

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
                    world.getChunkManager().addTicket(METEOR_CHUCK_TICKET,  this.getChunkPos(), 3, this.getBlockPos());
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

            world.getChunkManager().addTicket(METEOR_CHUCK_TICKET,  this.getChunkPos(), 2, this.getBlockPos());
            currentlyLoadedChunk = this.getChunkPos();
            loadingChuckTicks = 5*20;
        }
    }

    @Override
    public void tick() {
        loadChunk();
        Entity entity = this.getOwner();
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

            /*ParticleEffect particleEffect = this.getParticleType();
            if (particleEffect != null) {
                this.getWorld().addParticle(particleEffect, d, e + 0.5, f, 0.0, 0.0, 0.0);
            }*/
            particleAnimation(d, e, f);

            this.setPosition(d, e, f);
        } else {
            this.discard();
        }
    }

    //pal vortex minecraft:flame ~ ~ ~ 1 0.01 0.8 0.1 5 3 10 false 3
    /**
     * Spawns the particle effects behind the meteor*/
    public void particleAnimation(double d, double e, double f){
        this.getWorld().addParticle(ParticleTypes.FLASH, Config.USE_FORCED_PARTICLES, d, e + 0.5, f, 0.0, 0.0, 0.0);
        this.getWorld().addParticle(ParticleTypes.EXPLOSION, Config.USE_FORCED_PARTICLES, d, e + 0.5, f, 0.0, 0.0, 0.0);

        if(this.getWorld() instanceof ServerWorld world){
            world.getPlayers().forEach(p -> {
                world.spawnParticles(p, ParticleTypes.FLAME, Config.USE_FORCED_PARTICLES, d,e,f, 30+this.getSize()*5, 0.02+ (double) this.getSize() /100, 0.02+ (double) this.getSize() /100, 0.02+ (double) this.getSize() /100, 0.1);
                world.spawnParticles(p, ParticleTypes.SMOKE, Config.USE_FORCED_PARTICLES, d,e,f, 30+this.getSize()*5, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.1);
                world.spawnParticles(p, ParticleTypes.CAMPFIRE_COSY_SMOKE, Config.USE_FORCED_PARTICLES, d,e,f, 10+this.getSize()*2, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.02+(double) this.getSize()/100, 0.1);

            });
        }
    }

    /** The sphere explosion is a little weaker than the vanilla one, so adjustment may be needed to have a niceer effect*/
    private int sphereExplosionAdjuster(){
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
        ExplosionBehavior explosionBehavior = new ExplosionBehavior();

        ExplosionBehavior safeExplosion = new ExplosionBehavior() {
            @Override
            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                return Optional.of(Blocks.BEDROCK.getBlastResistance());
            }
        };

        if(isScatterMeteor()){
            if(Config.SCATTER_METEOR_GRIEFING){
                if(Config.USE_BETTER_EXPLOSIONS){
                    ExplosionUtils.createExplosion(this.getWorld(), this, this.getDamageSources().explosion(this, this), explosionBehavior, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER+sphereExplosionAdjuster(), true, World.ExplosionSourceType.TNT);
                }else{
                    this.getWorld().createExplosion(this, this.getDamageSources().explosion(this, this), explosionBehavior, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER, true, World.ExplosionSourceType.TNT);
                }
            }else{
                if(Config.USE_BETTER_EXPLOSIONS){
                    ExplosionUtils.createExplosion(this.getWorld(), this, this.getDamageSources().explosion(this, this), safeExplosion, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER+sphereExplosionAdjuster(), false, World.ExplosionSourceType.TNT);
                }else{
                    this.getWorld().createExplosion(this, this.getDamageSources().explosion(this, this), safeExplosion, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER, false, World.ExplosionSourceType.TNT);
                }
            }

            if(Config.ANNOUNCE_METEOR_SPAWN && !this.isSilenced()){
                if(Config.ANNOUNCE_LOCATION){
                    String meteorPos = this.getBlockPos().getX() + " x, " + this.getBlockPos().getZ() + " z!";
                    this.getWorld().getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("message.ohmymeteors.meteor_impacted.localized", meteorPos).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
                }
            }

            this.discard();
            return;
        }

        if(Config.METEOR_GRIEFING){
            if(Config.USE_BETTER_EXPLOSIONS){
                ExplosionUtils.createExplosion(this.getWorld(), this, this.getDamageSources().explosion(this, this), explosionBehavior, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER+sphereExplosionAdjuster(), true, World.ExplosionSourceType.TNT);
            }else {
                this.getWorld().createExplosion(this, this.getDamageSources().explosion(this, this), explosionBehavior, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER, true, World.ExplosionSourceType.TNT);
            }
        }else{
            this.getWorld().createExplosion(this, this.getDamageSources().explosion(this, this), safeExplosion, this.getPos(), this.getSize()+Config.EXPLOSION_POWER_MODIFIER, false, World.ExplosionSourceType.TNT);
        }

        if(!this.getWorld().isClient()){
            ((ServerWorld)this.getWorld()).getPlayers().forEach(serverPlayerEntity -> {
                //If it should play a sound for every player, do it, unless the player is close enough to the original one
                if(Config.GLOBAL_EXPLOSION_SOUND && (serverPlayerEntity.getPos().distanceTo(this.getPos()) > 60)){
                    serverPlayerEntity.playSoundToPlayer(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 0.5f, 0.8f);
                }else if(Config.AREA_EXPLOSION_SOUND){
                    double dist = serverPlayerEntity.getPos().distanceTo(this.getPos());
                    if(dist < Config.AREA_EXPLOSION_SOUND_RADIUS && dist > 60){
                        serverPlayerEntity.playSoundToPlayer(SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 0.5f, 0.8f);
                    }
                }
                ((ServerWorld)this.getWorld()).spawnParticles(serverPlayerEntity, ParticleTypes.EXPLOSION_EMITTER, true, this.getX(), this.getY(), this.getZ(), 1, 0.1, 0.1, 0.1, 0.1);
            });
        }

        if(Config.ANNOUNCE_METEOR_SPAWN && !this.isSilenced()){
            if(Config.ANNOUNCE_LOCATION){
                String meteorPos = this.getBlockPos().getX() + " x, " + this.getBlockPos().getZ() + " z!";
                this.getWorld().getPlayers().forEach(player -> player.sendMessage(Text.literal(OhMyMeteors.PREFIX).append(Text.translatable("message.ohmymeteors.meteor_impacted.localized", meteorPos).formatted(Formatting.RED)), Config.ACTIONBAR_ANNOUNCEMENTS));
            }
        }
        //entity.getWorld().addParticle(ParticleTypes.FLASH, pos.getX(), pos.getY(), pos.getZ(), 0,0,0);
        this.discard();
    }


    /** Like {@link #detonateSimple()} but will also spawn the structure of the meteor*/
    public void detonateWithStructure(){
        detonateSimple();
        if(!this.getWorld().isClient()){
            StructurePlacerAPI placer = getPlacer();
            //it means the meteor was too small for the structure, a single block has been placed instead
            if(placer == null){
                return;
            }
            placer.loadStructure();
        }
    }

    /** Like {@link #detonateWithStructure()} but will only replace air blocks*/
    public void detonateWithStructureOnlyAir(){
        detonateSimple();
        if(!this.getWorld().isClient()){
            StructurePlacerAPI placer = getPlacer();
            //it means the meteor was too small for the structure, a single block has been placed instead
            if(placer == null){
                return;
            }
            placer.setOnlyReplaceTaggedBlocks(true, BlockTags.AIR);
            placer.loadStructure();
        }
    }

    /**If the meteor is micro size (<2) will just spawn a block and return null*/
    public StructurePlacerAPI getPlacer(){
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
            return null;
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

            if(Config.ONLY_REPLACE_AIR){
                placer.setOnlyReplaceTaggedBlocks(true, BlockTags.AIR);
            }

            return placer;
        }

        if(this.getSize() <= Config.MAX_MEDIUM_METEOR_SIZE){
            Identifier tobeplaced = getStructureToPlace("medium");
            m_pos_offset = getOffset(new BlockPos(-2, +1, -2), tobeplaced);


            placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(),
                    tobeplaced,
                    this.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);
            return placer;
        }

        if(this.getSize() <= Config.MAX_BIG_METEOR_SIZE){
            Identifier tobeplaced = getStructureToPlace("big");
            m_pos_offset = getOffset(new BlockPos(-3, 0, -3), tobeplaced);

            //m_pos_offset = new BlockPos(-4, -6, -3);

            placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(),
                    tobeplaced,
                    this.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);

            return placer;
        }

        //If it's not in the sizes above, then it's a huge one:

        Identifier tobeplaced = getStructureToPlace("huge");
        m_pos_offset = getOffset(new BlockPos(-4, 0, -4), tobeplaced);
        //m_pos_offset = new BlockPos(-4, -10, -3);

        placer = new StructurePlacerAPI((StructureWorldAccess) this.getWorld(),
                tobeplaced,
                this.getBlockPos(), BlockMirror.NONE, BlockRotation.NONE, false, 1f, m_pos_offset);

        return placer;
    }

    /** Returms the offset of the meteor structure, aka how much it's going to be embedded in the terrain.
     * it's based on its size and the distance from the terrain that would be left from the imapct point, and
     * the direction of the meteor
     *
     * @param m_pos_offset The staring offset
     * @param tobeplaced the id of the meteor that is going to be placed
     * @return the blockpos offset
     */
    private BlockPos getOffset(BlockPos m_pos_offset, Identifier tobeplaced){
        //If it's an error structure it should be as visible as possible
        if(tobeplaced.getPath().startsWith("error")){
            return m_pos_offset.add(0, 5, 0);
        }

        Vec3d size_factors = Vec3d.of(StructurePlacerAPI.getTemplatePreview((ServerWorld) this.getWorld(), tobeplaced).get().getSize());
        //size_factors = size_factors.multiply(0.2, -0.2, 0.2);
        //size_factors = new Vec3d(size_factors.getZ(), size_factors.getY(), size_factors.getX());

        m_pos_offset = BlockPos.ofFloored(this.getVelocity()).add(m_pos_offset);

        /*if(false && this.getPitch() < 25 && !(tobeplaced.getPath().startsWith("big") || tobeplaced.getPath().startsWith("huge"))){
            OhMyMeteors.LOGGER.error("Meteor fell diagonally, embedding laterally, the pitch is: " + this.getPitch());
            m_pos_offset = m_pos_offset.add(((int) this.getVelocity().getX()*2), 0,  ((int) this.getVelocity().getZ()*2));
        }else{

        }*/

        //This is the one used for "small" meteors
        BlockPos nonair_pos = BlockPos.ofFloored(this.getPos()).add(0, -(int) size_factors.getY()/3, 0);
        if(tobeplaced.getPath().startsWith("big")){
            nonair_pos = BlockPos.ofFloored(this.getPos()).add(0, -(int) size_factors.getY()/10, 0);
            if(this.getPitch() < 27){
                nonair_pos.add((int) (this.getVelocity().getX()*5), 0, (int) (this.getVelocity().getZ()*5));
            }
        }

        if(tobeplaced.getPath().startsWith("medium")){
            if(this.getPitch() < 27){
                nonair_pos.add((int) (this.getVelocity().getX()*2), 0, (int) (this.getVelocity().getZ()*2));
            }
        }
        if(tobeplaced.getPath().startsWith("huge")){
            nonair_pos = BlockPos.ofFloored(this.getPos()).add(0, -(int) size_factors.getY()/37, 0);
        }
        BlockState state = this.getWorld().getBlockState(nonair_pos);
        int dist_to_floor = 0;
        while(state.isAir() || state.isOf(Blocks.FIRE)){
            nonair_pos = nonair_pos.down();
            state = this.getWorld().getBlockState(nonair_pos);
            dist_to_floor++;
        }

        //get the distance to floor, get the height and stuff and the place half of it underground?

        /*+(size_factors.getY()/2)*/
        m_pos_offset = m_pos_offset.add(0, - dist_to_floor, 0);
        return m_pos_offset;
    }

    /**
     * Returns the ID of the structure that is going to be spawned based the size class
     *
     * @param sizeClass The size of the meteors that we want to spawn, can be "small" "medium" "big" "huge"
     * */
    public Identifier getStructureToPlace(String sizeClass){
        AtomicBoolean hasSpecial = new AtomicBoolean(false);

        if(METEOR_STRUCTURES.isEmpty() && !this.getWorld().isClient()){
            OhMyMeteors.reInitStructures((ServerWorld) this.getWorld());
        }

        //In case there was a problem and the only meteor spawnable is that one
        if(METEOR_STRUCTURES.size() == 1 && METEOR_STRUCTURES.getFirst().getPath().equals("error")){
            return METEOR_STRUCTURES.getFirst();
        }

        List<Identifier> structs = METEOR_STRUCTURES.stream().filter(identifier -> {

            if(!identifier.getPath().startsWith(sizeClass)){
                return false;
            }

            if (!hasSpecial.get()) { //saves on checks
                //This allows me to see if this size has at least a special meteor
                if (identifier.getPath().startsWith(sizeClass+"/special")) {
                    hasSpecial.set(true);
                    return true;
                }
            }

            return true;
        }).toList();

        if (structs.isEmpty()){
            OhMyMeteors.LOGGER.error("The list of structures for size class '" + sizeClass + "' is empty! Check that your structures are valid ones!");
            structs = List.of(OhMyMeteors.getIdentifier("error"));
        }

        Identifier structure_id = structs.get(this.getWorld().getRandom().nextBetween(0,structs.size()-1));
        //This is to prevent special structures from spawning "before" they should
        //TODO make sure it's not too much of a performance issue
        while(structure_id.getPath().startsWith(sizeClass+"/special")){
            structure_id = structs.get(this.getRandom().nextBetween(0,structs.size()-1));
        }

        //If there is at least a special meteor structure, and the chance is hit, the structs list should only have those
        if(hasSpecial.get()){
            int i = random.nextBetween(0, Config.SPECIAL_METEORS_CHANCE);
            if(i == 1){
                List<Identifier> specials = structs.stream().filter(id -> id.getPath().startsWith(sizeClass+"/special")).toList();
                structure_id = specials.get(this.getRandom().nextBetween(0,specials.size()-1));
            }
        }
        return structure_id;
    }

    /**This will detonate the meteor with an explosion like {@link #detonateSimple()}
     * but will also spawn other meteors based on the size of this meteor.
     * <p>
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
            MeteorProjectileEntity m = MeteorUtils.getDownwardsMeteor(this.getPos(), (ServerWorld) this.getWorld(), 1, 10+this.getSize() /2, this.getPos().getY(), size, size, false);
            m.setScatterMeteor(true);
            newMeteors.add(m);
        }

        this.detonateSimple();

        newMeteors.forEach( meteorProjectileEntity -> this.getWorld().spawnEntity(meteorProjectileEntity));

    }

    /// Since it likes to explode more times instead of just one, i'll put this here so it won't explode twice
    private boolean exploded = false;
    private int travelledBlocks = 0;
    private Vec3d explosionPos = null;

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
            if(FabricLoader.getInstance().isModLoaded("flan") && !this.getWorld().isClient()){
                if(!FlanCompat.canSpawnHere(null, blockHitResult.getBlockPos())){
                    this.discard();
                    OhMyMeteors.LOGGER.warn("A meteor had entered a space protected by a Flan claim, it has been discarded!");
                    return;
                }
            }

            if(FabricLoader.getInstance().isModLoaded("yawp") && !this.getWorld().isClient()){
                if(!YawpCompat.canSpawnHere((ServerWorld) this.getWorld(), blockHitResult.getBlockPos())){
                    this.discard();
                    OhMyMeteors.LOGGER.warn("A meteor had entered a space protected by YetAnotherWorldProtector 'EXPLOSION_ENTITY' flag, it has been discarded!");
                    return;
                }
            }

            //Checks if the block should be bypassed or not
            if(state.isIn(OhMyMeteors.METEOR_BYPASSES)){
                //Early return so the rest of the code doesn't run if the meteor hits a leaves block and the config option is there
                if(state.isIn(OhMyMeteors.METEOR_BYPASSES_AND_DESTROY)){
                    Box box = this.getBoundingBox();
                    BlockPos.stream(box).forEach((blockPos -> {
                        if(this.getWorld().getBlockState(blockPos).isIn(OhMyMeteors.METEOR_BYPASSES_AND_DESTROY)){
                            this.getWorld().setBlockState(blockPos, Blocks.AIR.getDefaultState());
                            this.getWorld().addParticle(ParticleTypes.EXPLOSION, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, 0,0 );
                        }
                    }));
                }
                return;
            }
            //this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 10, World.ExplosionSourceType.NONE);

            //this section makes the meteor entity appear as if it's going into the terrain, which is nicer instead of exploding as soon as the hitbox gets a block especially at high dimensions
            if(explosionPos == null){
                explosionPos = this.getPos();
            }
            travelledBlocks++;
            if(this.getSize()/2 > travelledBlocks){
                return;
            }

            this.setPos(explosionPos.x, explosionPos.y, explosionPos.z);

            this.discard(); //So it doesn't trigger again hitting the next block
            exploded = true;
            if(!this.getWorld().isClient()){
                if(this.isScatterMeteor()){
                    if(Config.SCATTER_METEOR_STRUCTURE){
                        if(Config.SCATTER_ONLY_REPALCE_AIR){
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

                if(Config.METEOR_STRUCTURE){
                    if(Config.ONLY_REPLACE_AIR){
                        this.detonateWithStructureOnlyAir();
                    }else{
                        this.detonateWithStructure();
                    }

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


    public boolean isScatterMeteor() {
        return isScatterMeteor;
    }

    public void setScatterMeteor(boolean scatterMeteor) {
        isScatterMeteor = scatterMeteor;
    }

    @Override
    public boolean shouldRender(double distance) {
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
}
